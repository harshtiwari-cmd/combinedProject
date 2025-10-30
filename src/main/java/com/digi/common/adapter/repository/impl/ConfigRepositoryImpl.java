package com.digi.common.adapter.repository.impl;

import com.digi.common.adapter.repository.ConfigRepository;
import com.digi.common.dto.ConfigDto;
import com.digi.common.mapper.ConfigMapper;
import com.digi.common.repository.JPAConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigRepositoryImpl implements ConfigRepository {

	private final JPAConfigRepository jpaConfigRepository;
	
	private ConfigMapper configMapper;

    public ConfigRepositoryImpl(JPAConfigRepository jpaConfigRepository, ConfigMapper configMapper) {
        this.jpaConfigRepository = jpaConfigRepository;
        this.configMapper = configMapper;
    }

	@Override
	public Optional<ConfigDto> findByUnit_IdAndChannel_ChannelAndKeyAndStatus(String unit, String channel, String key,
			String status) {
		return Optional.of(configMapper.configToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelAndKeyAndStatus(unit, channel, key, status)));
	}


	@Override
	public List<ConfigDto> findByUnit_IdAndChannel_ChannelAndKeyStartsWithAndStatus(String unit, String channel,
			String key, String status) {
		return configMapper.configListToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelAndKeyStartsWithAndStatus(unit, channel, key, status));
	}
}
