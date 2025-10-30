package com.digi.common.infrastructure.persistance;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rbx_t_locator_images")
public class LocateUsImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String locatorType;

    @Column(columnDefinition = "TEXT")
    private String image;
}
