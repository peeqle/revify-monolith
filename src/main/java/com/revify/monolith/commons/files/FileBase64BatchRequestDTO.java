package com.revify.monolith.commons.files;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileBase64BatchRequestDTO {
    String entityId;
    String entityType;
    List<FileBase64DTO> files;
}
