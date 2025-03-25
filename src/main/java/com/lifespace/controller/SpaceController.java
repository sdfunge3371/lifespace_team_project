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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.model.Space;
import com.lifespace.service.SpaceService;

import jakarta.validation.Valid;


// 直接回傳 JSON，前端可以用 AJAX 調用
@RestController
public class SpaceController {
	
	@Autowired
	private SpaceService spaceService;

	@GetMapping("/spaces")
	public ResponseEntity<List<Space>> getAllSpaces() {
		List<Space> allSpaces = spaceService.getAllSpaces();
		if (allSpaces.isEmpty()) {    // 如果沒有資料時
		    return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(allSpaces);
	}

	@GetMapping("/spaces/id/{spaceId}")
	public ResponseEntity<Space> getSpaceById(@PathVariable	String spaceId) {
		Space space = spaceService.getSpaceById(spaceId);
		
		if (space == null) {   // 若這個spaceId沒有資料
			throw new ResourceNotFoundException("找不到 ID 為「 " + spaceId + " 」的空間");   // ResourceNotFoundException為我自己定義的Exception
		}
		return ResponseEntity.ok(space);
	}
	
	@GetMapping("/spaces/name")
	public ResponseEntity<Space> getSpaceBySpaceName(@RequestParam String spaceName) {  // 用@RequestParam以讓Postman處理空白字元
		Space space = spaceService.getSpaceBySpaceName(spaceName);
		
		if (space == null) {   // 若這個spaceName沒有資料
			throw new ResourceNotFoundException("找不到空間名稱為「 " + spaceName + " 」的空間");
		}
		return ResponseEntity.ok(space);
	}
	
	// 這個會改成下面的
	@PostMapping("/spaces")
	public ResponseEntity<?> addSpace(@RequestBody @Valid Space space) {   // ?: 可回傳任何型別
	    try {
	        Space created = spaceService.addSpace(space);
	        URI location = URI.create("/spaces/id/" + created.getSpaceId());
	        return ResponseEntity.created(location).body(created); // 201 Created
	    } catch (DataIntegrityViolationException e) {
	        // 回傳 409 Conflict，並可加入錯誤訊息
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("此空間名稱已經被使用過");
	    }
	}
	
	@PostMapping("/spacesequiptest")
	public ResponseEntity<?> createSpaceWithEquipments(@RequestBody @Valid Space space) {   // ?: 可回傳任何型別
	    try {
	        Space created = spaceService.createSpaceWithEquipments(space);
	        URI location = URI.create("/spaces/id/" + created.getSpaceId());
	        return ResponseEntity.created(location).body(created); // 201 Created
	    } catch (DataIntegrityViolationException e) {
	        // 回傳 409 Conflict，並可加入錯誤訊息
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("此空間名稱已經被使用過");
	    }
	}
	
	@PutMapping("/spaces/id/{spaceId}")
	public ResponseEntity<?> updateSpace(@PathVariable String spaceId, @RequestBody @Valid Space space) {
		try {
			Space updated = spaceService.updateSpace(spaceId, space);
	        return ResponseEntity.ok(updated);
		} catch (DataIntegrityViolationException e) {
			// 回傳 409 Conflict，並可加入錯誤訊息
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("此空間名稱已經被使用過");
		}
	}
	
	// More methods...
}