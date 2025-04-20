package com.lifespace.controller;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lifespace.SessionUtils;
import com.lifespace.dto.MemberDTO;
import com.lifespace.dto.MemberRequestDTO;
import com.lifespace.entity.Member;
import com.lifespace.exception.MemberValidator;
import com.lifespace.repository.MemberRepository;
import com.lifespace.service.MailService;
import com.lifespace.service.MemberService;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "*")
public class MemberController {

	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MailService mailService;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	
	//-------------------------會員登入-----------------------------
	@PostMapping("/member/login")
	public ResponseEntity<?> login(@RequestBody Map<String,String> loginRequest, HttpSession session){
		String email = loginRequest.get("email");
		String password = loginRequest.get("password");
		
		Optional<Member> memberOpt = memberService.login(email, password);
		
		if(memberOpt.isPresent()) {
			//登入成功，根據需要回傳相關的會員資訊(絕對不能放密碼)
			Member member = memberOpt.get();
			
			//告訴spring security會員已登入
			Authentication auth = new UsernamePasswordAuthenticationToken(
			        member.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
			SecurityContextHolder.getContext().setAuthentication(auth);
			
			// Spring Security的session認證狀態
			session.setAttribute(
			    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
			    SecurityContextHolder.getContext()
			);
			
			//自己寫的session，儲存會員資料
			session.setAttribute("loginMember",member.getMemberId());

			//避免洩漏敏感資訊，這裡回傳部分資料
			Map<String, Object> response = new HashMap<>();
			response.put("memberId", member.getMemberId());
			response.put("memberName", member.getMemberName());
			response.put("email", member.getEmail());
			
			return ResponseEntity.ok(response);
		} else {
			//登入失敗
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("帳號或密碼錯誤");
		}
		
	}
	
	
	
	//-----------------------取得登入會員資訊（從 Session 抓）------------------------------
		@GetMapping("/member/account")
		public ResponseEntity<Map<String, Object>> getLoginMember(HttpSession session) {
		    String memberId = (String) session.getAttribute("loginMember");
		    
		    if (memberId != null) {
		        Optional<Member> memberOpt = memberService.findByIdMem(memberId);
		        if (memberOpt.isPresent()) {
		            Member member = memberOpt.get();

		            Map<String, Object> response = new HashMap<>();
		            response.put("memberId", member.getMemberId());
		            response.put("memberName", member.getMemberName());

		            return ResponseEntity.ok(response);
		        }
		    }

		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		

		//-----------------------取得會員帳號資料修改（從 Session 抓）------------------------------
		@GetMapping("/member/profile")
		public ResponseEntity<MemberDTO> getLoginMemberInfo(HttpSession session) {
		    String memberId = SessionUtils.getLoginMemberId(session);
		    if (memberId == null) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		    }
		    
		    Member member = memberRepository.findById(memberId).orElse(null);
		    MemberDTO dto = new MemberDTO();
		    dto.setMemberId(member.getMemberId());
		    dto.setMemberName(member.getMemberName());
		    dto.setPhone(member.getPhone());
		    dto.setEmail(member.getEmail());
		    dto.setBirthday(member.getBirthday());

		    return ResponseEntity.ok(dto);
		}
		
		//顯示會員的大頭貼
		@GetMapping(value = "/member/image", produces = MediaType.IMAGE_JPEG_VALUE)
		public ResponseEntity<byte[]> getMyImage(HttpSession session) {
		    String memberId = SessionUtils.getLoginMemberId(session);
		    if (memberId == null) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		    }

		    Optional<Member> memberOpt = memberRepository.findById(memberId);
		    
		    if (memberOpt.isPresent() && memberOpt.get().getMemberImage() != null) {
		        byte[] imageData = memberOpt.get().getMemberImage();
		        return ResponseEntity.ok().body(imageData);
		    }

		    // 回傳預設大頭貼
		    try (InputStream defaultStream = getClass().getResourceAsStream("/static/img/default.jpg")) {
		        if (defaultStream != null) {
		            byte[] defaultImage = defaultStream.readAllBytes();
		            return ResponseEntity.ok(defaultImage);
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		    }

		    return ResponseEntity.notFound().build();
		}
		
		
		//回傳會員修改後的資料
		@PostMapping(value = "/member/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		public ResponseEntity<String> updateMyProfile(
		        @RequestParam("memberName") String memberName,
		        @RequestParam("email") String email,
		        @RequestParam("phone") String phone,
		        @RequestParam("birthday") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthday,
		        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage,
		        HttpSession session) {

		    String memberId = SessionUtils.getLoginMemberId(session);
		    if (memberId == null) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
		    }
		    
		    //統一由service處理驗證跟邏輯
		    memberService.updateMem(memberId, memberName, email, phone, 1, birthday, memberImage);
		    return ResponseEntity.ok("更新成功");
		}

	
	
