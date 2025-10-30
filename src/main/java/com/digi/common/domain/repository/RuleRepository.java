package com.digi.common.domain.repository;


import com.digi.common.domain.model.RuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<RuleEntity, Long> {
    // Fetch only active rules (status = true)
    List<RuleEntity> findByTypeAndLanguageAndStatusTrue(String type, String language);
}
 