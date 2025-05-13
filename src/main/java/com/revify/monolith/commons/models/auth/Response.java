package com.revify.monolith.commons.models.auth;


import com.revify.monolith.commons.ValidationContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    public List<ValidationContext> validationContext;

    public Response(ValidationContext validationContext) {
        this.validationContext = Collections.singletonList(validationContext);
    }

    public static Response success() {
        return new Response(Collections.singletonList(ValidationContext.SUCCESS));
    }
}