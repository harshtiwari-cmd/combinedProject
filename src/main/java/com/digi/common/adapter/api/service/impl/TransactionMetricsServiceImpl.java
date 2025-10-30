package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.TransactionMetricsService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.TransactionMetricsReq;
import com.digi.common.dto.ConfigDto;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.entity.RRmessage;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.mapper.ConfigMapper;
import com.digi.common.repository.JPAConfigRepository;
import com.digi.common.repository.JPARRmessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionMetricsServiceImpl implements TransactionMetricsService {

    private DecimalFormat decimalFormat = new DecimalFormat(AppConstant.RATE_FORMATE);

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private JPAConfigRepository jpaConfigRepository;

    @Autowired
    private JPARRmessageRepository rrMessageRepo;

    @Override
    public GenericResponse<Map> getTransactionMetrics(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request) {

        var response = new GenericResponse<Map>();
        ResultUtilVO resultUtilVO = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
        var serviceMap = new HashMap<String, Set<String>>();
        var rrList = new ArrayList<RRmessage>();
        var categoryTypes = new ArrayList<String>();
        List<String> servicesList = new ArrayList<>();
        var rrListLogin = new ArrayList<RRmessage>();
        try {

            if(((List<String>) request.getFilter().get("serviceTypes")).contains("ALL")) {
                //All servicesTypes
                ConfigDto configDto = configMapper.configToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelAndKey(unit, channel, "TOTAL_TXN_SERVICES"));
                servicesList = Arrays.stream(configDto.getValue().split(",")).map(String::trim).collect(Collectors.toList());
            } else {
                servicesList = (List<String>) request.getFilter().get("serviceTypes");
            }
            var configList = configMapper.configListToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelInAndKeyInAndStatus(unit, List.of(channel),
                                                                                                            servicesList, "ACT"));

            for (ConfigDto config : configList) {
                String key = config.getKey();
                String value = config.getValue();

                Set<String> valueSet = Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toSet());
                categoryTypes.addAll(Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList()));

                serviceMap.put(key, valueSet);
            }

            if (request.getType().equalsIgnoreCase(AppConstant.DATE_RANGE)) {
                SimpleDateFormat formatter = new SimpleDateFormat(AppConstant.DATE_FORMATE);
                Date startDate = formatter.parse(request.getStartDate());
                Date endDate = new Date(formatter.parse(request.getEndDate()).getTime() + 86399999);

                rrList = (ArrayList<RRmessage>) rrMessageRepo
                        .findByChannelIdInAndCategoryCodeInAndRequestDateBetweenOrderByDateCreatedDesc(
                                (List<String>) request.getFilter().get("channels"), categoryTypes, startDate, endDate);

				/* rrListLogin = (ArrayList<RRmessage>) rrMessageRepo
						.findByChannelIdInAndCategoryInAndRequestDateBetweenOrderByDateCreatedDesc(
						(List<String>) request.getFilter().get("channels"), List.of(AppConstant.LOGIN_CATEGORY_CODE), startDate, endDate); */
            } else {

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                Date endDate = calendar.getTime();

                calendar.add(Calendar.DAY_OF_YEAR, -7);
                Date startDate = calendar.getTime();

                rrList = (ArrayList<RRmessage>) rrMessageRepo
                        .findByChannelIdInAndCategoryCodeInAndRequestDateBetweenOrderByDateCreatedDesc(
                                (List<String>) request.getFilter().get("channels"), categoryTypes, startDate, endDate);

		        /* rrListLogin = (ArrayList<RRmessage>) rrMessageRepo
						.findByChannelIdInAndCategoryInAndRequestDateBetweenOrderByDateCreatedDesc(
						(List<String>) request.getFilter().get("channels"), List.of(AppConstant.LOGIN_CATEGORY_CODE), startDate, endDate); */
            }
            response.setData(generateResponseWithMaps(serviceMap, rrList, rrListLogin, servicesList));
        } catch (Exception e) {
            log.error("Exception in getTransactionMetrics service: {}", e);
            resultUtilVO = new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC);
        }

        response.setStatus(resultUtilVO);
        return response;
    }

    private Map<String, Object> generateResponseWithMaps(Map<String, Set<String>> serviceMap, List<RRmessage> rrList, List<RRmessage> rrListLogin, List<String> totalServices) {

        var response = new LinkedHashMap<String, Object>();
        var transactionAnalysis = new LinkedHashMap<String, Object>();
        response.put("transactionAnalysis", transactionAnalysis);

        int totalTransactions = rrList.size() + rrListLogin.size();
        transactionAnalysis.put("totalTransactions", totalTransactions);

        Map<String, LinkedHashMap<String, Object>> serviceMetricsMap = new LinkedHashMap<>();

        for (RRmessage message : rrList) {
            String serviceType = serviceMap.entrySet().stream()
                    .filter(entry -> entry.getValue().contains(message.getCategoryType())).map(Map.Entry::getKey)
                    .findFirst().orElse("Unknown");

            serviceMetricsMap.putIfAbsent(serviceType, new LinkedHashMap<>());
            Map<String, Object> serviceMetrics = serviceMetricsMap.get(serviceType);
            serviceMetrics.putIfAbsent("serviceType", serviceType);
            serviceMetrics.putIfAbsent("successCount", 0);
            serviceMetrics.putIfAbsent("failureCount", 0);
            serviceMetrics.putIfAbsent("channelBreakdown", new ArrayList<>());

            boolean isSuccess = message.getResponseCode().equals("000000"); // Assume "0" indicates success
            String countKey = isSuccess ? "successCount" : "failureCount";
            serviceMetrics.put(countKey, (int) serviceMetrics.get(countKey) + 1);

            List<Map<String, Object>> channelBreakdown = (List<Map<String, Object>>) serviceMetrics.get("channelBreakdown");
            Map<String, Object> channelMetrics = channelBreakdown.stream()
                    .filter(cb -> cb.get("channelId").equals(message.getChannelId())).findFirst().orElseGet(() -> {
                        Map<String, Object> newChannelMetrics = new LinkedHashMap<>();
                        newChannelMetrics.put("channelId", message.getChannelId());
                        newChannelMetrics.put("successCount", 0);
                        newChannelMetrics.put("failureCount", 0);
                        newChannelMetrics.put("failureReasons", new ArrayList<>());
                        channelBreakdown.add(newChannelMetrics);
                        return newChannelMetrics;
                    });
            channelMetrics.put(countKey, (int) channelMetrics.get(countKey) + 1);

            if (!isSuccess) {
                List<Map<String, Object>> failureReasons = (List<Map<String, Object>>) channelMetrics.get("failureReasons");
                String reason = message.getRrMessage();
                Map<String, Object> failureReason = failureReasons.stream()
                        .filter(fr -> fr.get("reason").equals(reason)).findFirst().orElseGet(() -> {
                            Map<String, Object> newFailureReason = new HashMap<>();
                            newFailureReason.put("reason", reason);
                            newFailureReason.put("count", 0);
                            failureReasons.add(newFailureReason);
                            return newFailureReason;
                        });
                failureReason.put("count", (int) failureReason.get("count") + 1);
            }
        }
        for (RRmessage message : rrListLogin) {
            String serviceType = serviceMap.entrySet().stream()
                    .filter(entry -> entry.getValue().contains(message.getCategoryCode())).map(Map.Entry::getKey)
                    .findFirst().orElse("Unknown");

            serviceMetricsMap.putIfAbsent(serviceType, new LinkedHashMap<>());
            Map<String, Object> serviceMetrics = serviceMetricsMap.get(serviceType);
            serviceMetrics.putIfAbsent("serviceType", serviceType);
            serviceMetrics.putIfAbsent("successCount", 0);
            serviceMetrics.putIfAbsent("failureCount", 0);
            serviceMetrics.putIfAbsent("channelBreakdown", new ArrayList<>());

            boolean isSuccess = (message.getResponseCode() != null ? message.getResponseCode().equals("000000") : false); // Assume "0" indicates success
            String countKey = isSuccess ? "successCount" : "failureCount";
            serviceMetrics.put(countKey, (int) serviceMetrics.get(countKey) + 1);
            List<Map<String, Object>> channelBreakdown = (List<Map<String, Object>>) serviceMetrics.get("channelBreakdown");
            Map<String, Object> channelMetrics = channelBreakdown.stream()
                    .filter(cb -> cb.get("channelId").equals(message.getChannelId())).findFirst().orElseGet(() -> {
                        Map<String, Object> newChannelMetrics = new LinkedHashMap<>();
                        newChannelMetrics.put("channelId", message.getChannelId());
                        newChannelMetrics.put("successCount", 0);
                        newChannelMetrics.put("failureCount", 0);
                        newChannelMetrics.put("failureReasons", new ArrayList<>());
                        channelBreakdown.add(newChannelMetrics);
                        return newChannelMetrics;
                    });
            channelMetrics.put(countKey, (int) channelMetrics.get(countKey) + 1);
            if (!isSuccess) {
                List<Map<String, Object>> failureReasons = (List<Map<String, Object>>) channelMetrics.get("failureReasons");
                String reason = message.getRrMessage() != null ? message.getRrMessage() : "GEN_ERR";
                Map<String, Object> failureReason = failureReasons.stream()
                        .filter(fr -> fr.get("reason").equals(reason)).findFirst().orElseGet(() -> {
                            Map<String, Object> newFailureReason = new HashMap<>();
                            newFailureReason.put("reason", reason);
                            newFailureReason.put("count", 0);
                            failureReasons.add(newFailureReason);
                            return newFailureReason;
                        });
                failureReason.put("count", (int) failureReason.get("count") + 1);
            }
        }
        List<Map<String, Object>> services = new ArrayList<>();
        for (Map<String, Object> serviceMetrics : serviceMetricsMap.values()) {
            int successCount = (int) serviceMetrics.get("successCount");
            int failureCount = (int) serviceMetrics.get("failureCount");
            int totalServiceTransactions = successCount + failureCount;
            serviceMetrics.put("successRate", decimalFormat.format((successCount / (double) totalServiceTransactions) * 100));
            serviceMetrics.put("failureRate", decimalFormat.format((failureCount / (double) totalServiceTransactions) * 100));
            services.add(serviceMetrics);
        }
        services.sort(Comparator.comparingInt(service -> totalServices.indexOf((String) service.get("serviceType"))));
        transactionAnalysis.put("services", services);
        return response;
    }

    @Override
    public GenericResponse<Map> getTransactionMetricsByPlatform(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request) {

        var response = new GenericResponse<Map>();
        ResultUtilVO resultUtilVO = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
        var rrList = new ArrayList<Object[]>();
        var serviceMap = new HashMap<String, Set<String>>();
        var categoryTypes = new ArrayList<String>();
        try {
            var configList = configMapper.configListToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelInAndKeyInAndStatus(unit,
                                                                                    List.of(channel), (List<String>) request.getFilter().get("serviceTypes"), "ACT"));

            for (ConfigDto config : configList) {
                String key = config.getKey();
                String value = config.getValue();

                Set<String> valueSet = Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toSet());
                categoryTypes.addAll(Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList()));
                serviceMap.put(key, valueSet);
            }

            if (request.getType().equalsIgnoreCase("DATE_RANGE")) {
                SimpleDateFormat formatter = new SimpleDateFormat(AppConstant.DATE_FORMATE);
                Date startDate = formatter.parse(request.getStartDate());
                Date endDate = new Date(formatter.parse(request.getEndDate()).getTime() + 86399999);

                rrList = (ArrayList<Object[]>) rrMessageRepo.fetchTransactionAnalysisByDateRange((List<String>) request.getFilter().get("channels"),
                                                                                                    categoryTypes, startDate, endDate);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                Date endDate = calendar.getTime();

                calendar.add(Calendar.DAY_OF_YEAR, -7);
                Date startDate = calendar.getTime();

                rrList = (ArrayList<Object[]>) rrMessageRepo.fetchTransactionAnalysisByDateRange((List<String>) request.getFilter().get("channels"),
                                                                                                        categoryTypes, startDate, endDate);
            }
            response.setData(getTransactionAnalysis(rrList));
        } catch (Exception e) {
            log.error("Exception in getTransactionMetricsByPlatform service: {}", e);
            resultUtilVO = new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC);
        }
        response.setStatus(resultUtilVO);
        return response;
    }

    public Map<String, Object> getTransactionAnalysis(List<Object[]> result) {

        Map<String, Map<String, Object>> platformMap = new LinkedHashMap<>();

        for (Object[] row : result) {
            String platformType = (String) row[0];
            String channelId = (String) row[1];
            Long transactionCount = (Long) row[2];

            platformMap.putIfAbsent(platformType, new LinkedHashMap<>() {{
                put("platformType", platformType);
                put("transactionCount", 0L);
                put("channelBreakdown", new ArrayList<Map<String, Object>>());
            }});

            Map<String, Object> platformData = platformMap.get(platformType);
            platformData.put("transactionCount", (Long) platformData.get("transactionCount") + transactionCount);

            List<Map<String, Object>> channelBreakdown = (List<Map<String, Object>>) platformData.get("channelBreakdown");
            channelBreakdown.add(new LinkedHashMap<>() {{
                put("channelId", channelId);
                put("transactionCount", transactionCount);
            }});
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("transactionAnalysis", new LinkedHashMap<>() {{
            put("totalTransactions", platformMap.values().stream()
                    .mapToLong(platform -> (Long) platform.get("transactionCount")).sum());
            put("platforms", new ArrayList<>(platformMap.values()));
        }});

        return response;
    }

    @Override
    public GenericResponse<Map> getTransactionMetricsByModel(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request) {

        var response = new GenericResponse<Map>();
        ResultUtilVO resultUtilVO = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
        var rrList = new ArrayList<Object[]>();
        var serviceMap = new HashMap<String, Set<String>>();
        var categoryTypes = new ArrayList<String>();
        try {

            var configList = configMapper.configListToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelInAndKeyInAndStatus(unit,
                            List.of(channel), (List<String>) request.getFilter().get("serviceTypes"), "ACT"));

            for (ConfigDto config : configList) {
                String key = config.getKey();
                String value = config.getValue();

                Set<String> valueSet = Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toSet());
                categoryTypes.addAll(Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList()));

                serviceMap.put(key, valueSet);
            }

            if (request.getType().equalsIgnoreCase("DATE_RANGE")) {
                SimpleDateFormat formatter = new SimpleDateFormat(AppConstant.DATE_FORMATE);
                Date startDate = formatter.parse(request.getStartDate());
                Date endDate = new Date(formatter.parse(request.getEndDate()).getTime() + 86399999);

                rrList = (ArrayList<Object[]>) rrMessageRepo.fetchTransactionAnalysisModelByDateRange(
                                (List<String>) request.getFilter().get("channels"), categoryTypes, startDate, endDate);

            } else {
                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                Date endDate = calendar.getTime();

                calendar.add(Calendar.DAY_OF_YEAR, -7);
                Date startDate = calendar.getTime();

                rrList = (ArrayList<Object[]>) rrMessageRepo.fetchTransactionAnalysisModelByDateRange(
                                (List<String>) request.getFilter().get("channels"), categoryTypes, startDate, endDate);
            }
            response.setData(getTransactionAnalysisForModel(rrList));
        } catch (Exception e) {
            log.error("Exception in getTransactionMetricsByPlatform service: {}", e);
            resultUtilVO = new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC);
        }
        response.setStatus(resultUtilVO);
        return response;
    }

    private Map<String, Object> getTransactionAnalysisForModel(List<Object[]> result) {

        Map<String, Map<String, Object>> platformMap = new LinkedHashMap<>();

        for (Object[] row : result) {
            String modelType = (String) row[0];
            String channelId = (String) row[1];
            Long transactionCount = (Long) row[2];

            platformMap.putIfAbsent(modelType, new LinkedHashMap<>() {{
                put("modelType", modelType);
                put("transactionCount", 0L);
                put("channelBreakdown", new ArrayList<Map<String, Object>>());
            }});

            Map<String, Object> platformData = platformMap.get(modelType);
            platformData.put("transactionCount", (Long) platformData.get("transactionCount") + transactionCount);

            List<Map<String, Object>> channelBreakdown = (List<Map<String, Object>>) platformData.get("channelBreakdown");
            channelBreakdown.add(new LinkedHashMap<>() {{
                put("channelId", channelId);
                put("transactionCount", transactionCount);
            }});
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("transactionAnalysis", new LinkedHashMap<>() {{
            put("totalTransactions", platformMap.values().stream()
                    .mapToLong(platform -> (Long) platform.get("transactionCount")).sum());
            put("models", new ArrayList<>(platformMap.values()));
        }});

        return response;
    }

    @Override
    public GenericResponse<Map> getTransactionMetricsByDateRange(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request) {

        var response = new GenericResponse<Map>();
        ResultUtilVO resultUtilVO = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
        var rrList = new ArrayList<Object[]>();
        Map<String, Set<String>> platformMap = new HashMap();
        var categoryTypes = new ArrayList<String>();
        try {

            SimpleDateFormat formatter = new SimpleDateFormat(AppConstant.DATE_FORMATE);
            Date startDate = formatter.parse(request.getStartDate());
            Date endDate = new Date(formatter.parse(request.getEndDate()).getTime() + 86399999);

            rrList = (ArrayList<Object[]>) rrMessageRepo.fetchTransactionByDateRange(
                            (List<String>) request.getFilter().get("channels"), startDate, endDate);

            var configList = configMapper
                    .configListToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelInAndKeyInAndStatus(unit,
                            List.of(channel),
                            List.of("mobile", "web"), "ACT"));

            for (ConfigDto config : configList) {
                String key = config.getKey();
                String value = config.getValue();

                Set<String> valueSet = Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toSet());
                categoryTypes.addAll(Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList()));

                platformMap.put(key, valueSet);
            }
            response.setData(generateTransactionSummary(rrList, platformMap, request.getStartDate(), request.getEndDate()));
        } catch (Exception e) {
            log.error("Exception in getTransactionMetricsByDateRange service: {}", e);
            resultUtilVO = new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC);
        }

        response.setStatus(resultUtilVO);
        return response;
    }

    private Map<String, Object> generateTransactionSummary(List<Object[]> result, Map<String, Set<String>> platformMap, String startDate, String endDate) {

        Set<String> mobilePlatforms = platformMap.getOrDefault("mobile", new LinkedHashSet<>());
        Set<String> webPlatforms = platformMap.getOrDefault("web", new LinkedHashSet<>());

        Map<String, Map<String, Long>> platformCountsByChannel = new LinkedHashMap<>();

        for (Object[] row : result) {
            String channelId = (String) row[0];
            String channelName = (String) row[1];
            String devicePlatform = (String) row[2];
            Long transactionCount = (Long) row[3];

            // Skip rows where devicePlatform is null
            if (devicePlatform == null || devicePlatform.equalsIgnoreCase("null")) {
                continue;
            }

            platformCountsByChannel.putIfAbsent(channelId, new LinkedHashMap<>());
            Map<String, Long> channelData = platformCountsByChannel.get(channelId);

            channelData.putIfAbsent("channelName", 0L);

            if (mobilePlatforms.contains(devicePlatform)) {
                channelData.merge("mobile", transactionCount, Long::sum);
                channelData.merge("total", transactionCount, Long::sum);
            } else if (webPlatforms.contains(devicePlatform)) {
                channelData.merge("web", transactionCount, Long::sum);
                channelData.merge("total", transactionCount, Long::sum);
            }

        }

        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> dateRangeMap = new LinkedHashMap<>();
        dateRangeMap.put("startDate", startDate);
        dateRangeMap.put("endDate", endDate);
        response.put("dateRange", dateRangeMap);
        Map<String, Object> transactionSummary = new HashMap<>();

        List<Map<String, Object>> channelSummaries = new ArrayList<>();
        long totalTransactions = 0;

        for (Map.Entry<String, Map<String, Long>> entry : platformCountsByChannel.entrySet()) {
            String channelId = entry.getKey();
            Map<String, Long> counts = entry.getValue();

            Map<String, Object> channelSummary = new HashMap<>();
            channelSummary.put("channelId", channelId);
            channelSummary.put("channelName", result.stream()
                    .filter(row -> channelId.equals(row[0]))
                    .map(row -> (String) row[1])
                    .findFirst()
                    .orElse("Unknown Channel"));
            long channelTotalTransactions = counts.getOrDefault("total", 0L);
            System.out.println("total2---" + channelTotalTransactions);
            long webTransactions = counts.getOrDefault("web", 0L);
            long mobileTransactions = counts.getOrDefault("mobile", 0L);

            channelSummary.put("totalTransactions", channelTotalTransactions);
            channelSummary.put("webTransactions", webTransactions);
            channelSummary.put("mobileTransactions", mobileTransactions);

            totalTransactions += channelTotalTransactions;
            channelSummaries.add(channelSummary);
        }

        transactionSummary.put("totalTransactions", totalTransactions);
        transactionSummary.put("channels", channelSummaries);
        response.put("transactionSummary", transactionSummary);

        return response;
    }

    @Override
    public GenericResponse<Map> getMetricsPieChart(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request) {

        var response = new GenericResponse<Map>();
        ResultUtilVO resultUtilVO = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
        var serviceMap = new HashMap<String, Set<String>>();
        var rrList = new ArrayList<RRmessage>();
        var categoryTypes = new ArrayList<String>();
        try {

            //All services
            ConfigDto configDto = configMapper.configToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelAndKey(unit, channel, "TOTAL_TXN_SERVICES"));

            List<String> servicesList = Arrays.stream(configDto.getValue().split(",")).map(String::trim).collect(Collectors.toList());

            var configList = configMapper
                    .configListToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelInAndKeyInAndStatus(unit,
                            List.of(channel),
                            servicesList, "ACT"));

            for (ConfigDto config : configList) {
                String key = config.getKey();
                String value = config.getValue();

                Set<String> valueSet = Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toSet());
                categoryTypes.addAll(Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList()));

                serviceMap.put(key, valueSet);
            }

            if (request.getType().equalsIgnoreCase(AppConstant.DATE_RANGE)) {
                SimpleDateFormat formatter = new SimpleDateFormat(AppConstant.DATE_FORMATE);
                Date startDate = formatter.parse(request.getStartDate());
                Date endDate = new Date(formatter.parse(request.getEndDate()).getTime() + 86399999);

                rrList = (ArrayList<RRmessage>) rrMessageRepo
                        .findByChannelIdInAndCategoryCodeInAndRequestDateBetweenOrderByDateCreatedDesc(
                                (List<String>) request.getFilter().get("channels"), categoryTypes, startDate, endDate);
            } else {

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                Date endDate = calendar.getTime();

                calendar.add(Calendar.DAY_OF_YEAR, -7);
                Date startDate = calendar.getTime();

                rrList = (ArrayList<RRmessage>) rrMessageRepo
                        .findByChannelIdInAndCategoryCodeInAndRequestDateBetweenOrderByDateCreatedDesc(
                                (List<String>) request.getFilter().get("channels"), categoryTypes, startDate, endDate);
            }
            response.setData(generateResultByChannels(rrList));
        } catch (Exception e) {
            log.error("Exception in getTransactionMetrics service: {}", e);
            resultUtilVO = new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC);
        }

        response.setStatus(resultUtilVO);
        return response;
    }

    public Map<String, Object> generateResultByChannels(List<RRmessage> rrList) {

        Map<String, Map<String, Long>> groupedByChannel = rrList.stream()
                .collect(Collectors.groupingBy(
                        RRmessage::getChannelId,
                        Collectors.groupingBy(
                                msg -> "000000".equals(msg.getResponseCode()) ? "success" : "failure",
                                Collectors.counting()
                        )
                ));

        List<Map<String, Object>> channels = groupedByChannel.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> channel = new LinkedHashMap<>();
                    channel.put("id", entry.getKey());
                    channel.put("success", entry.getValue().getOrDefault("success", 0L));
                    channel.put("failure", entry.getValue().getOrDefault("failure", 0L));
                    return channel;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("channels", channels);

        return response;
    }

    @Override
    public GenericResponse<Map> txnMetricsByService(String unit, String channel, String lang, String serviceId, TransactionMetricsReq request) {

        var response = new GenericResponse<Map>();
        ResultUtilVO resultUtilVO = new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC);
        var serviceMap = new HashMap<String, List<String>>();
        var rrList = new ArrayList<RRmessage>();
        var rrListLogin = new ArrayList<RRmessage>();
        var categoryTypes = new ArrayList<String>();
        try {

            //All services
            ConfigDto configDto = configMapper.configToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelAndKey(unit, channel, "TOTAL_TXN_SERVICES"));

            List<String> servicesList = Arrays.stream(configDto.getValue().split(",")).map(String::trim).collect(Collectors.toList());

            var configList = configMapper
                    .configListToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelInAndKeyInAndStatus(unit,
                            List.of(channel),
                            servicesList, "ACT"));

            for (ConfigDto config : configList) {
                String key = config.getKey();
                String value = config.getValue();

                List<String> valueSet = Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList());
                categoryTypes.addAll(Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList()));

                serviceMap.put(key, valueSet);
            }

            if (request.getType().equalsIgnoreCase(AppConstant.DATE_RANGE)) {
                SimpleDateFormat formatter = new SimpleDateFormat(AppConstant.DATE_FORMATE);
                Date startDate = formatter.parse(request.getStartDate());
                Date endDate = new Date(formatter.parse(request.getEndDate()).getTime() + 86399999);

                rrList = (ArrayList<RRmessage>) rrMessageRepo
                        .findByChannelIdInAndCategoryCodeInAndRequestDateBetweenOrderByDateCreatedDesc(
                                (List<String>) request.getFilter().get("channels"), categoryTypes, startDate, endDate);

				/* rrListLogin = (ArrayList<RRmessage>) rrMessageRepo
						.findRRByCategoryCodeWithDateRange(
								(List<String>) request.getFilter().get("channels"), List.of(AppConstant.LOGIN_CATEGORY_CODE), startDate, endDate); */
            } else {

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                Date endDate = calendar.getTime();

                calendar.add(Calendar.DAY_OF_YEAR, -7);
                Date startDate = calendar.getTime();

                rrList = (ArrayList<RRmessage>) rrMessageRepo
                        .findByChannelIdInAndCategoryCodeInAndRequestDateBetweenOrderByDateCreatedDesc(
                                (List<String>) request.getFilter().get("channels"), categoryTypes, startDate, endDate);

		        /* rrListLogin = (ArrayList<RRmessage>) rrMessageRepo
						.findRRByCategoryCodeWithDateRange(
								(List<String>) request.getFilter().get("channels"), List.of(AppConstant.LOGIN_CATEGORY_CODE), startDate, endDate); */
            }
            response.setData(analyzeTransactionsByServiceAndChannel(rrList,serviceMap, rrListLogin));
        } catch (Exception e) {
            log.error("Exception in getTransactionMetrics service: {}", e);
            resultUtilVO = new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC);
        }

        response.setStatus(resultUtilVO);
        return response;
    }

    private static Map<String, Object> analyzeTransactionsByServiceAndChannel(List<RRmessage> messages, Map<String, List<String>> transactionTypeMap, List<RRmessage> messagesLogin) {
        int totalTransactions = messages.size() + messagesLogin.size();

        List<Map<String, Object>> platforms = transactionTypeMap.entrySet().stream()
                .map(entry -> {
                    String transactionType = entry.getKey();
                    List<String> categoryTypes = entry.getValue();
                    Map<String, Object> platformData = new LinkedHashMap<>();

                    if(transactionType.equalsIgnoreCase("login")) {
                        Map<String, Long> channelCountsLogin = messages.stream()
                                .collect(Collectors.groupingBy(RRmessage::getChannelId, Collectors.counting()));

                        List<Map<String, Object>> channelBreakdown = channelCountsLogin.entrySet().stream()
                                .map(channelEntry -> {
                                    Map<String, Object> channelData = new LinkedHashMap<>();
                                    channelData.put("channelId", channelEntry.getKey());
                                    channelData.put("transactionCount", channelEntry.getValue().toString());
                                    return channelData;
                                })
                                .collect(Collectors.toList());

                        platformData.put("transactionType", transactionType);
                        platformData.put("transactionCount", String.valueOf(messagesLogin.size()));
                        platformData.put("channelBreakdown", channelBreakdown);
                    } else {
                        List<RRmessage> filteredMessages = messages.stream()
                                .filter(msg -> categoryTypes.contains(msg.getCategoryCode()))
                                .collect(Collectors.toList());

                        Map<String, Long> channelCounts = filteredMessages.stream()
                                .collect(Collectors.groupingBy(RRmessage::getChannelId, Collectors.counting()));

                        List<Map<String, Object>> channelBreakdown = channelCounts.entrySet().stream()
                                .map(channelEntry -> {
                                    Map<String, Object> channelData = new LinkedHashMap<>();
                                    channelData.put("channelId", channelEntry.getKey());
                                    channelData.put("transactionCount", channelEntry.getValue().toString());
                                    return channelData;
                                })
                                .collect(Collectors.toList());

                        platformData.put("transactionType", transactionType);
                        platformData.put("transactionCount", String.valueOf(filteredMessages.size()));
                        platformData.put("channelBreakdown", channelBreakdown);
                    }

                    return platformData;
                })
                .collect(Collectors.toList());

        // Build the final response
        Map<String, Object> transactionAnalysis = new HashMap<>();
        transactionAnalysis.put("totalTransactions", String.valueOf(totalTransactions));
        transactionAnalysis.put("platforms", platforms);

        Map<String, Object> response = new HashMap<>();
        response.put("transactionAnalysis", transactionAnalysis);

        return response;
    }
}
