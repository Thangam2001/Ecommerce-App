package com.prabu.ecommerce.repository;

import com.prabu.ecommerce.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);

    @Modifying
    @Query("Update Brand b set b.description=:description, b.name=:name where b.id=:id ")
    void update(Long id, String name, String description);
}
