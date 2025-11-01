package com.personal.facilityscope.repository;

import com.personal.facilityscope.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
    // You can add custom query methods here if needed
}
