package com.personal.parkingservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.personal.parkingservice.entity.Property;
import com.personal.parkingservice.service.PropertyService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        return ResponseEntity.ok(propertyService.create(property));
    }

    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable UUID id) {
        return ResponseEntity.ok(propertyService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable UUID id, @RequestBody Property updatedProperty) {
        return ResponseEntity.ok(propertyService.update(id, updatedProperty));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
