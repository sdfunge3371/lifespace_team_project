package com.lifespace.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.dto.NewsDTO;
import com.lifespace.service.NewsService;
//會回傳 JSON
@RestController
@RequestMapping("/member/news")
public class NewsMemberController {

	@Autowired
	NewsService newsSvc;
	
	
	// 前台取得全部欄位(依上架狀態)
	@GetMapping("query")
	public List<NewsDTO> getAllNews() {
		return newsSvc.getAllNews(1);
	}
		
}


