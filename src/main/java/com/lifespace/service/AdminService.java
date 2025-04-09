package com.lifespace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lifespace.dto.AdminDTO;
import com.lifespace.entity.Admin;
import com.lifespace.repository.AdminRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;



@Service
public class AdminService {
	// 可加其他商業邏輯，例如會員驗證、格式檢查等
	
    @Autowired
    private AdminRepository adminRepository;
	@Autowired //密碼雜湊處理(我要先把註冊功能寫完，用雜湊生成密碼後，才能登入驗證)
	private PasswordEncoder passwordEncoder;
	@Autowired
    private EntityManager entityManager;
	
	

	
	
    
	//-------------------------------管理員新增功能------------------------------------------
	//共用的管理員新增功能
	public Admin createAdmin(String adminName, String email, Integer accountStatus, String password) {
		Admin admin = new Admin();
		String newId = generateNextAdminId();
		admin.setAdminId(newId);
		admin.setAdminName(adminName);
		admin.setEmail(email);
		admin.setAccountStatus(accountStatus);
		admin.setPassword(passwordEncoder.encode(password)); // 密碼加密
		return adminRepository.save(admin);
		
	}
    
	//新增A開頭的Id
	public String generateNextAdminId() {
		String lastId = adminRepository.findLatestAdminId(); 
		if (lastId == null) {
			return "A001"; // 資料庫沒有資料時
		} else {   
			              //轉成數字      //去掉首字英文
			int num = Integer.parseInt(lastId.substring(1)); 
			return String.format("A%03d", num + 1); 
		}
		
	}
	
	
	// ------------------修改------------------------------
		public boolean updateAdm(String adminId, String adminName, String email, Integer accountStatus) {
			
			//先抓出會員ID
			Optional<Admin> optional = adminRepository.findById(adminId);
			
			//欄位驗證(會員資訊除錯)，如果驗證失敗就會自動 throw，會被 Global Handler 接住
			//new MemberValidator(memberRepository, memberId, memberName, email, phone).validateAndThrow();
			
			//更新修正後的會員資料
			if (optional.isPresent()) {
				Admin admin = optional.get();
				// 更新欄位
				admin.setAdminName(adminName);
				admin.setEmail(email);
				admin.setAccountStatus(accountStatus);
				adminRepository.save(admin);
				return true;
			}
			return false;
		}
	
	
    
	//-------------------查詢---------------------------------
	//單筆查詢
    public Optional<Admin> findByIdAdm(String adminId) {
        return adminRepository.findById(adminId);
    }
    
	// 全部查詢
	public List<Admin> findAllAdm() {
		return (List<Admin>) adminRepository.findAll();
	}
	
	
	// 模糊查詢
		public List<Admin> searchAdmin(AdminDTO dto) {
	        StringBuilder sql = new StringBuilder("SELECT * FROM admin WHERE 1=1");

	        if (dto.getAdminId() != null && !dto.getAdminId().isEmpty()) {
	            sql.append(" AND admin_id = :adminId");
	        }
	        if (dto.getAdminName() != null && !dto.getAdminName().isEmpty()) {
	            sql.append(" AND admin_name LIKE :adminName");
	        }
	        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
	            sql.append(" AND email LIKE :email");
	        }
	        if (dto.getAccountStatus() != null) {
	            sql.append(" AND account_status = :accountStatus");
	        }
	        if (dto.getRegistrationTime() != null) {
	            sql.append(" AND DATE(registration_time) = :registrationTime");
	        }

	        Query query = entityManager.createNativeQuery(sql.toString(), Admin.class);

	        if (dto.getAdminId() != null && !dto.getAdminId().isEmpty()) {
	            query.setParameter("adminId", dto.getAdminId());
	        }
	        if (dto.getAdminName() != null && !dto.getAdminName().isEmpty()) {
	            query.setParameter("adminName", "%" + dto.getAdminName() + "%");
	        }
	        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
	            query.setParameter("email", "%" + dto.getEmail() + "%");
	        }
	        if (dto.getAccountStatus() != null) {
	            query.setParameter("accountStatus", dto.getAccountStatus());
	        }
	        if (dto.getRegistrationTime() != null) {
	            query.setParameter("registrationTime", dto.getRegistrationTime());
	        }

	        return query.getResultList();
	    }

	
	
	
	
	
	
	
  //------------------單筆刪除---------------------------------
    public void deleteByIdAdm(String adminId) {
    	adminRepository.deleteById(adminId);
    }
	

}
