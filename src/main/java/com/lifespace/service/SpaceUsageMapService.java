package com.lifespace.service;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.entity.Space;
import com.lifespace.entity.SpaceUsageMap;
import com.lifespace.repository.SpaceRepository;
import com.lifespace.repository.SpaceUsageMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpaceUsageMapService {

    @Autowired
    private SpaceUsageMapRepository spaceUsageMapRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    // 透過spaceId取得該空間的所有空間用途 (僅測試用)
    public List<SpaceUsageMap> getSpaceUsageMapsBySpaceId(String spaceId) {
        Optional<Space> spaceOptional = spaceRepository.findById(spaceId);

        if (spaceOptional.isPresent()) {
            Space space = spaceOptional.get();
            return spaceUsageMapRepository.findBySpace(space);
        } else {
            throw new ResourceNotFoundException("找不到ID 為「 " + spaceId + " 」的空間");
        }
    }

    // 透過usageMappingId取得單筆空間用途 (僅測試用)
    public SpaceUsageMap getSpaceEquipmentById(Integer usageMappingId) {   // 透過SpaceEquipemntId取得單筆資料
        return spaceUsageMapRepository.findById(usageMappingId).orElse(null);
    }



}
