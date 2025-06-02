package com.revify.monolith.shoplift.repo;

import com.revify.monolith.shoplift.model.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {
    Boolean existsByName(String name);

    @Query("SELECT Shop FROM Shop p WHERE lower(p.name) LIKE lower(concat('%',:name,'%')) ORDER BY p.relevance")
    List<Shop> search(String name, Pageable pageable);
}
