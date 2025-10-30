package com.digi.common.adapter.repository.impl;

import com.digi.common.adapter.repository.I18Repository;
import com.digi.common.domain.repository.JpaI18Repository;
import com.digi.common.entity.I18N;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class I18RepositoryImpl implements I18Repository {
	
	@Autowired
	JpaI18Repository jpaI18Repo;

	@Override
	public List<I18N> getLabelList(String unitId, String channelId, String lang) {
		return jpaI18Repo.getLabelList(unitId, channelId, lang);
	}

}
