package com.lifespace.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lifespace.entity.Member;

public interface MemberRepository extends JpaRepository<Member,String> {
	
	//-----------------------會員的登入功能----------------------------
	//原本的方法，直接做比對
	 Optional<Member> findByEmailAndPassword(String email, String password);
	 
	//------------------------會員的註冊功能------------------------
	// 包含會員用Email查詢的功能
	// 自定義方法:根據 email 查詢
    Optional<Member> findByEmail(String email);
    
    //自增主鍵
	@Query(value = "SELECT member_id FROM member ORDER BY member_id DESC LIMIT 1", nativeQuery = true)
	String findLatestMemberId();
	
	
	//------------------------會員的查詢功能--------------------------
    // 自定義方法:根據 name 查詢，回傳多筆或一筆都可以
	Optional<Member> findByMemberName(String memberName);

    // 自定義方法:根據 phone 查詢
	Optional<Member> findByPhone(String phone);
	

	
	//動態查詢-帳號狀態、註冊日期、生日
	@Query("SELECT m FROM Member m " +
		       "WHERE (:accountStatus IS NULL OR m.accountStatus = :accountStatus) " +
		       "AND (:birthday IS NULL OR m.birthday = :birthday) " +
		       "AND (:regTime IS NULL OR FUNCTION('DATE', m.registrationTime) = :regTime)")
		List<Member> searchMembers(
		    @Param("accountStatus") Integer accountStatus,
		    @Param("birthday") LocalDate birthday,
		    @Param("regTime") LocalDate regTime
		);


}
