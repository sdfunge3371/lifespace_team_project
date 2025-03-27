package com.lifespace.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.entity.Member;
import com.lifespace.repository.MemberRepository;

@Service
public class MemberService {
	// 可加其他商業邏輯，例如會員驗證、格式檢查等

	@Autowired
	private MemberRepository memberRepository;

	
    //----------------單筆新增---------------------------------
	public Member saveMem(Member member) {
		return memberRepository.save(member);
	}
	
	//新增M開頭的Id
	public String generateNextMemberId() {
		String lastId = memberRepository.findLatestMemberId(); // e.g. M015
		if (lastId == null) {
			return "M001"; // 資料庫沒有資料時
		} else {   
			              //轉成數字      //去掉首字英文
			int num = Integer.parseInt(lastId.substring(1)); 
			return String.format("M%03d", num + 1); // M016
		}
	}

	
	//----------------查詢---------------------------------
	//全部查詢
	public List<Member> findAllMem() {
	    return (List<Member>) memberRepository.findAll();
	}
	//單筆查Id
	public Optional<Member> findByIdMem(String memberId) {
		return memberRepository.findById(memberId);
	}
	//單筆查Name
	public Optional<Member> findByNameMem(String memberName) {
		return memberRepository.findByMemberName(memberName);
	}
	//單筆查Phone
	public Optional<Member> findByPhoneMem(String memberPhone) {
		return memberRepository.findByPhone(memberPhone);
	}
	//單筆查Email
	public Optional<Member> findByEmailMem(String memberEmail) {
		return memberRepository.findByEmail(memberEmail);
	}
	



	//------------------單筆刪除---------------------------------
	public void deleteByIdMem(String memberId) {
		memberRepository.deleteById(memberId);
	}

}
