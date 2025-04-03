package com.lifespace.controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lifespace.dto.SpaceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.entity.Space;
import com.lifespace.service.SpaceService;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;


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
	public ResponseEntity<List<Space>> getSpacesByNameContainingIgnoreCase(@RequestParam String keyword) {  // 用@RequestParam以讓Postman處理空白字元
		List<Space> spaces = spaceService.getSpacesByNameContainingIgnoreCase(keyword);

		if (spaces.isEmpty()) {
			throw new ResourceNotFoundException("找不到包含「" + keyword + "」的空間");
		}

		return ResponseEntity.ok(spaces);

	}
	
	@PostMapping("/spaces")  // 需使用multipart/form-data + JSON + 檔案格式提交
	public ResponseEntity<?> addSpace(@RequestPart("data") @Valid SpaceRequest space,
									  @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
	    try {
	        Space created = spaceService.addSpace(space, photos);
	        URI location = URI.create("/spaces/id/" + created.getSpaceId());
	        return ResponseEntity.created(location).body(created); // 201 Created
	    } catch (DataIntegrityViolationException e) {
	        // 回傳 409 Conflict，並可加入錯誤訊息
			Map<String, String> errorBody = new HashMap<>();
			errorBody.put("message", "此空間名稱已經被使用過");
			return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(errorBody);  // .contentType(MediaType.APPLICATION_JSON):確保該回傳型別不論為何，都是指定為application/json，
	    } catch (IOException e) {
			Map<String, String> errorBody = new HashMap<>();
			errorBody.put("message", "空間照片新增失敗：" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errorBody);
		} catch (IllegalArgumentException e) {
			Map<String, String> errorBody = new HashMap<>();
			errorBody.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(errorBody);
		}
	}
	
	@PutMapping("/spaces/{spaceId}")  // 需使用multipart/form-data + JSON + 檔案格式提交
	public ResponseEntity<?> updateSpace(@PathVariable String spaceId,
										 @RequestPart("data") @Valid SpaceRequest space,
										 @RequestPart(value = "photos", required = false) List<MultipartFile> files) {
		try {
			Space updated = spaceService.updateSpace(spaceId, space, files != null ? files : List.of());
	        return ResponseEntity.ok(updated);
		} catch (DataIntegrityViolationException e) {
			// 回傳 409 Conflict，並可加入錯誤訊息
			Map<String, String> errorBody = new HashMap<>();
			errorBody.put("message", "此空間名稱已經被使用過");
			return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(errorBody);
		} catch (IOException e) {
			Map<String, String> errorBody = new HashMap<>();
			errorBody.put("message", "空間照片新增失敗：" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errorBody);
		}
	}
	
	// More methods...
}