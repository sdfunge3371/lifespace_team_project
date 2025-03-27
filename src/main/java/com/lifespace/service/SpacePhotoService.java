package com.lifespace.service;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.entity.Space;
import com.lifespace.entity.SpacePhoto;
import com.lifespace.repository.SpacePhotoRepository;
import com.lifespace.repository.SpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpacePhotoService {

    @Autowired
    private SpacePhotoRepository spacePhotoRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    public List<SpacePhoto> getSpacePhotosBySpaceId(String spaceId) {  // 透過spaceId取得該空間的所有空間設備
        Optional<Space> spaceOptional = spaceRepository.findById(spaceId);

        if (spaceOptional.isPresent()) {
            Space space = spaceOptional.get();
            return spacePhotoRepository.findBySpace(space);
        } else {
            throw new ResourceNotFoundException("找不到ID 為「 " + spaceId + " 」的空間");
        }
    }

    public SpacePhoto getSpacePhotoById(Integer spacePhotoId) {   // 透過SpaceEquipemntId取得單筆資料
        return spacePhotoRepository.findById(spacePhotoId).orElse(null);
    }
}
