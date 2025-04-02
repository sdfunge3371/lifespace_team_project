package com.lifespace.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lifespace.dto.MemberDTO;
import com.lifespace.entity.Member;
import com.lifespace.repository.MemberRepository;

@Service
public class MemberService {
	// 可加其他商業邏輯，例如會員驗證、格式檢查等

	@Autowired
	private MemberRepository memberRepository;
	@Autowired //密碼雜湊處理(我要先把註冊功能寫完，用雜湊生成密碼後，才能登入驗證)
	private PasswordEncoder passwordEncoder;
	
	//------------------會員登入-------------------------------
	//不適用加密
//	public Optional<Member> login(String email, String password) {
//	    return memberRepository.findByEmailAndPassword(email, password); 
//	}
	
	//雜湊
	public Optional<Member> login(String email, String rawPassword) {
	    Optional<Member> memberOpt = memberRepository.findByEmail(email);
	    if (memberOpt.isPresent()) {
	        Member member = memberOpt.get();
	        if (passwordEncoder.matches(rawPassword, member.getPassword())) {
	            return Optional.of(member); // 密碼驗證通過
	        }
	    }
	    return Optional.empty();
	}
	
	//-------------------------------會員新增功能------------------------------------------
	//共用的會員新增功能
	public Member createMember(String memberName, String email, String phone, Integer accountStatus, String password,
			LocalDate birthday, MultipartFile memberImage) {
		Member member = new Member();
		String newId = generateNextMemberId();
		member.setMemberId(newId);
		member.setMemberName(memberName);
		member.setEmail(email);
		member.setPhone(phone);
		member.setAccountStatus(accountStatus);
//		member.setPassword(password); // 密碼未加密
		member.setPassword(passwordEncoder.encode(password)); // 密碼加密
		member.setBirthday(birthday);
		
		//用 MultipartFile 並存入 byte[]
		if (memberImage != null && !memberImage.isEmpty()) {
			try {
				member.setMemberImage(memberImage.getBytes());
			} catch (IOException e) {
				e.printStackTrace(); // 可改成自定義例外處理
			}
		}

		return memberRepository.save(member);
		
	}

	// 乾淨的DTO(沒有照片)
	public MemberDTO toDTO(Member member) {
		MemberDTO dto = new MemberDTO();
		dto.setMemberId(member.getMemberId());
		dto.setMemberName(member.getMemberName());
		dto.setEmail(member.getEmail());
		dto.setPhone(member.getPhone());
		dto.setAccountStatus(member.getAccountStatus());
		dto.setBirthday(member.getBirthday());
		return dto;
	}

	// 新增M開頭的Id
	public String generateNextMemberId() {
		String lastId = memberRepository.findLatestMemberId(); // e.g. M015
		if (lastId == null) {
			return "M001"; // 資料庫沒有資料時
		} else {
			// 轉成數字 //去掉首字英文
			int num = Integer.parseInt(lastId.substring(1));
			return String.format("M%03d", num + 1); // M016
		}
	}

	// ------------------修改------------------------------
	public boolean updateMem(String memberId, String memberName, String email, String phone, Integer accountStatus,
			String password, LocalDate birthday, MultipartFile memberImage) {
		Optional<Member> optional = memberRepository.findById(memberId);
		if (optional.isPresent()) {
			Member member = optional.get();
			// 更新欄位
			member.setMemberName(memberName);
			member.setEmail(email);
			member.setPhone(phone);
			member.setAccountStatus(accountStatus);
			member.setPassword(password);
			member.setBirthday(birthday);
			// 更新圖片(有更動才更新)
			if (memberImage != null && !memberImage.isEmpty()) {
				try {
					member.setMemberImage(memberImage.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			memberRepository.save(member);
			return true;
		}
		return false;
	}


	// ----------------查詢---------------------------------
	// 全部查詢
	public List<Member> findAllMem() {
		return (List<Member>) memberRepository.findAll();
	}

	// 單筆查Id
	public Optional<Member> findByIdMem(String memberId) {
		return memberRepository.findById(memberId);
	}

	// 單筆查Name
	public Optional<Member> findByNameMem(String memberName) {
		return memberRepository.findByMemberName(memberName);
	}

	// 單筆查Phone
	public Optional<Member> findByPhoneMem(String memberPhone) {
		return memberRepository.findByPhone(memberPhone);
	}

	// 單筆查Email
	public Optional<Member> findByEmailMem(String memberEmail) {
		return memberRepository.findByEmail(memberEmail);
	}
	
	//動態查詢-多筆
	public List<Member> searchMembers(Integer status, LocalDate birthday, LocalDate regTime) {
	    return memberRepository.searchMembers(status, birthday, regTime);
	}

	
	

	// ------------------單筆刪除---------------------------------
	public void deleteByIdMem(String memberId) {
		memberRepository.deleteById(memberId);
	}

}
