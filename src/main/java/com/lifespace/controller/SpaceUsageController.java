package com.lifespace.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.model.SpaceUsage;
import com.lifespace.service.SpaceUsageService;

import jakarta.validation.Valid;

@RestController
public class SpaceUsageController {
	
	@Autowired
	private SpaceUsageService spaceUsageService;
	
	@GetMapping("/spaceusages")
	public ResponseEntity<List<SpaceUsage>> getAllSpaceUsages() {
		List<SpaceUsage> allSpaceUsages = spaceUsageService.getAllSpaceUsages();
		if (allSpaceUsages.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(allSpaceUsages);
	}
	
	@GetMapping("/spaceusages/id/{spaceUsageId}")
	public ResponseEntity<SpaceUsage> getSpaceUsageById(@PathVariable String spaceUsageId) {
		SpaceUsage spaceUsage = spaceUsageService.getSpaceUsageById(spaceUsageId);
		
		if (spaceUsage == null) {
			throw new ResourceNotFoundException("找不到ID 為「" + spaceUsageId + "」的用途");
		}
		return ResponseEntity.ok(spaceUsage);
	}
	
	@PostMapping("/spaceusages")
	public ResponseEntity<?> addSpaceUsage(@RequestBody @Valid SpaceUsage spaceUsage) {
		try {
	        SpaceUsage created = spaceUsageService.addSpaceUsage(spaceUsage);
	        URI location = URI.create("/spaceusages/id/" + created.getSpaceUsageId());
	        return ResponseEntity.created(location).body(created); // 201 Created
	    } catch (DataIntegrityViolationException e) {
	        // 回傳 409 Conflict，並可加入錯誤訊息
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("此用途名稱已經被使用過");
	    }
	}
	
	@PutMapping("/spaceusages/id/{spaceUsageId}")
	public ResponseEntity<?> updateSpaceUsage(@PathVariable String spaceUsageId, @RequestBody @Valid SpaceUsage spaceUsage) {
		return null;
	}
}
