package com.personal.parkingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.parkingservice.entity.Property;

import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
    // You can add custom query methods here if needed
}
