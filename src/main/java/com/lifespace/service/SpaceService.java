package com.lifespace.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.model.Space;
import com.lifespace.model.SpaceEquipment;
import com.lifespace.repository.SpaceRepository;

@Service	
public class SpaceService {
	
	@Autowired
	private SpaceRepository spaceRepository;

	public List<Space> getAllSpaces() {  // 取得所有空間
		return spaceRepository.findAll();
	}

	public Space getSpaceById(String spaceId) {  // 透過id取得單一空間
		return spaceRepository.findById(spaceId).orElse(null);
	}

	public Space getSpaceBySpaceName(String spaceName) {  // 透過空間名稱取得單一空間
		return spaceRepository.findBySpaceName(spaceName).orElse(null);
	}

	public Space addSpace(Space space) {  // 新增空間
		// 前端就是送完整 Entity 結構，才可以這樣寫
		return spaceRepository.save(space);
	}
	
	// 含有巢狀關聯資料(space equipment)的新增做法
	public Space createSpaceWithEquipments(Space space) {
		Space s = new Space();
		s.setBranchId(space.getBranchId());
		s.setSpaceName(space.getSpaceName());
		s.setSpacePeople(space.getSpacePeople());
		s.setSpaceSize(space.getSpaceSize());
		s.setSpaceHourlyFee(space.getSpaceHourlyFee());
		s.setSpaceDailyFee(space.getSpaceDailyFee());
		s.setSpaceDesc(space.getSpaceDesc());
		s.setSpaceAlert(space.getSpaceAlert());
		s.setSpaceStatus(space.getSpaceStatus());
		s.setSpaceFloor(space.getSpaceFloor());
		
		// 將Space Entity(或DTO)轉為SpaceEquipment實體並設關聯
		Set<SpaceEquipment> equips = space.getSpaceEquipments().stream().map(se -> {
			SpaceEquipment equip = new SpaceEquipment();
			equip.setSpaceEquipName(se.getSpaceEquipName());
			equip.setSpaceEquipComment(se.getSpaceEquipComment());
			return equip;
		}).collect(Collectors.toSet());
		
        space.setSpaceEquipments(equips);

		return spaceRepository.save(space);  // CascadeType.ALL 會自動幫你存子表格的項目
	}
	
	// 含有巢狀關聯資料(space equipment)的刪除做法
	public Space updateSpace(String spaceId, Space space) {  // 修改空間
		Space s = spaceRepository.findById(spaceId).orElse(null);   // 檢查資料是否存在
		if (s == null) {
			throw new ResourceNotFoundException("找不到ID 為「 " + spaceId + " 」的空間");
		}
		s.setBranchId(space.getBranchId());
		s.setSpaceName(space.getSpaceName());
		s.setSpacePeople(space.getSpacePeople());
		s.setSpaceSize(space.getSpaceSize());
		s.setSpaceHourlyFee(space.getSpaceHourlyFee());
		s.setSpaceDailyFee(space.getSpaceDailyFee());
		s.setSpaceDesc(space.getSpaceDesc());
		s.setSpaceAlert(space.getSpaceAlert());
		s.setSpaceStatus(space.getSpaceStatus());
		s.setSpaceFloor(space.getSpaceFloor());
		
		// 將Space Entity(或DTO)轉為SpaceEquipment實體並設關聯
		// 取得舊的空間設備清單
		Set<SpaceEquipment> currentEquips = s.getSpaceEquipments();
		
		// 建立新的空間設備清單
		Set<SpaceEquipment> newEquips = space.getSpaceEquipments().stream().map(se -> {
			SpaceEquipment equip = new SpaceEquipment();
			equip.setSpaceEquipId(se.getSpaceEquipId());  // 若有值代表要更新
			equip.setSpaceEquipName(se.getSpaceEquipName());
			equip.setSpaceEquipComment(se.getSpaceEquipComment());
			equip.setSpace(s);  // 建立關聯
			return equip;
		}).collect(Collectors.toSet());
		
		// 清空原有的設備再重建
		currentEquips.clear();
		currentEquips.addAll(newEquips);

		return spaceRepository.save(s);   // CascadeType.ALL 會自動幫你存子表格的項目
	}
}
