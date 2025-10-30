package com.digi.common.domain.repository;


import com.digi.common.domain.model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByLangIgnoreCase(String lang);

}
