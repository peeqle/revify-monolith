package com.revify.monolith.geo.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.io.Serializable;

@Data
@Builder
@Document("user_geolocation")
@AllArgsConstructor
@NoArgsConstructor
public class UserGeolocation implements Serializable {

    @Id
    private ObjectId id;

    @JsonAdapter(UserIdAdapter.class)
    @Indexed(unique = true)
    private Long userId;
    private ObjectId geolocationId;

    private Long timestamp;

    public static class UserIdAdapter extends TypeAdapter<Long> {
        @Override
        public void write(JsonWriter out, Long exit) throws IOException {
            out.beginObject();
            out.name("sender_id").value(exit);
            out.endObject();
        }

        @Override
        public Long read(JsonReader in) throws IOException {
            in.beginObject();
            in.nextName();
            Long exitValue = in.nextLong();
            in.endObject();
            return exitValue;
        }
    }

    /**
     * Returns KM
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public double haversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Earth's radius in km
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}

