package com.lifespace.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.dto.NewsCategoryDTO;
import com.lifespace.service.NewsCategoryService;

//會回傳 JSON
@RestController
@RequestMapping("/admin/newsCategory")
public class NewsCategoryAdminController {

	@Autowired
	private NewsCategoryService newsCategorySvc;

	@GetMapping("query")
	public List<NewsCategoryDTO> getAll() {
		// 呼叫 Service 從DB撈資料、轉DTO
		return newsCategorySvc.getAll();
	}

	// 找分類ID做篩選(新增消息頁面)
	@GetMapping("select")
	public NewsCategoryDTO findByCategoryId(String newsCategoryId) {
		return newsCategorySvc.findByCategoryId(newsCategoryId);
	}

}
