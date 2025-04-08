package com.lifespace.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.lifespace.dto.NewsStatusDTO;
import com.lifespace.entity.NewsStatus;
import com.lifespace.repository.NewsStatusRepository;



@Service
public class NewsStatusService {

	@Autowired
	private NewsStatusRepository statusRepository;
	
	@Autowired
	@Lazy //延遲初始化，直到第一次使用才注入，避免依賴循環
	private NewsService newsService;

	// 從DB撈出所有資料轉成NewsStatusDTO傳給前端(取得所有欄位)
	public List<NewsStatusDTO> getAll() {
		// 建立要回傳給前端的DTO List
		List<NewsStatusDTO> listDto = new ArrayList<>();
		// 查詢資料庫中所有News資料（VO）
		List<NewsStatus> listVo = statusRepository.findAll();
		for (NewsStatus vo : listVo) {
			// 把每一筆VO轉換成DTO，加進list
			listDto.add(voToNewsStatusDTO(vo));
		}
		return listDto; // 回傳DTO的List給Controller（最後送給前端）
	}

	// 查找狀態
	public NewsStatus getNewsStatus(Integer NewsStatusId) {
		Optional<NewsStatus> voTemp = statusRepository.findById(NewsStatusId);
		if (voTemp.isPresent()) { // 如果voTemp 有值，代表找到了這筆NewsStatus
			return voTemp.get(); // 有這筆資料就回傳
		} else {
			return null;
		}
	}

	// 點選"狀態"下拉式清單做狀態消息篩選
	public NewsStatusDTO findByStatusId(Integer newsStatusId) {
		NewsStatusDTO dto = new NewsStatusDTO();
		Optional<NewsStatus> votmp = statusRepository.findById(newsStatusId);
		if (votmp.isPresent()) {
			// Optional物件轉成VO
			NewsStatus vo = votmp.get();
			dto = voToNewsStatusDTO(vo);
		}else {
			// 如果沒有接到該狀態代碼，拿全部欄位
//			List<NewsDTO> a = newsService.getAll();
			dto.setNewsList(newsService.getAll());
		}
		return dto;
	}



	// 將 DTO 轉換成 NewsStatusVO 實體，用於儲存
	private NewsStatus dtoToVo(NewsStatusDTO dto) {
		NewsStatus statusVo = new NewsStatus();
		statusVo.setNewsStatusId(dto.getNewsStatusId()); 
		statusVo.setStatusName(dto.getStatusName());
		statusVo.setCreatedTime(dto.getCreatedTime());
		return statusVo;
	}

// 將 NewsStatusVO 轉換成 DTO 回傳前端
	private NewsStatusDTO voToNewsStatusDTO(NewsStatus vo) {
		NewsStatusDTO statusDto = new NewsStatusDTO();
		statusDto.setNewsStatusId(vo.getNewsStatusId());
		statusDto.setStatusName(vo.getStatusName());
		statusDto.setCreatedTime(vo.getCreatedTime());
		statusDto.setNewsList(newsService.voToDto(vo.getNewsSet()));
		return statusDto;
	}

}
