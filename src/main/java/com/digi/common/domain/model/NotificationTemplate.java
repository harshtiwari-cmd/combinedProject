package com.digi.common.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {
    
    @Id
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "parameter_list")
    private String parameterList;

    @Column(name = "use_case")
    private String useCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;
    
    @Column(name = "is_active")
    private Integer isActive = 1; // 1 = active, 0 = inactive (Oracle/PostgreSQL compatible)

    // Helper methods for boolean semantics
    public boolean isActive() {
        return isActive != null && isActive == 1;
    }
    
    public void setActive(boolean active) {
        this.isActive = active ? 1 : 0;
    }
}
