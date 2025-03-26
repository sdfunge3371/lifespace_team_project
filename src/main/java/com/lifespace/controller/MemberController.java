package com.lifespace.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lifespace.model.Member;
import com.lifespace.service.MemberService;

@RestController
@CrossOrigin(origins = "*")
public class MemberController {
	
    @Autowired
    private MemberService memberService;
    
    //------------------------新增-----------------------------------------
	//新增功能
    @PostMapping(value = "/member", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Member addMember(
			 @RequestParam("memberName") String memberName,
		     @RequestParam("email") String email,
		     @RequestParam("phone") String phone,
		     //從表單拉下來的東西都是String，這邊請spring boot協助轉型Integer
		     @RequestParam("accountStatus") Integer accountStatus,
		     @RequestParam("password") String password,
		     @RequestParam("birthday") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthday,
		     @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
			
			) {
    	//建立新會員物件
    	Member member = new Member();
    	String newId = memberService.generateNextMemberId();
    	member.setMemberId(newId);
    	member.setMemberName(memberName);
    	member.setEmail(email);
    	member.setPhone(phone);
    	member.setPassword(password);
    	member.setAccountStatus(accountStatus);
    	member.setBirthday(birthday);
    	
    	if(memberImage != null && !memberImage.isEmpty()) {
    		try {
    			 member.setMemberImage(memberImage.getBytes()); // 存進 DB (byte[] 型別)
    		}  catch (IOException e) {
                e.printStackTrace();
            }
    	}
		memberService.saveMem(member);
		return member;
	} 
	
	
	//-------------------------修改-------------------------------------------
	//修改功能
	@PutMapping("/member/{memberId}")
	public String updateMember(@PathVariable String memberId,@RequestBody Member member) {
		Member m = memberService.findByIdMem(memberId).orElse(null);
		if(m!=null) {
			m.setMemberName(member.getMemberName());
			m.setMemberImage(member.getMemberImage());
			m.setEmail(member.getEmail());
			m.setPhone(member.getPhone());
			m.setAccountStatus(member.getAccountStatus());
			m.setPassword(member.getPassword());
			m.setBirthday(member.getBirthday());
			memberService.saveMem(m);
			return "執行資料庫的update操作";
		} else {
			return "update失敗，數據不存在";
		}
	} 
	

	
	//------------------------查詢-------------------------------------------
	//全部查詢功能
	@GetMapping("/member")
	public List<Member> getAllMembers() {
	    return memberService.findAllMem();
	}
	
	//為了讓每個路徑都更清楚的指定是什麼欄位的值，所以再路徑上加上分類
	//單一查詢ID功能
	@GetMapping("/member/id/{memberId}")
	public Member readId(@PathVariable String memberId) {
		Member member = memberService.findByIdMem(memberId).orElse(null);
		return member;
	} 
	
	//單一查詢Name功能
	@GetMapping("/member/name/{memberName}")
	public Member readName(@PathVariable String memberName) {
		Member member = memberService.findByNameMem(memberName).orElse(null);
		return member;
	} 
	
	//單一查詢Phone功能
	@GetMapping("/member/phone/{memberPhone}")
	public Member readPhone(@PathVariable String memberPhone) {
		Member member = memberService.findByPhoneMem(memberPhone).orElse(null);
		return member;
	} 
	
	//單一查詢Email功能
	@GetMapping("/member/email/{memberEmail}")
	public Member readEmail(@PathVariable String memberEmail) {
		Member member = memberService.findByEmailMem(memberEmail).orElse(null);
		return member;
	}
	
	
	
//	//------------------------真的刪除(寫著以防萬一)--------------------------------------
//	//刪除功能
//	@DeleteMapping("/member/{memberId}")
//	public String deleteMember(@PathVariable String memberId) {
//		memberService.deleteByIdMem(memberId);
//		return "執行資料庫的delete操作";
//	} 
	


	
	

}
