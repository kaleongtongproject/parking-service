package com.personal.facilityscope.service.impl;

import com.personal.facilityscope.entity.Property;
import com.personal.facilityscope.repository.PropertyRepository;
import com.personal.facilityscope.service.PropertyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public Property create(Property property) {
        return propertyRepository.save(property);
    }

    @Override
    public List<Property> getAll() {
        return propertyRepository.findAll();
    }

    @Override
    public Property getById(UUID id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
    }

    @Override
    public Property update(UUID id, Property updatedProperty) {
        Property existing = getById(id);
        existing.setName(updatedProperty.getName());
        existing.setAddress(updatedProperty.getAddress());
        return propertyRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        propertyRepository.deleteById(id);
    }
}
