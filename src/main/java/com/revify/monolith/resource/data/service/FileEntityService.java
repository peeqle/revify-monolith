package com.revify.monolith.resource.data.service;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.resource.data.models.EntityFile;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileEntityService {

    private final MongoTemplate template;

    public void saveUserFileRelation(String fileId) {
        saveFileRelation(UserUtils.getKeycloakId(), fileId);
    }

    public void saveFileRelation(String entityId, String fileId) {
        template.save(new EntityFile(entityId, fileId));
    }

    public Tuple2<Long, List<EntityFile>> deleteUserFileRelation(String fileId) {
        return deleteFileRelation(UserUtils.getKeycloakId(), fileId);
    }

    public Tuple2<Long, List<EntityFile>> deleteFileRelation(String entityId, String fileId) {
        List<EntityFile> allAndRemove = template.findAllAndRemove(Query.query(
                Criteria.where("entityId").is(entityId)
                        .and("customFileId").is(fileId)
        ), EntityFile.class);
        Long existingFileTraces = template.count(Query.query(Criteria.where("customFileId").is(fileId)), EntityFile.class);

        return Tuple.of(existingFileTraces, allAndRemove);
    }
}
