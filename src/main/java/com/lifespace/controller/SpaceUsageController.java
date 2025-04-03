package com.lifespace.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.constant.SpaceUsageStatus;
import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.entity.SpaceUsage;
import com.lifespace.service.SpaceUsageService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
public class SpaceUsageController {

	@Autowired
	private SpaceUsageService spaceUsageService;

	@GetMapping("/space-usages")
	public ResponseEntity<List<SpaceUsage>> getAllSpaceUsages() {	// 僅取得所有「可用」的資料
		List<SpaceUsage> allSpaceUsages = spaceUsageService.getAllSpaceUsages(SpaceUsageStatus.AVAILABLE);
		if (allSpaceUsages.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(allSpaceUsages);
	}

	@GetMapping("/space-usages/id/{spaceUsageId}")
	public ResponseEntity<SpaceUsage> getSpaceUsageById(@PathVariable String spaceUsageId) {
		SpaceUsage spaceUsage = spaceUsageService.getSpaceUsageById(spaceUsageId);

		if (spaceUsage == null) {
			throw new ResourceNotFoundException("找不到ID 為「" + spaceUsageId + "」的用途");
		}
		return ResponseEntity.ok(spaceUsage);
	}

	@PostMapping(value = "/space-usages", consumes = "application/json")
	public ResponseEntity<?> addSpaceUsage(@RequestBody @Valid SpaceUsage spaceUsage) {
		try {
	        SpaceUsage created = spaceUsageService.addSpaceUsage(spaceUsage);
	        URI location = URI.create("/space-usages/id/" + created.getSpaceUsageId());
	        return ResponseEntity.created(location).body(created); // 201 Created
	    } catch (DataIntegrityViolationException e) {
	        // 回傳 409 Conflict，並可加入錯誤訊息
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("此用途名稱已經被使用過");
	    }
	}

	@PutMapping("/space-usages/id/{spaceUsageId}/soft-delete")
	public ResponseEntity<?> softDeleteById(@PathVariable String spaceUsageId) {
	    try {
	        spaceUsageService.softDeleteById(spaceUsageId);
	        return ResponseEntity.ok("刪除成功（已標記為已刪除）");
	    } catch (EntityNotFoundException e) {   // 實體找不到
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    }
	}
}
