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
    private GeoLocation geoLocation;

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
}

