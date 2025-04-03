package com.lifespace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifespace.entity.EventMember;

public interface EventMemberRepository extends JpaRepository<EventMember,String>{

	//根據eventId和memberId查詢欄位是否存在
	Optional<EventMember> findByEventEventIdAndMemberMemberId(String eventId, String memberId);
	
}