	//-------------------------------會員登出功能------------------------------------
	@PostMapping("/member/logout")
	public ResponseEntity<Void> logout(HttpSession session) {
		SessionUtils.removeMemberLogin(session); // 清掉 session 裡的loginMember
		return ResponseEntity.ok().build();  //讓前端決定要導去哪裡
	}
	
	
	//------------------------------會員忘記密碼-"發送"驗證連結到信箱---------------------------
    public MemberController(MailService mailService) {
        this.mailService = mailService;
    }
	
	@PostMapping("/member/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestParam String email) {
	    
		//先檢查郵件是否存在
		Optional<Member> member = memberRepository.findByEmail(email);
		if(member.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email:不存在");
		}

	    // 1. 產生一次性 token
	    String token = UUID.randomUUID().toString();
	    
	    // 2. 存到 Redis（10分鐘有效）
	    redisTemplate.opsForValue().set("RESET_TOKEN_" + token, email, Duration.ofMinutes(10)); //亂碼壽命十分鐘

	    // 3. 建立重設密碼連結（token 放在 URL）
	    String resetLink = "/lifespace/setPassword?token=" + token;

	    // 4. 寄信（用原本的 mailService）
	    mailService.sendResetLink(email, resetLink);

	    return ResponseEntity.ok("已發送重設密碼連結");
	}



	//------------------------------會員忘記密碼-"核對"token+"重設密碼"---------------------------
	@PostMapping("/member/reset-password")
	public ResponseEntity<String> resetPassword(
	        @RequestParam String token,
	        @RequestParam String newPassword) {

	    String email = redisTemplate.opsForValue().get("RESET_TOKEN_" + token);

	    if (email == null) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("連結無效或已過期");
	    }

