package com.digi.common.adapter.repository;

import com.digi.common.dto.ConfigDto;

import java.util.List;
import java.util.Optional;

public interface ConfigRepository {

	Optional<ConfigDto> findByUnit_IdAndChannel_ChannelAndKeyAndStatus(String unit, String channel, String key,
			String status);

	List<ConfigDto> findByUnit_IdAndChannel_ChannelAndKeyStartsWithAndStatus(String unit, String channel,
			String otp, String status);

}
