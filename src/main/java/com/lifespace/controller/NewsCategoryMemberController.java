package com.lifespace.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.dto.NewsCategoryDTO;
import com.lifespace.service.NewsCategoryService;

//會回傳 JSON
@RestController
@RequestMapping("/member/newsCategory")
public class NewsCategoryMemberController {

	@Autowired
	NewsCategoryService newsCategorySvc;

	@GetMapping("query")
	public List<NewsCategoryDTO> getAll() {
		// 呼叫 Service 從DB撈資料、轉DTO
		return newsCategorySvc.getAll();
	}

	@GetMapping("select")
	public NewsCategoryDTO findById(@RequestParam String newsCategoryId){
	return newsCategorySvc.findByCategoryId1(newsCategoryId);
	}
}
