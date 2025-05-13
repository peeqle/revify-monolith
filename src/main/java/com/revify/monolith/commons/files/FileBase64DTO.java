package com.revify.monolith.commons.files;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileBase64DTO {
    String id;
    String fileName;
    String fileEncoded;

    Boolean success = false;
}
