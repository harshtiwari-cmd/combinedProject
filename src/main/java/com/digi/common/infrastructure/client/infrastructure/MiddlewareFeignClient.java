package com.digi.common.infrastructure.client.infrastructure;

import com.digi.common.domain.model.dto.MiddlewareRequestDto;
import com.digi.common.domain.model.dto.MiddlewareResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign Client for calling dkn-middleware-service REST API
 * 
 * This declarative REST client replaces the manual RestTemplate approach
 * and provides better integration with Spring Cloud features.
 */
@FeignClient(
    name = "middleware-service",
    url = "${middleware.base-url}",
    configuration = MiddlewareFeignClientConfig.class
)
public interface MiddlewareFeignClient {

    /**
     * Send request to middleware service
     * 
     * @param correlationId Correlation ID for tracking
     * @param language Requestor language (E or A)
     * @param request The middleware request
     * @return Response from middleware service
     */
    @PostMapping(
        value = "${middleware.api-path}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    MiddlewareResponseDto sendRequest(
        @RequestHeader(value = "X-Correlation-ID") String correlationId,
        @RequestHeader(value = "X-Requestor-Language", defaultValue = "E") String language,
        @RequestBody MiddlewareRequestDto request
    );
}
