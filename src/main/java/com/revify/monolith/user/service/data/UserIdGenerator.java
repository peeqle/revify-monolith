package com.revify.monolith.user.service.data;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class UserIdGenerator implements IdentifierGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return (Long) session.createNativeQuery("SELECT nextval('my_entity_sequence')").uniqueResult();
    }
}
