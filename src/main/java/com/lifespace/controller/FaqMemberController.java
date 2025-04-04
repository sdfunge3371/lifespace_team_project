package com.lifespace.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.dto.FaqDTO;
import com.lifespace.service.FaqService;
//會回傳 JSON
@RestController
@RequestMapping("/member/faq")
public class FaqMemberController {

	@Autowired
	FaqService faqSvc;

	// 前台取得FAQ(設定狀態1:已上架)
	@GetMapping("query")
	public List<FaqDTO> getAllFaqs() {
		return faqSvc.getAllFaqs(1);
	}
}