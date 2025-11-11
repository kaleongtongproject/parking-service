package com.personal.parkingservice.service;

import java.util.List;
import java.util.UUID;

import com.personal.parkingservice.entity.Property;

public interface PropertyService {
    Property create(Property property);

    List<Property> getAll();

    Property getById(UUID id);

    Property update(UUID id, Property updatedProperty);

    void delete(UUID id);
}
