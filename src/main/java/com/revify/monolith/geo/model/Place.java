package com.revify.monolith.geo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Place {
    private long place_id;
    private String licence;
    private String osm_type;
    private long osm_id;
    private String lat;
    private String lon;
    private String classType;
    private String type;
    private int place_rank;
    private double importance;
    private String addresstype;
    private String name;
    private String display_name;
    private Address address;
    private List<String> boundingbox;
}
