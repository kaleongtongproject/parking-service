package com.personal.facilityscope.service;

import com.personal.facilityscope.entity.Property;
import com.personal.facilityscope.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropertyServiceTest {

    private PropertyRepository propertyRepository;
    private PropertyService propertyService;

    @BeforeEach
    void setUp() {
        propertyRepository = Mockito.mock(PropertyRepository.class);
        propertyService = new com.personal.facilityscope.service.impl.PropertyServiceImpl(propertyRepository);
    }

    @Test
    void testCreateProperty() {
        Property property = new Property();
        property.setName("Test Property");

        when(propertyRepository.save(any())).thenReturn(property);

        Property result = propertyService.create(property);
        assertEquals("Test Property", result.getName());
    }

    @Test
    void testGetById() {
        UUID id = UUID.randomUUID();
        Property property = new Property();
        property.setId(id);
        property.setName("Test");

        when(propertyRepository.findById(id)).thenReturn(Optional.of(property));

        Property found = propertyService.getById(id);
        assertEquals(id, found.getId());
    }
}
