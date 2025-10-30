package com.digi.common.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocateUsDTO {

    private String locatorType;
    private String searchString;

    // latitude/longitude represented via coordinates
    private CoordinatesDTO coordinates;

    private String facility;
    //    private String address;
    private String name;

    private Integer cashDeposit; // 0/1
    private Integer cashOut; // 0/1
    private Integer chequeDeposit; // 0/1

    private String city;

    private String code;
    private String contactDetails;
    private String country;
    private Integer disablePeople; // 0/1
    private String fullAddress;

    private Integer onlineLocation; // 0/1
    private String timing;
    private String typeLocation;
    private String workingHours;

    private String status; // e.g., OPEN, CLOSED, UNKNOWN

    private Date dateCreate;
    private String userCreate;
    private Date dateModif;
    private String userModif;

    private String maintenanceVendor;
    private String atmType;
    private String currencySupported;
    private String isActive; // 'Y' or 'N'
    private Date installationDate;

    @JsonIgnore
    private String originalLocatorType;

}
