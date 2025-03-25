package com.lifespace.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.model.SpaceUsage;
import com.lifespace.repository.SpaceUsageRepository;

@Service
public class SpaceUsageService {
	
	@Autowired
	private SpaceUsageRepository spaceUsageRepository;
	
	public List<SpaceUsage> getAllSpaceUsages() {  // 取得所有用途
		return spaceUsageRepository.findAll();
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
	
	public SpaceUsage updateSpaceUsage(String spaceUsageId, SpaceUsage spaceUsage) {
		SpaceUsage su = spaceUsageRepository.findById(spaceUsageId).orElse(null);
		
		if (su == null) {
			throw new ResourceNotFoundException("找不到ID 為「" + spaceUsageId + "」的用途");
		}
		su.setSpaceUsageName(spaceUsage.getSpaceUsageName());
		
		return spaceUsageRepository.save(su);
	}
	
	// 刪除：可以改狀態，在新增之前，先查詢全部是否已有用過那個名稱
	// 有用過：狀態改回來
	// 沒用過：新增
}
