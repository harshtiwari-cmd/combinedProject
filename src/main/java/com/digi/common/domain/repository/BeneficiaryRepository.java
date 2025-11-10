package com.digi.common.domain.repository;

import com.digi.common.infrastructure.persistance.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
}
