package com.revify.monolith.commons.models.bid;

import com.revify.monolith.geo.model.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class PathFragment implements Serializable {
    private GeoLocation from;
    private GeoLocation to;


    public static PathFragment defaultBehaviour() {
        return PathFragment.builder().build();
    }
}