	    try {
	        memberService.resetPassword(email, newPassword);
	        redisTemplate.delete("RESET_TOKEN_" + token); // 用完刪除
	        return ResponseEntity.ok("密碼更新成功");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("密碼更新失敗");
	    }
	}
	



	
	
	
	//--------------------------會員註冊(回傳DTO)---------------------------
	//DTO為一種資料格式，用來包裝想回傳給前端的欄位
	//回傳會員、產品、訂單等資料時
	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> registerMember(
	        @RequestParam("memberName") String memberName,
	        @RequestParam("email") String email,
	        @RequestParam("phone") String phone,
	        @RequestParam("password") String password,
	        @RequestParam("birthday") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthday,
	        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage,
	        HttpSession session) {
		
		//統一交給service層處理邏輯
		Member member = memberService.createMember(memberName, email, phone, 1, password, birthday, memberImage);
	    MemberDTO dto = memberService.toDTO(member);
	    session.setAttribute("loginUser", dto); //自動登入
	    return ResponseEntity.ok(dto);
	}


	// ------------------------後台會員新增(回傳文字)-----------------------------------------
	// 新增功能(我用一次打API的方式寫)
	//ResponseEntity為Spring 提供的「HTTP 回應物件」，可以控制狀態碼與回傳內容
	//單純文字訊息、狀態控制時（ex. 200 OK, 400 Bad Request）
	@PostMapping(value = "/member", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> addMember(
			@RequestParam("memberName") String memberName, 
			@RequestParam("email") String email,
			@RequestParam("phone") String phone,
			// 從表單拉下來的東西都是String，這邊請spring boot協助轉型Integer
			@RequestParam("accountStatus") Integer accountStatus, 
			@RequestParam("password") String password,
			@RequestParam("birthday") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthday,
			@RequestPart(value = "memberImage", required = false) MultipartFile memberImage
			){
		
		//統一交給service層處理邏輯
	    memberService.createMember(memberName, email, phone, accountStatus, password, birthday, memberImage);
	    return ResponseEntity.ok("會員新增成功!");
		
	}


	// -------------------------修改-------------------------------------------
		// 修改功能
		@PostMapping(value="/member/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		public String updateMember(
				@PathVariable String memberId,
				@RequestParam("memberName") String memberName, 
				@RequestParam("email") String email,
				@RequestParam("phone") String phone,
				// 從表單拉下來的東西都是String，這邊請spring boot協助轉型Integer
				@RequestParam("accountStatus") Integer accountStatus, 
				@RequestParam("birthday") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthday,
				@RequestPart(value = "memberImage", required = false) MultipartFile memberImage
		) {
			

			//統一交給service層處理邏輯
			boolean success = memberService.updateMem(memberId, memberName, email, phone, accountStatus, birthday, memberImage);
			return success ? "成功更新資料" : "update失敗，數據不存在";
		}
	
	
	

		// ------------------------查詢-------------------------------------------
		// 全部查詢功能(一進入頁面就可以看到所有人的資料)
		@GetMapping("/member")
		public List<Member> getAllMembers() {
			return memberService.findAllMem();
		}
		
		// 單一查詢ID功能
		@GetMapping("/member/id/{memberId}")
		public Member readId(@PathVariable String memberId) {
			Member member = memberService.findByIdMem(memberId).orElse(null);
			return member;
		}

		// 查詢並顯示照片功能
		@GetMapping(value = "/member/image/{memberId}", produces = MediaType.IMAGE_JPEG_VALUE)
		public ResponseEntity<byte[]> getMemberImage(@PathVariable String memberId) {
			Optional<Member> memberOpt = memberService.findByIdMem(memberId);
			System.out.println("有人請求圖片: " + memberId);
			
			// 會員有照片 → 回傳照片
		    if (memberOpt.isPresent() && memberOpt.get().getMemberImage() != null) {
		    	byte[] imageData = memberOpt.get().getMemberImage();
		    	return ResponseEntity.ok().body(imageData);
		    }
		    
		    // 會員有資料，但沒上傳照片 → 回傳預設照片
		    try (InputStream defaultStream = getClass().getResourceAsStream("/static/images/default.jpg")) {
		        if (defaultStream != null) {
		            byte[] defaultImage = defaultStream.readAllBytes();
		            return ResponseEntity.ok(defaultImage);
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		    }
		    
		    // 會員根本不存在、或預設圖檔也壞了
		    return ResponseEntity.notFound().build();
			
				 
		}

	// 為了讓每個路徑都更清楚的指定是什麼欄位的值，所以再路徑上加上分類
	@PostMapping("/member/search")
	public List<Member> search(@RequestBody MemberRequestDTO dto) {
	    return memberService.searchMembers(dto);
	}
	

	

//	//------------------------真的刪除(寫著以防萬一)--------------------------------------
//	//刪除功能
//	@DeleteMapping("/member/{memberId}")
//	public String deleteMember(@PathVariable String memberId) {
//		memberService.deleteByIdMem(memberId);
//		return "執行資料庫的delete操作";
//	} 

}
