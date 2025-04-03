package com.lifespace.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.entity.SpaceEquipment;
import com.lifespace.service.SpaceEquipmentService;

import jakarta.validation.Valid;

@RestController
public class SpaceEquipmentController {
	
	@Autowired
	private SpaceEquipmentService spaceEquipmentService;

	// 僅作測試用
	@GetMapping("/space-equips/space/{spaceId}")
    public ResponseEntity<List<SpaceEquipment>> getSpaceEquipmentsBySpaceId(@PathVariable String spaceId) {
        List<SpaceEquipment> se = spaceEquipmentService.getSpaceEquipmentsBySpaceId(spaceId);
        
        if (se.isEmpty()) {
        	return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(se);
    }
	
	@GetMapping("/space-equips/id/{spaceEquipId}")
	public ResponseEntity<SpaceEquipment> getSpaceEquipmentById(@PathVariable Integer spaceEquipId) {
		SpaceEquipment se = spaceEquipmentService.getSpaceEquipmentById(spaceEquipId);
		if (se == null) {
			throw new ResourceNotFoundException("找不到ID 為「 " + spaceEquipId + " 」的空間設備");
		}
		
		return ResponseEntity.ok(se);
	}
	
	// 直接在Space表單用，目前應該不會用到
	@PostMapping("/spaces/space-equips")
	public ResponseEntity<SpaceEquipment> addSpaceEquipment(@RequestBody @Valid SpaceEquipment spaceEquipment) {
		SpaceEquipment created = spaceEquipmentService.addSpaceEquipment(spaceEquipment);
		URI location = URI.create("/space-equips/id/" + created.getSpaceEquipId());
        return ResponseEntity.created(location).body(created); // 201 Created
	}
	
	// 直接在Space表單用，目前應該不會用到
	@PutMapping("/spaces/space_equips/id/{spaceEquipId}")
	public ResponseEntity<SpaceEquipment> updateSpaceEquipment(@PathVariable Integer spaceEquipId, @RequestBody @Valid SpaceEquipment spaceEquipment) {
		SpaceEquipment updated = spaceEquipmentService.updateSpaceEquipment(spaceEquipId, spaceEquipment);
        return ResponseEntity.ok(updated);
	}
	
}