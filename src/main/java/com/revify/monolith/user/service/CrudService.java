package com.revify.monolith.user.service;

import com.revify.monolith.user.models.user.AppUser;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
public abstract class CrudService<T> {

    public <T extends AppUser> T store(T t) {
        try {
            t.setCreatedOnServerUtc(System.currentTimeMillis());
            JpaRepository<T, ?> repository = (JpaRepository<T, ?>) getRepository();
            return repository.save(t);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new RuntimeException("Cannot store app user", e);
        }
    }

    public abstract JpaRepository<T, ?> getRepository();
}
