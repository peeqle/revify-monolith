package com.revify.monolith.commons.geolocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeoLocation implements Comparable<GeoLocation> {
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private String stateName;

    private String placeDistrict;
    //village, town or city name
    private String placeName;

    private String countryCode;
    private String countryName;

    private String displayName;

    @Override
    public boolean equals(Object s) {
        if (s instanceof GeoLocation x) {
            return Objects.equals(x.getLocation().getX(), getLocation().getX())
                    && Objects.equals(x.getLocation().getY(), getLocation().getY());
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return (location.hashCode() * placeName.hashCode()) / countryName.hashCode();
    }

    @Override
    public int compareTo(GeoLocation other) {
        if (other == null) return 1;

        int result = compareNullableStrings(this.countryName, other.countryName);
        if (result != 0) return result;

        result = compareNullableStrings(this.placeName, other.placeName);
        if (result != 0) return result;

        if (this.location != null && other.location != null) {
            result = Double.compare(this.location.getX(), other.location.getX());
            if (result != 0) return result;
            return Double.compare(this.location.getY(), other.location.getY());
        }

        return this.location == null ? (other.location == null ? 0 : -1) : 1;
    }

    public static int compareNullableStrings(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }
}
