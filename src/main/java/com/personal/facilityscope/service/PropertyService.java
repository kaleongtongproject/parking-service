package com.personal.facilityscope.service;

import com.personal.facilityscope.entity.Property;

import java.util.List;
import java.util.UUID;

public interface PropertyService {
    Property create(Property property);
    List<Property> getAll();
    Property getById(UUID id);
    Property update(UUID id, Property updatedProperty);
    void delete(UUID id);
}
