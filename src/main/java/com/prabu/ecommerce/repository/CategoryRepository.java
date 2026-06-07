package com.prabu.ecommerce.repository;

import com.prabu.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Modifying
    @Query("Update Category c set c.description=:description, c.name=:name, c.imageUrl=:imageUrl where c.id=:id ")
    void update(Long id, String name, String description, String imageUrl);

}
