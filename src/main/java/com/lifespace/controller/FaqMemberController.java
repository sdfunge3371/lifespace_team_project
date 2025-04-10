package com.lifespace.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.dto.FaqDTO;
import com.lifespace.service.FaqRedisService;
import com.lifespace.service.FaqService;
//會回傳 JSON，不會找 View
@RestController
@RequestMapping("/member/faq")
public class FaqMemberController {

	@Autowired
	private FaqService faqSvc;
	
	@Autowired
	private FaqRedisService faqRedisService;

	// 前台取得FAQ(設定狀態1:已上架)
	@GetMapping("query")
	public List<FaqDTO> getAllFaqs() {
		return faqSvc.getAllFaqs(1);
	}
	
	// 測試Redis連線
	@GetMapping("/testRedis")
	public String test() {
		faqRedisService.testConnection();
		return "取得連線OK!";
	}
	
	// FAQ匯入Redis初始化
	@GetMapping("/testRedisFaq")
	public String testInit() {
	    faqRedisService.initFaqData();
	    return "FAQ Redis 初始化完成！";
	}
	
	// Hashtag分類資料
	@GetMapping("/hashtag")
	public Map<String, String> getByHashtag(@RequestParam String tag) {
	    return faqRedisService.getFaqByHashtag(tag);
	}


}