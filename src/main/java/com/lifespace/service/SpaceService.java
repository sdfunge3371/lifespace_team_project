package com.lifespace.service;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.lifespace.dto.SpaceRequest;
import com.lifespace.entity.*;
import com.lifespace.repository.SpaceUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.exception.ResourceNotFoundException;
import com.lifespace.repository.SpaceRepository;
import org.springframework.web.multipart.MultipartFile;

@Service	
public class SpaceService {
	
	@Autowired
	private SpaceRepository spaceRepository;

    @Autowired
    private SpaceUsageRepository spaceUsageRepository;

	public List<Space> getAllSpaces() {  // 取得所有空間
		return spaceRepository.findAll();
	}

	public Space getSpaceById(String spaceId) {  // 透過id取得單一空間
		return spaceRepository.findById(spaceId).orElse(null);
	}

	public Space getSpaceBySpaceName(String spaceName) {  // 透過空間名稱取得單一空間
		return spaceRepository.findBySpaceName(spaceName).orElse(null);
	}

//	public Space addSpace(Space space) {  // 新增空間
//		// 前端就是送完整 Entity 結構，才可以這樣寫
//		return spaceRepository.save(space);
//	}
	
	// 含有巢狀關聯資料(space equipment, space photos)的新增做法
	public Space addSpace(SpaceRequest space, List<MultipartFile> files) throws IOException {

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

		// ============= 新增Space Equipments =============
		Set<SpaceEquipment> equips = space.getSpaceEquipments().stream().map(se -> {
			SpaceEquipment equip = new SpaceEquipment();
			equip.setSpaceEquipName(se.getSpaceEquipName());
			equip.setSpaceEquipComment(se.getSpaceEquipComment());
			equip.setSpace(s);  // 建立關聯，沒寫會報錯
			return equip;
		}).collect(Collectors.toSet());
        s.setSpaceEquipments(equips);

		// ============= 新增Space Photos =============
 		Set<SpacePhoto> photos = new LinkedHashSet<>();
		for (MultipartFile file : files) {
			SpacePhoto photo = new SpacePhoto();
			photo.setPhoto(file.getBytes());
			photo.setSpace(s); // 關聯回 Space
			photos.add(photo);
		}
		s.setSpacePhotos(photos);

		// ============= 新增Space Usage maps =============
		List<SpaceUsage> usages = spaceUsageRepository.findAllById(space.getSpaceUsageIds());

		// 3. 建立對應的 SpaceUsageMap
		Set<SpaceUsageMap> usageMaps = usages.stream().map(usage -> {
			SpaceUsageMap map = new SpaceUsageMap();
			map.setSpace(s);           // 關聯到 Space（必填）
			map.setSpaceUsage(usage);      // 關聯到 Usage（必填）
			return map;
		}).collect(Collectors.toSet());

		// 4. 把 usageMap 塞進 Space
		s.setSpaceUsageMaps(usageMaps);

		// 新增空間
		return spaceRepository.save(s);  // CascadeType.ALL 會自動幫你存子表格的項目
	}

	public Space updateSpace(String spaceId, SpaceRequest space, List<MultipartFile> files) throws IOException {  // 修改空間
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

		// ============= 修改Space Equipments =============

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

		// ============= 修改Space Photos =============

		// 舊照片處理
		// [法一] 選擇全部刪除再重新抓照片
		s.getSpacePhotos().clear();

		// [法二] 移除使用者沒保留的照片
//		Set<SpacePhoto> updatedPhotos = s.getSpacePhotos().stream()
//				.filter(photo -> retainedPhotoIds.contains(photo.getSpacePhotoId()))
//				.collect(Collectors.toSet());

		// 新增新照片
		Set<SpacePhoto> photos = new LinkedHashSet<>();
		for (MultipartFile file : files) {
			SpacePhoto photo = new SpacePhoto();
			photo.setPhoto(file.getBytes());
			photo.setSpace(s);
			photos.add(photo);
		}
		// 更新關聯
		s.setSpacePhotos(photos);

		// ============= 修改Space Usage maps =============

		// 清空舊的關聯
		s.getSpaceUsageMaps().clear();  // 清空舊的space usage maps

		List<SpaceUsage> us = spaceUsageRepository.findAllById(space.getSpaceUsageIds());
		Set<SpaceUsageMap> ums = us.stream().map(u -> {
			SpaceUsageMap map = new SpaceUsageMap();
			map.setSpace(s);
			map.setSpaceUsage(u);
			return map;
		}).collect(Collectors.toSet());

		s.setSpaceUsageMaps(ums);

		return spaceRepository.save(s);   // CascadeType.ALL 會自動幫你存子表格的項目
	}
}
