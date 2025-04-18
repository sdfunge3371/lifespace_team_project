package com.lifespace.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.SessionUtils;
import com.lifespace.dto.AdminDTO;
import com.lifespace.entity.Admin;
import com.lifespace.entity.Member;
import com.lifespace.repository.AdminRepository;
import com.lifespace.service.AdminService;

import jakarta.servlet.http.HttpSession;


@RestController
public class AdminController {
	
    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminRepository adminRepository;
    
    
  //-------------------------管理員登入-----------------------------
    @PostMapping("/admin/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> loginRequest, HttpSession session){
    	String email = loginRequest.get("email");
    	String password = loginRequest.get("password");
    	
    	Optional<Admin> adminOpt = adminService.login(email, password);
    	
    	if(adminOpt.isPresent()) {
    		//登入成功
    		Admin admin = adminOpt.get();
    		//把資料放在session儲存
    		session.setAttribute("loginAdmin",admin.getAdminId());
    		//避免洩漏敏感資訊，這裡回傳部分資料
    		Map<String, Object> response = new HashMap<>();
    		response.put("adminId",admin.getAdminId());
    		response.put("adminName",admin.getAdminName());
    		response.put("email",admin.getEmail());
    		return ResponseEntity.ok(response);
    	} else {
    		//登入失敗
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("帳號或密碼錯誤");
    	}
    	
    }
    
  //-----------------------取得登入管理員資訊（從 Session 抓）------------------------------
  		@GetMapping("/admin/account")
  		public ResponseEntity<?> getProfile(HttpSession session){
  			String adminId = SessionUtils.getLoginAdminId(session); // 統一從工具類拿
  			
  			if(adminId == null) {
  				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
  			}
  			
  			Optional<Admin> adminOpt = adminService.findByIdAdm(adminId);
  			if(adminOpt.isPresent()) {
  				Admin admin = adminOpt.get();
  				Map<String, Object> response = new HashMap<>();
  				response.put("adminId", admin.getAdminId());
  				response.put("adminName", admin.getAdminName());
  				response.put("email", admin.getEmail());
  				return ResponseEntity.ok(response);
  			} else {
  				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("管理員不存在");
  			}
  			
  		}
    
    
    
    
	//新增功能
    @PostMapping(value = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> addAdmin(
			@RequestParam("adminName") String adminName, 
			@RequestParam("email") String email,
			// 從表單拉下來的東西都是String，這邊請spring boot協助轉型Integer
			@RequestParam("accountStatus") Integer accountStatus, 
			@RequestParam("password") String password
			){
		
    	// 這裡好像可以有更好的寫法!!!先緩緩再來修改，用可以共同除錯的方法來
		// 檢查信箱是否重複註冊
//	    if (adminRepository.findByEmail(email).isPresent()) {
//	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("此信箱已註冊！");
//	    }
	    
	    adminService.createAdmin(adminName, email, accountStatus, password);
	    return ResponseEntity.ok("會員新增成功!");
		
	}
    
    
  //-------------------------------管理員登出功能------------------------------------
  	@PostMapping("/admin/logout")
  	public ResponseEntity<Void> logout(HttpSession session) {
  		SessionUtils.removeAdminLogin(session); // 清掉 session 裡的loginMember
  		return ResponseEntity.ok().build();  //讓前端決定要導去哪裡
  	}
    

    
    
	// -------------------------修改-------------------------------------------
	//修改功能
	@PostMapping("/admin/{adminId}")
	public String updateAdmin(
			@PathVariable String adminId,
			@RequestParam("adminName") String adminName, 
			@RequestParam("email") String email,
			@RequestParam("accountStatus") Integer accountStatus

	) {
		boolean success = adminService.updateAdm(adminId, adminName, email, accountStatus);
		return success ? "成功更新資料" : "update失敗，數據不存在";
	} 
	
	//刪除功能
	@DeleteMapping("/admin/{adminId}")
	public String deleteAdmin(@PathVariable String adminId) {
		adminService.deleteByIdAdm(adminId);
		return "執行資料庫的delete操作";
	} 
	
	
	// ------------------------查詢-------------------------------------------
	// 全部查詢功能
	@GetMapping("/admin")
	public List<Admin> getAllAdmin() {
		return adminService.findAllAdm();
	}
	

	//查詢功能
	@GetMapping("/admin/id/{adminId}")
	public Admin read(@PathVariable String adminId) {
		Admin admin = adminService.findByIdAdm(adminId).orElse(null);
		return admin;
	} 
	
	// 管理員後台的搜尋
	@PostMapping("/admin/search")
	public List<Admin> search(@RequestBody AdminDTO dto) {
	    return adminService.searchAdmin(dto);
	}
	

}
