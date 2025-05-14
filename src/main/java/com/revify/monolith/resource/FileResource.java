package com.revify.monolith.resource;

import com.revify.monolith.commons.models.ResourceEntityType;
import com.revify.monolith.resource.data.models.FileOptions;
import com.revify.monolith.resource.data.service.FileEntityService;
import com.revify.monolith.resource.data.service.FileService;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class FileResource {

    private final FileService fileService;

    private final FileEntityService fileEntityService;


    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> upload(@RequestPart("entityType") String entityType,
                                         @RequestPart("entityId") String entityId,
                                         @RequestPart(value = "fileOrder", required = false) Long fileOrder,
                                         @RequestPart("file") MultipartFile multipartFile) {
        try {
            String savedFileId = fileService.store(FileOptions.builder()
                    .entityId(entityId)
                    .entityType(ResourceEntityType.valueOf(entityType))
                    .order(fileOrder != null ? fileOrder : -1)
                    .build(), multipartFile).toString();

            fileEntityService.saveUserFileRelation(savedFileId);
            return ResponseEntity.ok(savedFileId);
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
        try {
            GridFSFile gridFSFile = fileService.getById(new ObjectId(objectId));
            if (gridFSFile != null) {

                GridFsResource resource = fileService.getResource(gridFSFile);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                headers.setContentLength(resource.contentLength());
                headers.setContentDisposition(ContentDisposition.builder("inline")
                        .filename(resource.getFilename())
                        .build());

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            }
        } catch (IOException e) {
            log.warn("Cannot read file bytes", e);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Fetch existing file from resource",
            description = "Fetch multipart file resource",
            tags = {"files"})
    @GetMapping("/fetchForEntity")
    public ResponseEntity<FileBase64BatchRequestDTO> fetchForEntity(@RequestParam("entityType") String entityType,
                                                                    @RequestParam("entityId") String entityId) {

        GridFSFindIterable gridFSFiles = fileService.getForEntity(entityId, ResourceEntityType.valueOf(entityType));
        if (gridFSFiles != null) {
            FileBase64BatchRequestDTO fileBase64BatchRequestDTO = FileBase64BatchRequestDTO.builder()
                    .entityId(entityId)
                    .entityType(entityType)
                    .files(new LinkedList<>())
                    .build();

            for (GridFSFile gridFSFile : gridFSFiles) {
                FileBase64DTO fileBase64DTO = FileBase64DTO.builder()
                        .fileName(gridFSFile.getFilename())
                        .build();
                try {
                    fileBase64DTO.setFileEncoded(Base64.getEncoder()
                            .encodeToString(fileService.getResource(gridFSFile).getInputStream().readAllBytes()));
                    fileBase64DTO.setSuccess(true);
                    fileBase64DTO.setId(gridFSFile.getId().asObjectId().getValue().toHexString());

                    fileBase64BatchRequestDTO.getFiles().add(fileBase64DTO);
                } catch (IOException e) {
                    log.warn("Cannot read file resource from stream from gridFsFile: {}", gridFSFile.getId(), e);
                } catch (NullPointerException npe) {
                    log.warn("File contents corrupted: {} ", gridFSFile.getId(), npe);
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

