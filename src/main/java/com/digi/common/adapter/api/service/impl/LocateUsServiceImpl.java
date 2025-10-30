package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.LocateUsService;
import com.digi.common.adapter.repository.RbxTLocatorNewRepository;
import com.digi.common.domain.model.dto.CoordinatesDTO;
import com.digi.common.domain.model.dto.LocateUsDTO;
import com.digi.common.domain.repository.LocateUsImagesRepository;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.persistance.RbxTLocatorNewEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@Service
@ConditionalOnProperty(name = "mock.enabled", havingValue = "false")
public class LocateUsServiceImpl implements LocateUsService {

    @Autowired
    private RbxTLocatorNewRepository repository;

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private LocateUsImagesRepository imagesRepository;

    @Async
    @Override
    public CompletableFuture<Map<String, List<LocateUsDTO>>> fetchAllTypesAsync(String lang) {
        log.info("Fetching all locator types asynchronously for language: {}", lang);

        List<RbxTLocatorNewEntity> allRows = repository.findAll();
        log.debug("Retrieved {} locator entities from repository", allRows.size());

        Map<String, List<LocateUsDTO>> grouped = allRows.stream()
                .map(entity -> mapToUnifiedDto(entity, lang))
                .collect(Collectors.groupingBy(dto -> dto.getOriginalLocatorType().toLowerCase()));

        Map<String, List<LocateUsDTO>> result = new HashMap<>();
        result.put(AppConstant.BRANCHES, grouped.getOrDefault("branch", Collections.emptyList()));
        result.put(AppConstant.ATMS, grouped.getOrDefault("atm", Collections.emptyList()));
        result.put(AppConstant.KIOSKS, grouped.getOrDefault("kiosk", Collections.emptyList()));

        log.info("Successfully grouped locators - Branches: {}, ATMs: {}, Kiosks: {}",
                result.get(AppConstant.BRANCHES).size(),
                result.get(AppConstant.ATMS).size(),
                result.get(AppConstant.KIOSKS).size());

        return CompletableFuture.completedFuture(result);
    }


    @Override
    public String getImageForType(String locatorType) {
        log.info("Getting image for locator type: {}", locatorType);

        if (locatorType == null) {
            log.error("Locator type is null");
            throw new IllegalArgumentException("Locator type must not be null");
        }

        switch (locatorType.toUpperCase()) {
            case AppConstant.BRANCH:
            case AppConstant.ATM:
            case AppConstant.KIOSK:
                return imagesRepository.findByLocatorType(locatorType).getImage();
            default:
                throw new IllegalArgumentException("Unsupported locator type: " + locatorType);
        }
    }

    private LocateUsDTO mapToUnifiedDto(RbxTLocatorNewEntity e, String lang) {
        log.debug("Mapping entity to DTO for locator type: {}, language: {}", e.getLocatorType(), lang);

        LocateUsDTO locateUsDTO = LocateUsDTO.builder()
                .locatorType(e.getLocatorType())
                .originalLocatorType(e.getLocatorType())
                .searchString(e.getSearchString())
                .coordinates(parseCoordinates(e))
                .facility(e.getFacility())
                .cashDeposit(e.getCashDeposit())
                .cashOut(e.getCashOut())
                .chequeDeposit(e.getChequeDeposit())
                .code(e.getCode())
                .contactDetails(e.getContactDetails())
                .country(e.getCountry())
                .disablePeople(e.getDisablePeople())
                .fullAddress(e.getFullAddress())
                .onlineLocation(e.getOnlineLocation())
                .timing(e.getTiming())
                .typeLocation(e.getTypeLocation())
                .status(calculateStatus(e, lang))
                .dateCreate(e.getDateCreate())
                .userCreate(e.getUserCreate())
                .dateModif(e.getDateModif())
                .userModif(e.getUserModif())
                .maintenanceVendor(e.getMaintenanceVendor())
                .atmType(e.getAtmType())
                .currencySupported(e.getCurrencySupported())
                .isActive(e.getIsActive())
                .installationDate(e.getInstallationDate())
                .build();



        if (AppConstant.LANGUAGE_IN_AR.equalsIgnoreCase(lang)) {
            log.debug("Setting Arabic language fields for locator: {}", e.getCode());

            switch (e.getLocatorType()) {
                case AppConstant.BRANCH:
                    locateUsDTO.setLocatorType(AppConstant.BRANCHES_IN_AR);
                    locateUsDTO.setAtmType(AppConstant.BRANCHES_IN_AR);
                    break;
                case AppConstant.ATM:
                    locateUsDTO.setLocatorType(AppConstant.ATMS_IN_AR);
                    locateUsDTO.setAtmType(AppConstant.ATMS_IN_AR);
                    break;
                case AppConstant.KIOSK:
                    locateUsDTO.setLocatorType(AppConstant.KIOSKS_IN_AR);
                    locateUsDTO.setAtmType(AppConstant.KIOSKS_IN_AR);
                    break;
            }

            locateUsDTO.setName(e.getArabicName());
            locateUsDTO.setFullAddress(e.getFullAddressArb());
            locateUsDTO.setCity(e.getCityInArabic());
            locateUsDTO.setCountry(AppConstant.COUNTRY_IN_AR);
            locateUsDTO.setWorkingHours(e.getWorkingHoursInArb());
        } else {
            log.debug("Setting English language fields for locator: {}", e.getCode());
            locateUsDTO.setName(e.getName());
            locateUsDTO.setFullAddress(e.getFullAddress());
            locateUsDTO.setCity(e.getCity());
            locateUsDTO.setWorkingHours(e.getWorkingHours());
        }

        return locateUsDTO;
    }

