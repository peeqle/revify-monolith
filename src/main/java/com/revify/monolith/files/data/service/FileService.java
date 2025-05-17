package com.revify.monolith.files.data.service;


import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.revify.monolith.commons.models.ResourceEntityType;
import com.revify.monolith.files.data.models.CustomFile;
import com.revify.monolith.files.data.models.FileOptions;
import com.revify.monolith.files.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileService {
    private final GridFsTemplate template;

    private final MongoDatabaseFactory mongoDatabaseFactory;

    private final FileUtils fileUtils;

    public ObjectId store(FileOptions fileOptions, MultipartFile multipartFile) throws IOException, TikaException {
        try (ByteArrayInputStream is = new ByteArrayInputStream(multipartFile.getBytes())) {
            is.mark(1);
            String fileHash = DigestUtils.md5Hex(is);
            GridFSFile hashFile = findByHash(fileHash);
            if (hashFile == null) {
                is.reset();
                String filename = multipartFile.getOriginalFilename();
                File file = fileUtils.createTemporaryFile(is, filename);

                CustomFile metadata = prepareNewFileMetadata(file, fileHash, filename);
                metadata.setFileOptions(fileOptions);

                ObjectId store = template.store(
                        new FileInputStream(file),
                        FilenameUtils.getName(filename),
                        metadata
                );
                file.delete();
                return store;
            } else {
                CustomFile metadata = prepareExistingFileMetadata(fileHash, hashFile.getFilename());

                GridFsResource resource = getResource(hashFile);
                metadata.setFileOptions(fileOptions);

                return template.store(
                        resource.getInputStream(),
                        hashFile.getFilename(),
                        metadata
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String fileId) {
        var database = mongoDatabaseFactory.getMongoDatabase();
        var gridFSBucket = GridFSBuckets.create(database);

        GridFSFile gridFsFile = gridFSBucket.find(Filters.eq("_id", fileId)).first();

        if (gridFsFile != null) {
            Document metadata = gridFsFile.getMetadata();
            if (metadata != null) {
                Object fileOptionsObj = metadata.get("fileOptions");
                if (fileOptionsObj instanceof FileOptions fileOptions) {
//                    Long userId = fileOptions.getUserId();
//                    if (userId != null && userId.equals(userContext.getUserId())) {
//                        metadata.put("active", false);
//                        Document update = new Document("$set", new Document("metadata", metadata));
//                        database.getCollection("fs.files").updateOne(Filters.eq("_id", fileId), update);
//                    }
                }
            }
        }
    }

    public GridFSFile findByHash(String fileHash) {
        return template.findOne(Query.query(Criteria.where("metadata.fileHash").is(fileHash)));
    }

    public GridFSFindIterable getById(ObjectId fileId) {
        return template.find(Query.query(Criteria.where("_id").is(fileId)));
    }

    public GridFSFindIterable getForEntity(String entityId, ResourceEntityType entityType) {
        return template.find(
                Query.query(
                                Criteria.where("metadata.fileOptions.entityId").is(entityId)
                                        .and("metadata.fileOptions.entityType").is(entityType))
                        .with(Sort.by(Sort.Direction.DESC, "metadata.fileOptions.order"))
        );
    }

    public GridFsResource getResource(GridFSFile fsFile) {
        return template.getResource(fsFile);
    }

    public CustomFile prepareNewFileMetadata(File tempFile, String fileHash, String filename) throws IOException {
        CustomFile preparedCustomFile = new CustomFile();
        preparedCustomFile.setCreatedAt(System.currentTimeMillis());
        preparedCustomFile.setFileHash(fileHash);
        preparedCustomFile.setInitialFileExtension(FilenameUtils.getExtension(filename));
        preparedCustomFile.setSystemFileName(tempFile.getName());
        preparedCustomFile.setInitialFileName(filename);
        return preparedCustomFile;
    }

    public CustomFile prepareExistingFileMetadata(String fileHash, String filename) {
        CustomFile preparedCustomFile = new CustomFile();
        preparedCustomFile.setCreatedAt(System.currentTimeMillis());
        preparedCustomFile.setFileHash(fileHash);
        preparedCustomFile.setInitialFileExtension(FilenameUtils.getExtension(filename));
        preparedCustomFile.setSystemFileName(filename);
        preparedCustomFile.setInitialFileName(filename);
        return preparedCustomFile;
    }
}
