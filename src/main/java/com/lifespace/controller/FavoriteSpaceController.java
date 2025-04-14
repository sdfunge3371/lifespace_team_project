package com.lifespace.controller;

import com.lifespace.SessionUtils;
import com.lifespace.dto.FavoriteSpaceDTO;
import com.lifespace.service.FavoriteSpaceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/favorite-space")
public class FavoriteSpaceController {

    @Autowired
    private FavoriteSpaceService favoriteSpaceService;

    /**
     * 取得我的最愛空間列表
     */
    @GetMapping
    public ResponseEntity<?> getFavoriteSpaces(HttpSession session) {
        // 從Session獲取會員ID
        String memberId = SessionUtils.getLoginMemberId(session);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
        }

        List<FavoriteSpaceDTO> favoriteSpaces = favoriteSpaceService.getFavoriteSpacesByMemberId(memberId);
        return ResponseEntity.ok(favoriteSpaces);
    }

    /**
     * 判斷會員是否有收藏空間
     */
    @GetMapping("/has-favorites")
    public ResponseEntity<?> hasFavoriteSpaces(HttpSession session) {
        String memberId = SessionUtils.getLoginMemberId(session);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
        }

        boolean hasFavorites = favoriteSpaceService.hasFavoriteSpaces(memberId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasFavorites", hasFavorites);
        return ResponseEntity.ok(response);
    }

    /**
     * 判斷特定空間是否已被收藏
     */
    @GetMapping("/check/{spaceId}")
    public ResponseEntity<?> checkFavoriteSpace(@PathVariable String spaceId, HttpSession session) {
        String memberId = SessionUtils.getLoginMemberId(session);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
        }

        boolean isFavorite = favoriteSpaceService.isFavoriteSpace(memberId, spaceId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", isFavorite);
        return ResponseEntity.ok(response);
    }

    /**
     * 新增空間到我的最愛
     */
    @PostMapping("/{spaceId}")
    public ResponseEntity<?> addFavoriteSpace(@PathVariable String spaceId, HttpSession session) {
        String memberId = SessionUtils.getLoginMemberId(session);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
        }

        FavoriteSpaceDTO added = favoriteSpaceService.addFavoriteSpace(memberId, spaceId);
        if (added == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("新增失敗，會員或空間不存在");
        }
        return ResponseEntity.ok(added);
    }

    /**
     * 從我的最愛移除空間
     */
    @DeleteMapping("/{spaceId}")
    public ResponseEntity<?> removeFavoriteSpace(@PathVariable String spaceId, HttpSession session) {
        String memberId = SessionUtils.getLoginMemberId(session);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
        }

        boolean removed = favoriteSpaceService.removeFavoriteSpace(memberId, spaceId);
        if (removed) {
            return ResponseEntity.ok("已從我的最愛中移除");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("移除失敗，可能未收藏該空間");
        }
    }

    /**
     * 取得空間照片
     */
    @GetMapping(value = "/image/{favoriteSpaceId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getFavoriteSpaceImage(@PathVariable Integer favoriteSpaceId, HttpSession session) {
        String memberId = SessionUtils.getLoginMemberId(session);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 在實際應用中，可能需要從FavoriteSpaceDTO中取得照片
        // 此處暫時返回null，實際實現時應該返回空間的照片
        return ResponseEntity.ok(null);
    }
}