    private CoordinatesDTO parseCoordinates(RbxTLocatorNewEntity e) {
        try {
            double lat = e.getLatitude() == null ? 0.0 : Double.parseDouble(e.getLatitude());
            double lon = e.getLongitude() == null ? 0.0 : Double.parseDouble(e.getLongitude());
            log.debug("Parsed coordinates for locator {}: lat={}, lon={}", e.getCode(), lat, lon);
            return CoordinatesDTO.builder().latitude(lat).longitude(lon).build();
        } catch (Exception ex) {
            log.warn("Failed to parse coordinates for locator {}: lat={}, lon={}. Using default (0.0, 0.0)",
                    e.getCode(), e.getLatitude(), e.getLongitude(), ex);
            return CoordinatesDTO.builder().latitude(0.0).longitude(0.0).build();
        }
    }

    private String calculateStatus(RbxTLocatorNewEntity e, String lang) {
        log.debug("Calculating status for locator {} with language {}", e.getCode(), lang);

        String working = e.getWorkingHours();
        if (working == null || working.isBlank()) {
            working = e.getTiming();
        }
        if (working == null) {
            log.debug("No working hours found for locator {}, returning unknown status", e.getCode());
            return AppConstant.UNKNOWN;
        }

        String normalized = normalizeWorkingHours(working);
        if (normalized.toUpperCase().contains("24/7") || normalized.toUpperCase().contains("24X7") || normalized.toUpperCase().contains("24 X 7")) {
            log.debug("Locator {} is 24/7, returning open status", e.getCode());
            return AppConstant.DEFAULT_LANGUAGE.equalsIgnoreCase(lang) ? AppConstant.OPEN_IN_EN : AppConstant.OPEN_IN_AR;
        }

        Map<DayOfWeek, List<TimeWindow>> schedule = parseSchedule(normalized);
        if (schedule.isEmpty()) {
            log.debug("No schedule parsed for locator {}, returning closed status", e.getCode());
            return AppConstant.DEFAULT_LANGUAGE.equalsIgnoreCase(lang) ? AppConstant.CLOSE_IN_EN : AppConstant.CLOSE_IN_AR;
        }

        ZoneId zoneId = ZoneId.of(AppConstant.ZONE_ID);
        Optional<TemporalAccessor> nowOptional = dateTimeProvider.getNow();
        LocalDateTime now = nowOptional
                .map(LocalDateTime::from)
                .orElse(LocalDateTime.now(zoneId));
        LocalTime current = now.toLocalTime();
        DayOfWeek today = now.getDayOfWeek();

        log.debug("Checking status for locator {} on {} at {}", e.getCode(), today, current);

        List<TimeWindow> todays = schedule.getOrDefault(today, Collections.emptyList());
        for (TimeWindow w : todays) {
            boolean open;
            if (w.crossesMidnight) {
                open = !current.isBefore(w.start) || !current.isAfter(w.end);
            } else {
                open = !current.isBefore(w.start) && !current.isAfter(w.end);
            }
            if (open) {
                log.debug("Locator {} is currently open (time window: {} - {})", e.getCode(), w.start, w.end);
                return AppConstant.DEFAULT_LANGUAGE.equalsIgnoreCase(lang) ? AppConstant.OPEN_IN_EN : AppConstant.OPEN_IN_AR;
            }
        }

        log.debug("Locator {} is currently closed", e.getCode());
        return AppConstant.DEFAULT_LANGUAGE.equalsIgnoreCase(lang) ? AppConstant.CLOSE_IN_EN : AppConstant.CLOSE_IN_AR;
    }


    private static class TimeWindow {
        final LocalTime start;
        final LocalTime end;
        final boolean crossesMidnight;

        TimeWindow(LocalTime start, LocalTime end, boolean crossesMidnight) {
            this.start = start;
            this.end = end;
            this.crossesMidnight = crossesMidnight;
        }
    }

    private String normalizeWorkingHours(String text) {
        if (text == null) return "";
        return text
                .replace("\\n", "\n")
                .replace("\\N", "\n")
                .replace('–', '-')  // en dash
                .replace('—', '-')  // em dash
                .replaceAll("\\s+to\\s+", " TO ")
                .replaceAll("\\s*-\\s*", "-")
                .replaceAll("\\s*/\\s*", "/") // keep slash for multiple windows
                .trim();
    }

