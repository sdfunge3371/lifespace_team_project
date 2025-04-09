package com.lifespace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.lifespace.entity.Admin;



public interface AdminRepository extends CrudRepository<Admin,String> {
	
	@Query(value = "SELECT admin_id FROM admin ORDER BY admin_id DESC LIMIT 1", nativeQuery = true)
	String findLatestAdminId();
	
	//------------------------管理員的查詢功能--------------------------
    // 自定義方法:根據 name 查詢，回傳多筆或一筆都可以
	Optional<Admin> findByAdminName(String adminName);
	
	// 自定義方法:根據 email 查詢
    Optional<Admin> findByEmail(String email);

}
