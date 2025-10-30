package com.digi.common.domain.repository;

import com.digi.common.infrastructure.persistance.LocateUsImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocateUsImagesRepository extends JpaRepository<LocateUsImages, Integer> {

    LocateUsImages findByLocatorType(String locatorType);
}
