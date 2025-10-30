package com.digi.common.adapter.repository;

import com.digi.common.domain.model.NotificationTemplate;
import com.digi.common.domain.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    
    Optional<NotificationTemplate> findByNotificationIdAndIsActive(Long notificationId, Integer isActive);
    
    List<NotificationTemplate> findByIsActive(Integer isActive);
    
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.useCase = :useCase AND nt.isActive = 1")
    Optional<NotificationTemplate> findByUseCaseAndIsActive(String useCase);
    
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.notificationType = :notificationType AND nt.isActive = 1")
    List<NotificationTemplate> findByNotificationTypeAndIsActive(NotificationType notificationType);
    
    @Query("SELECT nt FROM NotificationTemplate nt WHERE UPPER(nt.useCase) LIKE UPPER(CONCAT('%', :keyword, '%')) AND nt.isActive = 1")
    List<NotificationTemplate> findByUseCaseContainingIgnoreCaseAndIsActive(String keyword);
}
