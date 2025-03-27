package com.lifespace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.lifespace.entity.Member;

public interface MemberRepository extends CrudRepository<Member,String> {
	
    // 自定義方法:根據 name 查詢，回傳多筆或一筆都可以
	Optional<Member> findByMemberName(String memberName);

    // 自定義方法:根據 phone 查詢
	Optional<Member> findByPhone(String phone);

    // 自定義方法:根據 email 查詢
	Optional<Member> findByEmail(String email);
	
    //自增主鍵
	@Query(value = "SELECT member_id FROM member ORDER BY member_id DESC LIMIT 1", nativeQuery = true)
	String findLatestMemberId();

}
