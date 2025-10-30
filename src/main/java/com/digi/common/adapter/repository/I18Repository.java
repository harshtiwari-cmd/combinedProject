package com.digi.common.adapter.repository;

import com.digi.common.entity.I18N;

import java.util.List;

public interface I18Repository {

	List<I18N> getLabelList(String unitId, String channelId, String lang);

}
