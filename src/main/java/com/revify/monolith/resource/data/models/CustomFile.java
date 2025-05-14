package com.revify.monolith.resource.data.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document
@RequiredArgsConstructor
public class CustomFile implements Serializable {
    private ObjectId fileId;
    private Boolean active = true;
    private String fileHash;
    private Long createdAt;
    private String initialFileName;
    private String initialFileExtension;
    private String internalFilePath;
    private Long internalFileLiveTime;
    private String systemFileName;
    private FileOptions fileOptions;
}
