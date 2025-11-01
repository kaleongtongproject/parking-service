package com.personal.facilityscope.mapper;

import com.personal.facilityscope.dto.InspectionAreaResponseDTO;
import com.personal.facilityscope.entity.InspectionArea;
import org.locationtech.jts.geom.Coordinate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InspectionAreaMapper {

    public static InspectionAreaResponseDTO toDto(InspectionArea area) {
        InspectionAreaResponseDTO dto = new InspectionAreaResponseDTO();
        dto.setId(area.getId());
        dto.setName(area.getName());
        dto.setPropertyId(area.getProperty().getId());
        dto.setPropertyName(area.getProperty().getName());

        Coordinate[] coords = area.getGeometry().getCoordinates();
        List<List<Double>> ring = Arrays.stream(coords)
            .map(c -> List.of(c.getX(), c.getY()))
            .collect(Collectors.toList());

        dto.setCoordinates(List.of(ring)); // GeoJSON-style wrapping
        return dto;
    }
}
