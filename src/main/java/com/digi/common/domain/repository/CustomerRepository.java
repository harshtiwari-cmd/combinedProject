package com.digi.common.domain.repository;

import com.digi.common.infrastructure.persistance.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<UserAuth, Long> {

    @Query("SELECT c FROM UserAuth c WHERE c.customerId = :customerId")
    Optional<UserAuth> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT c.userId FROM UserAuth c WHERE c.customerId = :customerId")
    Optional<String> findUsernameByCustomerId(@Param("customerId") Long customerId);
}
