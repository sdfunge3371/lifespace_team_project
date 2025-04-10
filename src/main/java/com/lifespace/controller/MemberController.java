package com.lifespace.controller;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
//	@Autowired
//	private StringRedisTemplate redisTemplate;
    // 建立一個簡單的記憶體 Map 來暫存驗證碼（key: email, value: code）
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
	
	//-------------------------會員登入-----------------------------
	@PostMapping("/member/login")
	public ResponseEntity<?> login(@RequestBody Map<String,String> loginRequest, HttpSession session){
		String email = loginRequest.get("email");
		String password = loginRequest.get("password");
		
		Optional<Member> memberOpt = memberService.login(email, password);
		
		if(memberOpt.isPresent()) {
			//登入成功，根據需要回傳相關的會員資訊(絕對不能放密碼)
			Member member = memberOpt.get();
			//把資料放到session儲存
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
	@GetMapping("/member/profile")
	public ResponseEntity<?> getProfile(HttpSession session){
		String memberId = SessionUtils.getLoginMemberId(session); // 統一從工具類拿
		
		if(memberId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
		}
		
		Optional<Member> memberOpt = memberService.findByIdMem(memberId);
		if(memberOpt.isPresent()) {
			Member member = memberOpt.get();
			Map<String, Object> response = new HashMap<>();
			response.put("memberId", member.getMemberId());
			response.put("memberName", member.getMemberName());
			response.put("email", member.getEmail());
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("會員不存在");
		}
		
	}
	
	
	//-------------------------------會員登出功能------------------------------------
	@PostMapping("/member/logout")
	public ResponseEntity<?> logout(HttpSession session) {
	    session.invalidate(); // 清除所有 session 屬性
	    return ResponseEntity.ok("已成功登出");
	}
	
	
	//------------------------------會員忘記密碼-先"檢查"郵件是否存在---------------------------
	@GetMapping("/member/check-email")
	public ResponseEntity<String> checkEmail(@RequestParam String email) {
	    Optional<Member> member = memberRepository.findByEmail(email);
	    if (member.isPresent()) {
	        return ResponseEntity.ok("帳號存在");
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("該 Email 尚未註冊");
	    }
	}

	
	
	
	//------------------------------會員忘記密碼-"發送"驗證亂碼---------------------------
    public MemberController(MailService mailService) {
        this.mailService = mailService;
    }
	
	@PostMapping("/member/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestParam String email) {
	    String code = generateRandomCode();
	    
	   // 用redis的方法 
	   // redisTemplate.opsForValue().set("RESET_CODE_" + email, code, Duration.ofMinutes(10)); //亂碼壽命十分鐘

	    // 將驗證碼存在後端 Map（不使用 Redis）
        verificationCodes.put(email, code);
	    
	    mailService.sendVerificationCode(email, code);
	    //System.out.println("寄送驗證碼到：" + toEmail + "，驗證碼為：" + code);

	    return ResponseEntity.ok("驗證碼已發送");
	}

	//亂碼產生器
	private String generateRandomCode() {
		String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 避免0O1l
	    SecureRandom random = new SecureRandom();
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < 6; i++) {
	        sb.append(chars.charAt(random.nextInt(chars.length())));
	    }
	    return sb.toString();
	}



	//------------------------------會員忘記密碼-"核對"驗證亂碼---------------------------
	@PostMapping("/member/verify-code")
	public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String inputCode) {
		// 用map
		String storedCode = verificationCodes.get(email);
		
		// 用redis
		//String storedCode = redisTemplate.opsForValue().get("RESET_CODE_" + email);

	    if (storedCode != null && storedCode.equals(inputCode)) {
	        return ResponseEntity.ok("驗證成功，可進入重設密碼頁面");
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("驗證失敗或驗證碼過期");
	    }
	}
	
	
	
	//-----------------------------會員忘記密碼-"重設密碼"-------------------------------------
	@PostMapping("/member/setPassword")
	public ResponseEntity<String> resetPassword(
	        @RequestParam String email,
	        @RequestParam String newPassword) {

	    try {
	        memberService.resetPassword(email, newPassword);
	        return ResponseEntity.ok("密碼更新成功");
	    } catch (NoSuchElementException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到該使用者");
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

	    // 檢查信箱是否重複註冊
	    if (memberRepository.findByEmail(email).isPresent()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("此信箱已註冊！");
	    }
	    
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
		
		 // 檢查信箱是否重複註冊
	    if (memberRepository.findByEmail(email).isPresent()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("此信箱已註冊！");
	    }
	    
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
		    try (InputStream defaultStream = getClass().getResourceAsStream("/static/img/default.jpg")) {
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
	
	
	
	

//	// 單一查詢Name功能
//	@GetMapping("/member/name/{memberName}")
//	public Member readName(@PathVariable String memberName) {
//		Member member = memberService.findByNameMem(memberName).orElse(null);
//		return member;
//	}
//
//	// 單一查詢Phone功能
//	@GetMapping("/member/phone/{memberPhone}")
//	public Member readPhone(@PathVariable String memberPhone) {
//		Member member = memberService.findByPhoneMem(memberPhone).orElse(null);
//		return member;
//	}
//
//	// 單一查詢Email功能
//	@GetMapping("/member/email/{memberEmail}")
//	public Member readEmail(@PathVariable String memberEmail) {
//		Member member = memberService.findByEmailMem(memberEmail).orElse(null);
//		return member;
//	}
//	
//	//多樣查詢
//	@GetMapping("/member/search")
//	public List<Member> searchMembers(
//	    @RequestParam(required = false) Integer accountStatus,
//	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthday,
//	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registrationTime
//	) {
//	    return memberService.searchMembers(accountStatus, birthday, registrationTime);
//	}

	
	

//	//------------------------真的刪除(寫著以防萬一)--------------------------------------
//	//刪除功能
//	@DeleteMapping("/member/{memberId}")
//	public String deleteMember(@PathVariable String memberId) {
//		memberService.deleteByIdMem(memberId);
//		return "執行資料庫的delete操作";
//	} 

}
