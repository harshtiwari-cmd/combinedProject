package com.digi.common.domain.repository;

import com.digi.common.entity.TermsAndCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermsAndConditionRepo extends JpaRepository<TermsAndCondition, Integer> {

	@Query("SELECT t FROM TermsAndCondition t WHERE t.lang = :lang AND t.screenId = :screenId AND t.status IN ('ACT')")
	List<TermsAndCondition> findTermsAndconditionBasedOnLangAndScreenId(String lang,String screenId); 

}

