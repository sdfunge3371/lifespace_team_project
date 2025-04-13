package com.lifespace.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.lifespace.dto.SpaceCommentResponse;
import com.lifespace.dto.SpaceEquipmentRequest;
import com.lifespace.dto.SpaceRequest;
import com.lifespace.entity.*;
import com.lifespace.repository.OrdersRepository;
import com.lifespace.repository.SpaceUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    @Autowired
    private OrdersRepository ordersRepository;

	public List<Space> getAllSpaces() {  // 取得所有空間
		List<Space> allSpaces = spaceRepository.findAll();
		recalculateAllSpaceRatings(allSpaces);
		return allSpaces;
	}

	public Space getSpaceById(String spaceId) {  // 透過id取得單一空間
		return spaceRepository.findById(spaceId).orElse(null);
	}

	public List<Space> getSpacesByNameContainingIgnoreCase(String keyword) {  // 透過空間名稱取得單一空間
		return spaceRepository.findBySpaceNameContainingIgnoreCase(keyword);
	}
	
	// 含有巢狀關聯資料(space equipment, space photos)的新增做法
	public Space addSpace(SpaceRequest space, List<MultipartFile> files) throws IOException {

		Space s = new Space();
		// 設定分點ID與其他欄位
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

		// 設定關聯分點
		Branch branch = new Branch();
		branch.setBranchId(space.getBranchId());
		s.setBranch(branch);

		// ============= 新增Space Equipments =============
		Set<SpaceEquipment> equips = space.getSpaceEquipments().stream().map(se -> {
			SpaceEquipment equip = new SpaceEquipment();
			equip.setSpaceEquipName(se.getSpaceEquipName());
			equip.setSpace(s);  // 建立關聯，沒寫會報錯
			return equip;
		}).collect(Collectors.toSet());
        s.setSpaceEquipments(equips);

		// ============= 新增Space Photos =============
 		Set<SpacePhoto> photos = new LinkedHashSet<>();

	 	if (files == null || files.isEmpty()) {
			 throw new IllegalArgumentException("請至少上傳一張照片");
		}

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

	public Space updateSpace(String spaceId, SpaceRequest space, List<MultipartFile> files, List<Integer> keptPhotoIds) throws IOException {  // 修改空間
		Space s = spaceRepository.findById(spaceId).orElse(null);   // 檢查資料是否存在
		if (s == null) {
			throw new ResourceNotFoundException("找不到ID 為「 " + spaceId + " 」的空間");
		}
		// 要跟 branch 關聯，才會更新branch的相關資料
		Branch branch = new Branch();
		branch.setBranchId(space.getBranchId());
		s.setBranch(branch);

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
		Set<SpaceEquipment> targetEquipments = s.getSpaceEquipments();
		targetEquipments.clear();

		for (SpaceEquipmentRequest se : space.getSpaceEquipments()) {
			SpaceEquipment equip = new SpaceEquipment();
		equip.setSpaceEquipName(se.getSpaceEquipName());
			equip.setSpace(s);
			targetEquipments.add(equip);
		}

		// ============= 修改Space Photos =============

		// 取得現有照片且未被刪除的
		Set<SpacePhoto> originalPhotos = s.getSpacePhotos();
		for (var photo : originalPhotos) {
			System.out.print(photo.getSpacePhotoId() + " ");
		}
		System.out.println();
		originalPhotos.removeIf(photo -> !keptPhotoIds.contains(photo.getSpacePhotoId()));

		// 加入新照片
		if (files != null) {   // 不要加files.isEmpty() 因為可能照片沒有新增
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					SpacePhoto photo = new SpacePhoto();
					photo.setPhoto(file.getBytes());
					photo.setSpace(s);
					originalPhotos.add(photo);		// 將新加入的photo直接丟進originalPhoto中，作為修改後的照片陣列
				}
			}
		}

		if (originalPhotos.isEmpty()) {		// 修改後若為空
			throw new IllegalArgumentException("請至少保留或上傳一張照片");
		}

		// ============= 修改Space Usage Maps =============
		Set<SpaceUsageMap> targetUsageMaps = s.getSpaceUsageMaps();
		targetUsageMaps.clear();
		List<SpaceUsage> usages = spaceUsageRepository.findAllById(space.getSpaceUsageIds());
		for (SpaceUsage usage : usages) {
			SpaceUsageMap map = new SpaceUsageMap();
			map.setSpace(s);
			map.setSpaceUsage(usage);
			targetUsageMaps.add(map);
		}

		return spaceRepository.save(s);   // CascadeType.ALL + orphanRemoval = true
	}

	// 切換上、下架狀態
	public Space toggleStatus(String spaceId, Map<String, String> body) {
		String newStatus = body.get("status");
		Space s = spaceRepository.findById(spaceId).orElse(null);

		if (s == null) {
			throw new ResourceNotFoundException("找不到ID 為「 " + spaceId + " 」的空間");
		}

		// 調整空間狀態
		System.out.println(newStatus);
		s.setSpaceStatus(newStatus.equals("1") ? 1 : 0);
		s.setSpaceStatusText(newStatus.equals("1") ? "上架中" : "未上架");

		return spaceRepository.save(s);
	}

	// 計算個別空間的平均滿意度
	public void updateSpaceRating(String spaceId) {
		System.out.println("test");
		Double avgRating = spaceRepository.findAverageSatisfactionBySpaceId(spaceId);

		if (avgRating == null) {
			avgRating = 0.0;
		}

//		// 四捨五入到小數點第一位
//		avgRating = BigDecimal.valueOf(avgRating)
//				.setScale(1, RoundingMode.HALF_UP)
//				.doubleValue();

		Space s = spaceRepository.findById(spaceId).orElse(null);

		if (s == null) {
			throw new ResourceNotFoundException("找不到ID 為「 " + spaceId + " 」的空間");
		}

		s.setSpaceRating(avgRating);
		spaceRepository.save(s);
	}

	// 取得所有空間時，同時計算空間滿意度
	public void recalculateAllSpaceRatings(List<Space> allSpaces) {
//		List<Space> allSpaces = spaceRepository.findAll();

		for (Space space : allSpaces) {
			String spaceId = space.getSpaceId();
			updateSpaceRating(spaceId);
		}
	}



















	//找特定空間id的評論
	public Page<SpaceCommentResponse> getSpaceCommentsById(String spaceId,Pageable pageable) {
			
			// 進行搜尋，不搜尋空間名稱以及分店
			String spaceName = null;
	        String branchId = null;
	        Page<SpaceCommentResponse> commentPage = spaceRepository.findSpaceCommentsByConditions(
	        		spaceId,
	                spaceName,
	                branchId,
	                pageable
	        );
	        
	        List<SpaceCommentResponse> responseList = commentPage.getContent();
	        
	        //回傳response
	        return new PageImpl<>(responseList, pageable, commentPage.getTotalElements());
			
		}

	//依照查詢條件查看空間評論
	public Page<SpaceCommentResponse> getSpaceCommentsByConditions( 
				String spaceId,
	            String spaceName,
	            String branchId,
	            Pageable pageable) {
			
			// 進行搜尋
	        Page<SpaceCommentResponse> commentPage = spaceRepository.findSpaceCommentsByConditions(
	        		spaceId,
	                spaceName,
	                branchId,
	                pageable
	        );
	        
	        List<SpaceCommentResponse> responseList = commentPage.getContent();
	        
	        //回傳response
	        return new PageImpl<>(responseList, pageable, commentPage.getTotalElements());
			
			}


	// 關鍵字、開始結束日間的複合查詢
	public List<Space> getAvailableSpaces(String keyword, String dateStr, String startTimeStr, String endTimeStr) {
		LocalTime startTime = LocalTime.parse(startTimeStr);
		LocalTime endTime = LocalTime.parse(endTimeStr);

		List<Space> targetSpaces;

		// 先對關鍵字模糊搜尋
		if (keyword != null && !keyword.isBlank()) {
			targetSpaces = spaceRepository.findBySpaceNameContainingIgnoreCase(keyword);
		} else {
			targetSpaces = spaceRepository.findAll(); // 若沒關鍵字則撈全部
		}

		if (targetSpaces == null || targetSpaces.isEmpty()) {
			throw new ResourceNotFoundException("查無空間");
		}

		if (dateStr == null || dateStr.isBlank()) {
			return targetSpaces; // 沒日期就不篩預約
		}

		LocalDate date = LocalDate.parse(dateStr);
		LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
		LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

		// 再過濾出沒被預約的空間
		return targetSpaces.stream()
				.filter(space -> !spaceRepository.existsBySpaceIdAndOrderOverlap(
						space.getSpaceId(), startDateTime, endDateTime))
				.collect(Collectors.toList());
	}

}
