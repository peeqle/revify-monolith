package com.revify.monolith.shoplift.repo;

import com.revify.monolith.shoplift.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {
    Boolean existsByName(String name);
}
