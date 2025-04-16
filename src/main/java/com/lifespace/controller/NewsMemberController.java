package com.lifespace.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.dto.NewsDTO;
import com.lifespace.service.NewsService;
//會回傳 JSON
@RestController
@RequestMapping("/member/news") // 前台使用者讀取的消息
public class NewsMemberController {

	@Autowired
	private NewsService newsSvc;
	
	
	// 前台取得全部欄位(依上架狀態)
	@GetMapping("query")
	public List<NewsDTO> getAllNews() {
		return newsSvc.getAllNews(1);
	}
	
	//  首頁專用：取得上架中消息中最多 3 筆
	@GetMapping("/top3")
	public List<NewsDTO> getTop3News() {
	    List<NewsDTO> allNews = newsSvc.getAllNews(1); // 撈出所有上架中消息

	    List<NewsDTO> top3 = new ArrayList<>();

	    // 只取前3筆（不足3筆就全取）
	    for (int i = 0; i < allNews.size() && i < 3; i++) {
	        top3.add(allNews.get(i));
	    }
	    return top3;
	}
}


