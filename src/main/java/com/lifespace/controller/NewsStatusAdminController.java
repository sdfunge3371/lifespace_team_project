package com.lifespace.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.dto.NewsStatusDTO;
import com.lifespace.service.NewsStatusService;

//會回傳 JSON
@RestController
@RequestMapping("/admin/newsStatus")
public class NewsStatusAdminController {

	@Autowired
	private NewsStatusService newsStatusSvc;

	@GetMapping("query")
	public List<NewsStatusDTO> getAll() {
		// 呼叫 Service 從DB撈資料、轉DTO
		return newsStatusSvc.getAll();
	}

	// 找狀態ID做篩選(新增消息頁面)
	@GetMapping("select")
	public NewsStatusDTO findByStatusId(Integer newsStatusId) {
		return newsStatusSvc.findByStatusId(newsStatusId);
	}

}
