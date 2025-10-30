package com.digi.common.domain.repository;

import com.digi.common.infrastructure.persistance.BankDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankDetailsRepository extends JpaRepository<BankDetailsEntity, Long> {
    BankDetailsEntity findTopBy();
}