    private Map<DayOfWeek, List<TimeWindow>> parseSchedule(String text) {
        if (text == null || text.isBlank()) {
            log.debug("Empty schedule text provided");
            return Collections.emptyMap();
        }

        log.debug("Parsing schedule from text: {}", text);
        Map<DayOfWeek, List<TimeWindow>> schedule = new EnumMap<>(DayOfWeek.class);

        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.isBlank()) continue;
            int idx = firstTimeIndex(line);
            if (idx == -1) {
                log.debug("Skipping line with no time index: {}", line);
                continue;
            }

            String dayPart = line.substring(0, idx).trim();
            String timePart = line.substring(idx).trim();

            List<DayOfWeek> days = parseDays(dayPart);
            if (days.isEmpty()) {
                log.debug("No days parsed from: {}", dayPart);
                continue;
            }

            String[] windows = timePart.split("\\s*/\\s*|\\s*;\\s*|\\s*,\\s*");
            for (String w : windows) {
                TimeWindow tw = parseTimeWindowOnly(w.trim());
                if (tw != null) {
                    for (DayOfWeek d : days) {
                        schedule.computeIfAbsent(d, k -> new ArrayList<>()).add(tw);
                        log.debug("Added time window {} - {} for {}", tw.start, tw.end, d);
                    }
                } else {
                    log.debug("Failed to parse time window from: {}", w);
                }
            }
        }

        log.debug("Parsed schedule with {} day(s)", schedule.size());
        return schedule;
    }

    private int firstTimeIndex(String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isDigit(c)) return i;
        }
        return -1;
    }

    private List<DayOfWeek> parseDays(String dayPart) {
        if (dayPart == null || dayPart.isBlank()) return Collections.emptyList();
        String dp = dayPart.toUpperCase(Locale.ROOT)
                .replace("&", ",")
                .replace(" AND ", ",")
                .replaceAll("\\s*-\\s*", " TO ")
                .replaceAll("\\s+TO\\s+", " TO ")
                .trim();

        List<DayOfWeek> result = new ArrayList<>();

        if (dp.contains(" TO ")) {
            String[] parts = dp.split(" TO ");
            if (parts.length == 2) {
                DayOfWeek start = dayNameToDOW(parts[0].trim());
                DayOfWeek end = dayNameToDOW(parts[1].trim());
                if (start != null && end != null) {
                    int s = start.getValue();
                    while (true) {
                        DayOfWeek d = DayOfWeek.of(s);
                        result.add(d);
                        if (d == end) break;
                        s = s % 7 + 1;
                    }
                }
            }
        }

        for (String part : dp.split(",")) {
            DayOfWeek d = dayNameToDOW(part.trim());
            if (d != null && !result.contains(d)) result.add(d);
        }

        return result;
    }

    private DayOfWeek dayNameToDOW(String s) {
        String t = s.trim().toLowerCase(Locale.ROOT);
        if (t.startsWith("sun")) return DayOfWeek.SUNDAY;
        if (t.startsWith("mon")) return DayOfWeek.MONDAY;
        if (t.startsWith("tue")) return DayOfWeek.TUESDAY;
        if (t.startsWith("wed")) return DayOfWeek.WEDNESDAY;
        if (t.startsWith("thu")) return DayOfWeek.THURSDAY;
        if (t.startsWith("fri")) return DayOfWeek.FRIDAY;
        if (t.startsWith("sat")) return DayOfWeek.SATURDAY;
        return null;
    }

    private TimeWindow parseTimeWindowOnly(String text) {
        if (text == null || text.isBlank()) return null;
        String cleaned = text.replace("to", "-").replace("TO", "-").trim();
        String[] parts = cleaned.split("\\s*-\\s*");
        if (parts.length < 2) return null;

        LocalTime start = parseTime(parts[0].trim());
        LocalTime end = parseTime(parts[1].trim());
        if (start == null || end == null) return null;

        boolean crossesMidnight = end.isBefore(start);
        return new TimeWindow(start, end, crossesMidnight);
    }

    private LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) {
            log.debug("Time string is null or blank");
            return null;
        }

        String normalized = s.trim().toUpperCase().replace("AM", " AM").replace("PM", " PM").replace("  ", " ");
        log.debug("Parsing time string: {} (normalized: {})", s, normalized);

        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("H:mm"),
                DateTimeFormatter.ofPattern("HH:mm"),
                DateTimeFormatter.ofPattern("h:mm a"),
                DateTimeFormatter.ofPattern("hh:mm a"),
                DateTimeFormatter.ofPattern("h a"),
                DateTimeFormatter.ofPattern("hh a"),
                DateTimeFormatter.ofPattern("ha")
        );
        for (DateTimeFormatter f : formatters) {
            try {
                LocalTime parsed = LocalTime.parse(normalized, f);
                log.debug("Successfully parsed time: {} using pattern: {}", parsed, f);
                return parsed;
            } catch (Exception ignored) {
            }
        }

        log.warn("Failed to parse time string: {}", s);
        return null;
    }
}

