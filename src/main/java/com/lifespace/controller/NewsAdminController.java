package com.lifespace.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lifespace.SessionUtils;
import com.lifespace.dto.AddNewsDTO;
import com.lifespace.dto.NewsDTO;
import com.lifespace.dto.NewsUpdateDTO;
import com.lifespace.service.NewsService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
//會回傳 JSON
@RestController
@RequestMapping("/admin/news")
public class NewsAdminController {

	@Autowired
	private NewsService newsSvc;
	
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
	
	// 後台取得全部欄位
	@GetMapping("query")
	public List<NewsDTO> getAll() {
		//呼叫 Service 從DB撈資料、轉DTO
		return newsSvc.getAll();
	}
	
	// 前台取得全部欄位(依上架狀態)
	@GetMapping("frontQuery")
	public List<NewsDTO> getAllNews() {
		return newsSvc.getAllNews(1);
	}
	
	//------篩選分類、狀態欄位--------
	@GetMapping("select")
	public List<NewsDTO> findById(@RequestParam String newsCategoryId, 
									@RequestParam Integer newsStatusId){
	return	newsSvc.getReqColumn(newsCategoryId, newsStatusId);
	}
	
	//--------對應前端表單或 AJAX 發送的新增請求--------
	@PostMapping("insertvalid")
	// @Valid 根據 DTO 裡的驗證註解（像@NotBlank）做自動欄位檢查
	// @ModelAttribute 處理表單 + multipart/form-data
	// @ModelAttribute + @RequestParam:表單 / 檔案上傳
	// BindingResult bindingResult: 存放上面驗證的結果（錯誤與否），要寫在 @Valid 後面才會生效
	// @RequestParam("newsImg") 對應前端 <input type="file" name="newsImg">
	public ResponseEntity<?> addNewsError(@Valid @ModelAttribute AddNewsDTO dto,
	                                      BindingResult bindingResult,
	                                      @RequestParam("newsImg") MultipartFile newsImg,
	                                      HttpSession session) { // 取得 Session 中的 admin 資訊

	    // 從 session 中取得登入中的管理員 ID（透過工具類）
	    String adminId = SessionUtils.getLoginAdminId(session);
	    if (adminId == null) {
	        // 若沒有登入，回傳 401 Unauthorized 錯誤
	        return ResponseEntity.status(401).body("尚未登入管理員");
	    }

	    byte[] imageBytes = null;
	    try {
	        if (!newsImg.isEmpty()) { // 檢查是否有選擇圖片
	            imageBytes = newsImg.getBytes(); // 將 MultipartFile 轉成 byte[] 格式
	        }
	    } catch (IOException e) {
	        // 若轉換圖片過程發生錯誤，回傳 400 Bad Request，並帶上錯誤訊息
	        e.printStackTrace();
	        return ResponseEntity.badRequest().body(Map.of("newsImg", "圖片上傳失敗"));
	    }

	    // 將 adminId 一併傳給 Service，Service 會負責設入到 Entity 中
	    return newsSvc.addNewsError(dto, bindingResult, imageBytes, adminId);
	}

	
	//------對應前端表單或 AJAX 發送的修改請求------
	@PostMapping("updatevalid") 
	public ResponseEntity<?> updateNewsError(@Valid @ModelAttribute NewsUpdateDTO dto, BindingResult bindingResult,
			@RequestParam("newsImg") MultipartFile newsImg) {
		System.out.println("有進來 Controller updatevalid(前端成功發送請求)");
		byte[] imageBytes = null;
		try {
		    if (!newsImg.isEmpty()) { // 檢查是否有選擇圖片
		        imageBytes = newsImg.getBytes(); // 將 MultipartFile 轉成 byte[] 格式
		    }
		} catch (IOException e) {
		    // 若轉換圖片過程發生錯誤，回傳 400 Bad Request，並帶上錯誤訊息
		    e.printStackTrace();
		    return ResponseEntity.badRequest().body(Map.of("newsImg", "圖片上傳失敗"));
		}

		// 將 DTO 和圖片位元組一併傳給 Service，由 Service 進行資料驗證與儲存
		return newsSvc.updateNewsError(dto, bindingResult, imageBytes);
	}
	
	//------修改時依ID抓整個欄位--------
		@GetMapping("queryById")
		public NewsDTO findById(@RequestParam String newsId) {
			return newsSvc.findById(newsId);
		}
	
	//-------下架----------
	@PostMapping("deprecated")
	public void deprecatedNews(@RequestParam String newsId) {
		newsSvc.deprecatedNews(newsId);
	}
		
}


