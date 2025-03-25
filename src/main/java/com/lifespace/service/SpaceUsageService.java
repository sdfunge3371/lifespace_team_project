package com.lifespace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.constant.SpaceUsageStatus;
import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.model.SpaceUsage;
import com.lifespace.repository.SpaceUsageRepository;

@Service
public class SpaceUsageService {
	
	@Autowired
	private SpaceUsageRepository spaceUsageRepository;
	
	public List<SpaceUsage> getAllSpaceUsages(SpaceUsageStatus spaceUsageStatus) {  // 只找出「可用」的項目
		return spaceUsageRepository.findBySpaceUsageStatus(spaceUsageStatus);
	}
	
	public SpaceUsage getSpaceUsageById(String spaceUsageId) {
		return spaceUsageRepository.findById(spaceUsageId).orElse(null);
	}
	
	public SpaceUsage getSpaceUsageBySpaceName(String spaceName) {
		return spaceUsageRepository.findBySpaceUsageName(spaceName).orElse(null);
	}
	
	public SpaceUsage addSpaceUsage(SpaceUsage spaceUsage) {
		return spaceUsageRepository.save(spaceUsage);
	}
	
	// 刪除：利用改狀態刪除
	public void softDeleteById(String spaceUsageId) {
		Optional<SpaceUsage> su = spaceUsageRepository.findById(spaceUsageId);
		
		if (su.isPresent()) {
			SpaceUsage spaceUsage = su.get();
			spaceUsage.setSpaceUsageStatus(SpaceUsageStatus.DELETED);  // 刪除資料
			spaceUsageRepository.save(spaceUsage);
		} else {
			throw new ResourceNotFoundException("找不到ID為 " + spaceUsageId + " 的用途");
		}
	}	
}
