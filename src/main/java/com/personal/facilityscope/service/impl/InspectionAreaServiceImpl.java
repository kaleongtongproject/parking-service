package com.personal.facilityscope.service.impl;

import com.personal.facilityscope.dto.InspectionAreaRequestDTO;
import com.personal.facilityscope.entity.InspectionArea;
import com.personal.facilityscope.entity.Property;
import com.personal.facilityscope.repository.InspectionAreaRepository;
import com.personal.facilityscope.repository.PropertyRepository;
import com.personal.facilityscope.service.InspectionAreaService;
import jakarta.persistence.EntityNotFoundException;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class InspectionAreaServiceImpl implements InspectionAreaService {

    private final InspectionAreaRepository inspectionAreaRepository;
    private final PropertyRepository propertyRepository;
    private final GeometryFactory geometryFactory;

    public InspectionAreaServiceImpl(
        InspectionAreaRepository inspectionAreaRepository,
        PropertyRepository propertyRepository,
        GeometryFactory geometryFactory
    ) {
        this.inspectionAreaRepository = inspectionAreaRepository;
        this.propertyRepository = propertyRepository;
        this.geometryFactory = geometryFactory;
    }

    @Override
    public InspectionArea create(InspectionArea inspectionArea) {
        return inspectionAreaRepository.save(inspectionArea);
    }

    @Override
    public List<InspectionArea> getAll() {
        return inspectionAreaRepository.findAll();
    }

    @Override
    public InspectionArea getById(UUID id) {
        return inspectionAreaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("InspectionArea not found"));
    }

    @Override
    public InspectionArea update(UUID id, InspectionArea updatedInspectionArea) {
        InspectionArea existing = getById(id);
        return inspectionAreaRepository.save(updatedInspectionArea);
    }

    @Override
    public void delete(UUID id) {
        inspectionAreaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public InspectionArea createFromDTO(InspectionAreaRequestDTO dto) {
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        Polygon polygon = convertToPolygon(dto.getGeometry().getCoordinates());

        InspectionArea area = new InspectionArea();
        area.setName(dto.getName());
        area.setProperty(property);
        area.setGeometry(polygon);
        return inspectionAreaRepository.save(area);
    }

    @Override
    @Transactional
    public InspectionArea updateFromDTO(UUID id, InspectionAreaRequestDTO dto) {
        InspectionArea existing = getById(id);

        existing.setName(dto.getName());
        existing.setGeometry(convertToPolygon(dto.getGeometry().getCoordinates()));
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        existing.setProperty(property);
        existing.setUpdatedAt(LocalDateTime.now());

        return inspectionAreaRepository.save(existing);
    }

    private Polygon convertToPolygon(List<List<List<Double>>> coords) {
        List<List<Double>> outerRing = coords.get(0);
        Coordinate[] coordinates = outerRing.stream()
                .map(point -> new Coordinate(point.get(0), point.get(1)))
                .toArray(Coordinate[]::new);

        // Ensure polygon is closed
        if (!coordinates[0].equals2D(coordinates[coordinates.length - 1])) {
            coordinates = Arrays.copyOf(coordinates, coordinates.length + 1);
            coordinates[coordinates.length - 1] = coordinates[0];
        }

        LinearRing shell = geometryFactory.createLinearRing(coordinates);
        return geometryFactory.createPolygon(shell);
    }
}
