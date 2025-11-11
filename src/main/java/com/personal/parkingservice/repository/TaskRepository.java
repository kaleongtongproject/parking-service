package com.personal.parkingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.parkingservice.entity.Task;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByPropertyId(UUID propertyId);
}
