package com.lifespace.controller;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.entity.SpaceUsageMap;
import com.lifespace.service.SpaceUsageMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SpaceUsageMapController {

    @Autowired
    private SpaceUsageMapService spaceUsageMapService;

    @GetMapping("/space-usage-map/space/{spaceId}")
    public ResponseEntity<List<SpaceUsageMap>> getSpaceUsageMapsBySpaceId(@PathVariable String spaceId) {
        List<SpaceUsageMap> um = spaceUsageMapService.getSpaceUsageMapsBySpaceId(spaceId);

        if (um.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(um);
    }

    @GetMapping("/space-usage-map/id/{usageMappingId}")
    public ResponseEntity<SpaceUsageMap> getSpaceEquipmentById(@PathVariable Integer usageMappingId) {
        SpaceUsageMap um = spaceUsageMapService.getSpaceEquipmentById(usageMappingId);

        if (um == null) {
            throw new ResourceNotFoundException("找不到ID 為「" + usageMappingId + "」的關聯資訊");
        }
        return ResponseEntity.ok(um);
    }
}
