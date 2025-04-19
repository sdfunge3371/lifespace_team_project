package com.lifespace.controller;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.entity.SpacePhoto;
import com.lifespace.service.SpacePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SpacePhotoController {

    @Autowired
    private SpacePhotoService spacePhotoService;

    @GetMapping("/space-photo/space/{spaceId}")
    public ResponseEntity<List<SpacePhoto>> getSpaceUsageMapsBySpaceId(@PathVariable String spaceId) {
        List<SpacePhoto> sp = spacePhotoService.getSpacePhotosBySpaceId(spaceId);

        if (sp.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sp);
    }

    @GetMapping("/space-usage-map/id/{spacePhotoId}")
    public ResponseEntity<SpacePhoto> getSpaceEquipmentById(@PathVariable Integer spacePhotoId) {
        SpacePhoto sp = spacePhotoService.getSpacePhotoById(spacePhotoId);

        if (sp == null) {
            throw new ResourceNotFoundException("找不到ID 為「" + spacePhotoId + "」的照片");
        }
        return ResponseEntity.ok(sp);
    }

    //抓取訂單的空間封面
    @GetMapping("/space-photo/space/{spaceId}/cover")
    public ResponseEntity<byte[]> getCoverPhoto(@PathVariable String spaceId) {
        List<SpacePhoto> photos = spacePhotoService.getSpacePhotosBySpaceId(spaceId);

        if (photos.isEmpty() || photos.get(0).getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] image = photos.get(0).getPhoto();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG); //設定圖片副檔名

        return new ResponseEntity<>(image, httpHeaders, HttpStatus.OK);
    }
}
