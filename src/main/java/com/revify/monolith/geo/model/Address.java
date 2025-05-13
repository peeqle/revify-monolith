package com.revify.monolith.geo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String hamlet;
    private String village;
    private String city;
    private String town;
    private String region;
    private String municipality;
    private String county;
    private String state;
    private String ISO3166_2_lvl4;
    private String postcode;
    private String country;
    private String country_code;
}
