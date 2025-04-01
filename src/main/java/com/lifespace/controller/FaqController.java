package com.lifespace.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.dto.FaqAddDTO;
import com.lifespace.dto.FaqDTO;
import com.lifespace.dto.FaqUpdateDTO;
import com.lifespace.service.FaqService;

import jakarta.validation.Valid;
//會回傳 JSON，不會找 View
@RestController
@RequestMapping("/faq")
public class FaqController {

	@Autowired
	FaqService faqSvc;
	
	// 後台取得FAQ
	@GetMapping("query")
	public List<FaqDTO> getAll() {
		//呼叫 Service 從DB撈資料、轉DTO
		return faqSvc.getAll();
	}
	
	// 前台取得FAQ(設定狀態1:已上架)
	@GetMapping("frontQuery")
	public List<FaqDTO> getAllFaqs() {
		return faqSvc.getAllFaqs(1);
	}
	
	// 下架
	@PostMapping("deprecated")
	public void deprecatedFaq(@RequestParam String faqId) {
		faqSvc.deprecatedFaq(faqId);
	}

	// 新增驗證FAQ
	@PostMapping("insertvalid") //對應前端表單或 AJAX 發送的新增請求
	// 用AddFaqDTO 接住前端送來的 JSON 資料
	// @Valid根據 DTO 裡的驗證註解（像@NotBlank）做自動欄位檢查
	//@RequestBody 告訴Spring(用Jackson)前端送來的是JSON格式 → 幫我轉換成Java物件（DTO）
	//BindingResult bindingResult:存放上面驗證的結果（錯誤與否），要寫在 @Valid 後面才會生效
	public ResponseEntity<?> addFaqError(@Valid @RequestBody FaqAddDTO dto, BindingResult bindingResult) {

		return faqSvc.addFaqError(dto, bindingResult);
	}

	// 修改驗證FAQ
	@PostMapping("updatevalid")
	public ResponseEntity<?> updateFaqError(@Valid @RequestBody FaqUpdateDTO dto, BindingResult bindingResult) {

		return faqSvc.updateFaqError(dto, bindingResult);
	}
}