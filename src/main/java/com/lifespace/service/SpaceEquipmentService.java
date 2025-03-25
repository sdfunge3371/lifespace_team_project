package com.lifespace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.model.Space;
import com.lifespace.model.SpaceEquipment;
import com.lifespace.repository.SpaceEquipmentRepository;
import com.lifespace.repository.SpaceRepository;

// 如果你改成「新增完設備就馬上送出到資料庫」的設計（ex：儲存在伺服器 session、草稿區、或共用設備清單），那就需要設備的單獨 API。

// 但你現在是「最後一起送出」的設計，所以後端只要支援 巢狀資料一次接收與處理 就行
@Service
public class SpaceEquipmentService {
	
	@Autowired
	private SpaceEquipmentRepository spaceEquipmentRepository;
	
	@Autowired
	private SpaceRepository spaceRepository;
	
	// 直接在Space表單用，目前應該不會用到
	public List<SpaceEquipment> getSpaceEquipmentsBySpaceId(String spaceId) {  // 透過spaceId取得該空間的所有空間設備
		Optional<Space> spaceOptional = spaceRepository.findById(spaceId);
		
		if (spaceOptional.isPresent()) {
            Space space = spaceOptional.get();
            return spaceEquipmentRepository.findBySpace(space);
        } else {
            throw new ResourceNotFoundException("找不到ID 為「 " + spaceId + " 」的空間");
        }
	}
	
	// 直接在Space表單用，目前應該不會用到
	public SpaceEquipment getSpaceEquipmentById(Integer spaceEquipId) {   // 透過SpaceEquipemntId取得單筆資料
		return spaceEquipmentRepository.findById(spaceEquipId).orElse(null);
	}

	// 直接在Space表單用，目前應該不會用到
	public SpaceEquipment addSpaceEquipment(SpaceEquipment spaceEquipment) {
		return spaceEquipmentRepository.save(spaceEquipment);
	}

	// 直接在Space表單用，目前應該不會用到
	public SpaceEquipment updateSpaceEquipment(Integer spaceEquipId, SpaceEquipment spaceEquipment) {
		SpaceEquipment se = spaceEquipmentRepository.findById(spaceEquipId).orElse(null);
		
		if (se == null) {
			throw new ResourceNotFoundException("找不到ID 為「 " + spaceEquipId + " 」的空間設備");
		}
		
		// ?????
		se.setSpaceEquipName(spaceEquipment.getSpaceEquipName());
		se.setSpaceEquipComment(spaceEquipment.getSpaceEquipComment());
		
		return spaceEquipmentRepository.save(se);
	}
}
