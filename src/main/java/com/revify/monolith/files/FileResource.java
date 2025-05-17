package com.revify.monolith.files;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.commons.files.FileBase64BatchRequestDTO;
import com.revify.monolith.commons.files.FileBase64DTO;
import com.revify.monolith.commons.models.ResourceEntityType;
import com.revify.monolith.files.data.models.EntityFile;
import com.revify.monolith.files.data.models.FileOptions;
import com.revify.monolith.files.data.service.FileEntityService;
import com.revify.monolith.files.data.service.FileService;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class FileResource {

    private final FileService fileService;

    private final FileEntityService fileEntityService;


    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> upload(@RequestParam("entityType") String entityType,
                                         @RequestParam("entityId") String entityId,
                                         @RequestParam(value = "fileOrder", required = false) Long fileOrder,
                                         @RequestPart("file") MultipartFile multipartFile) {
        try {
            String savedFileId = fileService.store(FileOptions.builder()
                    .entityId(entityId)
                    .entityType(ResourceEntityType.valueOf(entityType))
                    .order(fileOrder != null ? fileOrder : -1)
                    .build(), multipartFile).toString();

            if (ResourceEntityType.USER.name().equals(entityType)) {
                fileEntityService.saveUserFileRelation(savedFileId);
            }
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException | TikaException e) {
            log.warn("Cannot save file.", e);
            return ResponseEntity.badRequest().body("FAILED");
        }
    }


    @DeleteMapping("/delete-for-user")
    public ResponseEntity<String> delete(@RequestParam("objectId") String objectId) {
        Tuple2<Long, List<EntityFile>> longDeleteResultTuple2 = fileEntityService.deleteUserFileRelation(objectId);
        if (longDeleteResultTuple2._1 <= 0) {
            fileService.delete(objectId);
        }

        return ResponseEntity.ok(String.format("Deleted %s", longDeleteResultTuple2._2.size()));
    }

    @DeleteMapping("/delete-for-entity")
    public ResponseEntity<String> deleteForEntity(@RequestParam("objectId") String objectId,
                                                  @RequestParam("entityId") String entityId) {
        Tuple2<Long, List<EntityFile>> longDeleteResultTuple2 = fileEntityService.deleteFileRelation(entityId, objectId);
        if (longDeleteResultTuple2._1 <= 0) {
            fileService.delete(objectId);
        }

        return ResponseEntity.ok(String.format("Deleted %s", longDeleteResultTuple2._2.size()));
    }

    @GetMapping(value = "/fetchForFileId")
    public ResponseEntity<?> fetchForFileId(@RequestParam("objectId") String objectId) {
        if (!ObjectId.isValid(objectId)) {
            return ResponseEntity.badRequest().body("objectId is incorrect");
        }
        GridFSFindIterable files = fileService.getById(new ObjectId(objectId));
        if (files != null) {
            List<GridFsResource> resources = new ArrayList<>(1);
            long contentLength = 0L;

            try (MongoCursor<GridFSFile> cursor = files.iterator()) {
                while (cursor.hasNext()) {
                    GridFSFile file = cursor.next();
                    resources.add(fileService.getResource(file));
                    contentLength += file.getLength();
                }
            }

            if (resources.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(contentLength);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resources);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/fetchForEntity")
    public ResponseEntity<FileBase64BatchRequestDTO> fetchForEntity(@RequestParam("entityType") String entityType,
                                                                    @RequestParam("entityId") String entityId) {

        GridFSFindIterable files = fileService.getForEntity(entityId, ResourceEntityType.valueOf(entityType));
        if (files != null) {
            FileBase64BatchRequestDTO fileBase64BatchRequestDTO = FileBase64BatchRequestDTO.builder()
                    .entityId(entityId)
                    .entityType(entityType)
                    .files(new LinkedList<>())
                    .build();

            try (MongoCursor<GridFSFile> cursor = files.iterator()) {
                while (cursor.hasNext()) {
                    GridFSFile file = cursor.next();
                    FileBase64DTO fileBase64DTO = FileBase64DTO.builder()
                            .fileName(file.getFilename())
                            .build();
                    try {
                        fileBase64DTO.setFileEncoded(Base64.getEncoder()
                                .encodeToString(fileService.getResource(file).getInputStream().readAllBytes()));
                        fileBase64DTO.setSuccess(true);
                        fileBase64DTO.setId(file.getId().asObjectId().getValue().toHexString());

                        fileBase64BatchRequestDTO.getFiles().add(fileBase64DTO);
                    } catch (IOException e) {
                        log.warn("Cannot read file resource from stream from gridFsFile: {}", file.getId(), e);
                    } catch (NullPointerException npe) {
                        log.warn("File contents corrupted: {} ", file.getId(), npe);
                    }
                }
            }

            return ResponseEntity.ok(fileBase64BatchRequestDTO);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ExceptionHandler(UnauthorizedAccessError.class)
    public ResponseEntity<String> handleUnauthorizedAccess(UnauthorizedAccessError unauthorizedAccessError) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

