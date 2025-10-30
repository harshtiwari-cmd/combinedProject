package com.digi.common.adapter.repository.impl;

import com.digi.common.adapter.repository.PageConfigRepository;
import com.digi.common.domain.repository.JpaPageConfigRepository;
import com.digi.common.entity.PageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageConfigRepositoryImpl implements PageConfigRepository {

	@Autowired
	private JpaPageConfigRepository jpaPageConfigRepository;
	
	@Override
	public List<PageConfig> getValidations(String unitId, String channelId) {
		return jpaPageConfigRepository.getValidations(unitId, channelId);
	}

}
