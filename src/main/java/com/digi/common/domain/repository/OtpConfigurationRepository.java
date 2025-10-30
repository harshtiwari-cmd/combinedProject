package com.digi.common.domain.repository;

import com.digi.common.domain.model.OtpConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpConfigurationRepository extends JpaRepository<OtpConfiguration, Long> {
    Optional<OtpConfiguration> findByScreenId(Long screenId);
}