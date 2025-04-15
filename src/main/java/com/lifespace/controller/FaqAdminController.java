package com.lifespace.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.SessionUtils;
import com.lifespace.dto.FaqAddDTO;
import com.lifespace.dto.FaqDTO;
import com.lifespace.dto.FaqUpdateDTO;
import com.lifespace.dto.MemberDTO;
import com.lifespace.entity.Member;
import com.lifespace.service.FaqService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
//會回傳 JSON
@RestController
@RequestMapping("/admin/faq")
public class FaqAdminController {

	@Autowired
	private FaqService faqSvc;
	
	//---檢查是否登入管理員---
	@GetMapping("profile")
	public ResponseEntity<?> getLoginAdminInfo(HttpSession session) {
	    String adminId = SessionUtils.getLoginAdminId(session);// 從工具類(SessionUtil)拿
	    if (adminId == null) {
	        return ResponseEntity.status(401).body("尚未登入管理員");
	    }
		Map<String, String> res = new HashMap<>();
		res.put("adminId", adminId);  
		return ResponseEntity.ok(res);
	}
	
	
	// 後台取得FAQ
	@GetMapping("query")
	public List<FaqDTO> getAll() {
		//呼叫 Service 從DB撈資料、轉DTO
//		System.out.println("後端收到 FAQ 查詢請求");
		return faqSvc.getAll();
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