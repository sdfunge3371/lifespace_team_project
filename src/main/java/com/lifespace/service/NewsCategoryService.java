package com.lifespace.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.dto.NewsCategoryDTO;
import com.lifespace.dto.NewsDTO;
import com.lifespace.entity.NewsCategoryVO;
import com.lifespace.repository.NewsCategoryRepository;

@Service
public class NewsCategoryService {

	@Autowired
	private NewsCategoryRepository categoryRepository;
	
	@Autowired
	private NewsService newsService;

	// 從DB撈出所有資料轉成NewsCategoryDTO傳給前端(取得所有欄位)
	public List<NewsCategoryDTO> getAll() {
		// 建立要回傳給前端的DTO List
		List<NewsCategoryDTO> listDto = new ArrayList<>();
		// 查詢資料庫中所有News資料（VO）
		List<NewsCategoryVO> listVo = categoryRepository.findAll();
		for (NewsCategoryVO catvo : listVo) {
			// 把每一筆VO轉換成DTO，加進list
			listDto.add(voToNewsCategoryDTO(catvo));
		}
		return listDto;	// 回傳DTO的List給Controller（最後送給前端）
	}
	
	// 點選"分類"下拉式清單做分類消息篩選
	public NewsCategoryDTO findByCategoryId(String newsCategoryId) {
		NewsCategoryDTO dto = new NewsCategoryDTO();
		Optional<NewsCategoryVO> votmp = categoryRepository.findById(newsCategoryId);
		if(votmp.isPresent()) {
			// Optional物件轉成VO
			NewsCategoryVO vo = votmp.get();
			dto = voToNewsCategoryDTO(vo);
		}else {
			// 如果沒有接到值，拿全部欄位
//			List<NewsDTO> a = newsService.getAll();
			dto.setNewsList(newsService.getAll());
		}
		return dto;
		
}
	// 點選"分類"下拉式清單做分類消息篩選
	public NewsCategoryDTO findByCategoryId1(String newsCategoryId) {

		NewsCategoryDTO dto = new NewsCategoryDTO();
		List<NewsDTO> newList = newsService.getAll()
				// 只保留狀態為上架中的消息，並把過濾後的結果重新收集成 List
				.stream().filter(vo -> vo.getNewsStatusId() == 1).toList();
		if (!newsCategoryId.isBlank()) {
			newList = newList.stream()
					.filter(vo -> vo.getNewsCategoryId().equals(newsCategoryId)).toList();
		}
			dto.setNewsList(newList);
		return dto;
	}
	
	// 將DTO 轉換成 VO 存入資料庫
	private NewsCategoryVO dtoToNewsCategoryVO(NewsCategoryDTO dto) {
    		NewsCategoryVO categoryVO = new NewsCategoryVO();
    		categoryVO.setNewsCategoryId(dto.getNewsCategoryId());
    		categoryVO.setCategoryName(dto.getCategoryName());
    		categoryVO.setCreatedTime(dto.getCreatedTime());
    		return categoryVO;
	}
    		// 資料回傳前端VO轉換成DTO
	private NewsCategoryDTO voToNewsCategoryDTO(NewsCategoryVO vo) {
		NewsCategoryDTO categoryDto = new NewsCategoryDTO();
		categoryDto.setNewsCategoryId(vo.getNewsCategoryId());
		categoryDto.setCategoryName(vo.getCategoryName());
		categoryDto.setCreatedTime(vo.getCreatedTime());
		categoryDto.setNewsList(newsService.voToDto(vo.getNewsSet()));
		return categoryDto;
	}
}