package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.LocateUsDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface LocateUsService {
    CompletableFuture<Map<String, List<LocateUsDTO>>> fetchAllTypesAsync(String lang);
    String getImageForType(String locatorType) throws IOException;
}
