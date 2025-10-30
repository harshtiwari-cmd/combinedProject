package com.digi.common.infrastructure.persistance;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "rbx_t_locator_new")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RbxTLocatorNewEntity {

    @Column(name = "locator_type")
    private String locatorType;

    @Column(name = "search_string")
    private String searchString;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "facility")
    private String facility;

    @Column(name = "address")
    private String address;

    @Column(name = "name")
    private String name;

    @Column(name = "arabic_name")
    private String arabicName;

    @Column(name = "cash_deposit")
    private Integer cashDeposit;

    @Column(name = "cash_out")
    private Integer cashOut;

    @Column(name = "cheque_deposit")
    private Integer chequeDeposit;

    @Column(name = "city")
    private String city;

    @Column(name = "city_in_arabic")
    private String cityInArabic;

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "contact_details")
    private String contactDetails;

    @Column(name = "country")
    private String country;

    @Column(name = "disable_people")
    private Integer disablePeople;

    @Column(name = "full_address")
    private String fullAddress;

    @Column(name = "full_address_arb")
    private String fullAddressArb;

    @Column(name = "online_location")
    private Integer onlineLocation;

    @Column(name = "timing")
    private String timing;

    @Column(name = "type_location")
    private String typeLocation;

    @Column(name = "working_hours")
    private String workingHours;

    @Column(name = "working_hours_in_arb")
    private String workingHoursInArb;

    @Column(name = "date_create")
    private Date dateCreate;

    @Column(name = "user_create")
    private String userCreate;

    @Column(name = "date_modif")
    private Date dateModif;

    @Column(name = "user_modif")
    private String userModif;

    @Column(name = "maintenance_vendor")
    private String maintenanceVendor;

    @Column(name = "atm_type")
    private String atmType;

    @Column(name = "currency_supported")
    private String currencySupported;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "installation_date")
    private Date installationDate;
}

