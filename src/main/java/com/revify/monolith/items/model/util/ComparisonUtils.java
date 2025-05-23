package com.revify.monolith.items.model.util;


import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.geo.model.GeoLocation;

public class ComparisonUtils {
    public static int compareNullableStrings(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }

    public static int compareNullablePrices(Price p1, Price p2) {
        if (p1 == null && p2 == null) return 0;
        if (p1 == null) return -1;
        if (p2 == null) return 1;
        return p1.compareTo(p2);
    }

    public static int compareNullableGeoLocations(GeoLocation g1, GeoLocation g2) {
        if (g1 == null && g2 == null) return 0;
        if (g1 == null) return -1;
        if (g2 == null) return 1;
        return g1.compareTo(g2);
    }

    public static int compareNullableBooleans(Boolean b1, Boolean b2) {
        if (b1 == null && b2 == null) return 0;
        if (b1 == null) return -1;
        if (b2 == null) return 1;
        return b1.compareTo(b2);
    }
}
