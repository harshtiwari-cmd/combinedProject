package com.digi.common.adapter.repository;

import com.digi.common.entity.PageConfig;

import java.util.List;

public interface PageConfigRepository {

	List<PageConfig> getValidations(String unitId, String channelId);
}

