package com.lifespace.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lifespace.SessionUtils;
import com.lifespace.dto.CommentsDTO;
import com.lifespace.dto.OrdersDTO;
import com.lifespace.entity.Comments;
import com.lifespace.entity.EventMember;
import com.lifespace.entity.Member;
import com.lifespace.repository.EventMemberRepository;
import com.lifespace.service.CommentsService;
import com.lifespace.service.OrdersService;
//import com.lifespace.util.SessionUtils;

import jakarta.servlet.http.HttpSession;


@RestController
public class CommentsController {

	@Autowired
	CommentsService commentsService;
	
	@Autowired
    private OrdersService ordersSvc;
	
	@Autowired
	private EventMemberRepository eventMemberRepository;
	
//	// 沒驗證登入者是本人
//	@PostMapping("/comments")
//	public String insert(@RequestBody Comments comments) {
//		commentsService.addComments(comments);
//		return "執行資料庫的 Create 操作";
//	}
	
//	// 沒驗證登入者是本人
//	@PutMapping("/comments/{commentId}")
//	public String update(@PathVariable String commentId,
//						 @RequestBody Comments comments) {
//		comments.setCommentId(commentId); //這樣就可以設定commentsVO裡面的id的值
//		commentsService.updateComments(comments);
//		return "執行資料庫的 Update 操作";
//	}
	
//	// 沒驗證登入者是本人
//	@DeleteMapping("/comments/{commentId}")
//	public String delete(@PathVariable String commentId) {
//		commentsService.deleteComments(commentId);
//		return "執行資料庫的 Delete 操作";
//	}
	
	@GetMapping("/comments/{commentId}")
	public Comments read(@PathVariable String commentId) {
		Comments comments = commentsService.getOneComments(commentId);
		return comments;
	}
	
	@GetMapping("/comments")
	public List<Comments> read() {
		List<Comments> comments = commentsService.getAll();
		return comments;
	}
	
	// 後台中心活動留言板管理查詢功能
	@GetMapping("/comments/getAll")
    public List<OrdersDTO> getAllOrders() {
        return ordersSvc.getAllOrdersDTOs();
    }

	
	
	/**
	 * 前台取得留言分頁資料（RESTful 路由）
	 *
	 * 使用 infinite scroll 時呼叫此方法，每次回傳指定頁數的留言清單。
	 *
	 * @param page 當前頁數（從 0 開始）
	 * @param size 每頁筆數
	 * @return 留言資料的 List<CommentsDTO>
	 */	
//	@GetMapping("/comments/page/{page}/{size}")
//	public List<CommentsDTO> getCommentsPage(@PathVariable int page, @PathVariable int size) {
//	    
////		dto.setImageUrl(comments.getEventMember().getMember().getMemberImage());
//
//		
//		return commentsService.getCommentsDTOPage(page, size);
//	}
	
	
	// 【留言分頁查詢 API】
	// ✅ 提供前端 AJAX 呼叫，取得第幾頁（page）幾筆資料（size）的留言清單
	// ✅ 會回傳 List<CommentsDTO>，前端會用這些資料顯示留言區內容
	@GetMapping("/comments/page/{page}/{size}")
	public List<CommentsDTO> getCommentsPage(@PathVariable int page, @PathVariable int size) {
	    return commentsService.getCommentsDTOPage(page, size);
	}
	
	
	// ✅ 提供根據活動 ID 查詢留言（分頁）
	@GetMapping("/comments/event/{eventId}/page/{page}/{size}")
	public List<CommentsDTO> getCommentsForEventPage(
	        @PathVariable String eventId,
	        @PathVariable int page,
	        @PathVariable int size) {
	    return commentsService.getCommentsDTOByEventId(eventId, page, size);
	}

	
	
//	@PostMapping("/comments")
//	public CommentsDTO insertComments(@RequestBody Comments comments, HttpSession session) {
//	    Object obj = session.getAttribute("eventMember");
//	    EventMember eventMember = (obj instanceof EventMember) ? (EventMember) obj : null;
//
//	    if (eventMember == null) {
//	        throw new RuntimeException("尚未登入或未參加活動");
//	    }
//
//	    comments.setEventMember(eventMember);
//	    Comments saved = commentsService.addCommentsReturnSaved(comments);
//
//	    return commentsService.convertToDTO(saved); // 只呼叫一次轉換
//	}
	
	
//	@PostMapping("/comments")
//	public ResponseEntity<?> insertComments(@RequestBody CommentsDTO dto, HttpSession session) {
////	    String memberId = SessionUtils.getLoginMemberId(session); // 從 session 拿登入會員 ID
//	    String eventId = dto.getEventId(); // 前端也要傳活動 ID
//
//	    if (memberId == null || eventId == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入並選擇活動");
//	    }
//
////	    String eventMemberId = commentsService.findEventMemberId(memberId, eventId);
//	    String eventMemberId = commentsService.findEventMemberIdBySessionMemberAndEvent(eventId, session);
//
//	    if (eventMemberId == null) {
//	        return ResponseEntity.badRequest().body("尚未參加此活動，無法留言");
//	    }
//
//	    dto.setEventMember(new EventMember(eventMemberId)); // 指定留言對應的活動會員
//	    CommentsDTO saved = commentsService.insert(dto);    // 呼叫 Service 新增留言
//
//	    return ResponseEntity.ok(saved); // 回傳新增後留言資料（前端用來渲染）
//	}

	
	
	
	/**
	 * 新增留言（需登入、且為該活動參加者）
	 *
	 * 流程：
	 * 1. 前端傳入活動 ID（eventId）
	 * 2. 從 session 取得登入會員的 memberId
	 * 3. 根據 eventId + memberId 查詢 eventMemberId（驗證是否參與活動）
	 * 4. 設定至 DTO 中，轉為 Entity 進行新增留言
	 * 5. 新增成功後轉為 DTO 回傳前端
	 * 注意：這邊用 commentsService.findEventMemberId() 查出對應關係。
	 */
	@PostMapping("/events/{eventId}/comments")
	public ResponseEntity<?> insertComments(@RequestBody CommentsDTO dto, HttpSession session) {
	    String eventId = dto.getEventId(); // 從前端拿活動 ID
//		String eventMemberId = dto.getEventMemberId();

	    if (eventId == null) {
	        return ResponseEntity.badRequest().body("活動編號不可為空");
	    }

		// 這一段是為了建立留言時綁定會員與活動的中介關聯（EventMember）
		// 由於前端只有拿到 eventId，後端必須透過 session 找出 memberId，再找出對應的 eventMemberId
		// 此方法會自動驗證該會員是否真的有參加該活動，否則回傳錯誤
	    // 改成由 Service 處理 session + memberId + eventId 查 eventMemberId
	    String eventMemberId = commentsService.findEventMemberIdBySessionMemberAndEvent(eventId, session);
	    if (eventMemberId == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入或尚未參加此活動，無法留言");
	    }

	    // 設定 eventMemberId 至 DTO，準備轉換為 Entity
//	    dto.setEventMemberId(eventMemberId);
//	    dto.setEventMember(new EventMember(eventMemberId)); // 指定留言對應的活動會員
//	    CommentsDTO saved = commentsService.insert(dto);    // 呼叫 Service 新增留言

	    // 將 DTO 轉 Entity 新增留言，並取得儲存後的留言資料
		System.out.println(eventMemberId);
		dto.setEventMemberId(eventMemberId);

	    Comments savedComment = commentsService.addCommentsReturnSaved(dto.toEntity());
	    // 將新增成功的留言轉為 DTO 回傳給前端渲染
	    CommentsDTO saved = commentsService.convertToDTO(savedComment);

	    return ResponseEntity.ok(saved); // 回傳新增後留言資料（前端用來渲染）
	}

	
	
	
	// 載入「活動圖片＋活動名稱＋主辦人＋留言板起訖時間＋活動地點」的資訊
	@GetMapping("/comments/eventInfo/{eventId}")
	public Map<String, Object> getEventInfo(@PathVariable String eventId) {
	    return commentsService.getEventInfoFromComments(eventId);
	}

	
//	@GetMapping("/comments/loginMember")
//	public ResponseEntity<?> getLoginMemberId(HttpSession session) {
//	    String memberId = SessionUtils.getLoginMemberId(session); // 從工具類取得會員ID，session.getAttribute("loginMember")
//	    if (memberId == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入或參加活動");
//	    }
//	    return ResponseEntity.ok(memberId);
//	}
//	
	// 先驗證是否登入會員
	@GetMapping("/comments/loginMember")
	public ResponseEntity<?> getLoginEventMemberId(HttpSession session, @RequestParam String eventId) {
	    String memberId = SessionUtils.getLoginMemberId(session); // 從工具類取得會員ID，session.getAttribute("loginMember")
	    if (memberId == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
	    }

	    String eventMemberId = commentsService.findEventMemberId(memberId, eventId);
	    if (eventMemberId == null) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("未參加此活動");
	    }

	    return ResponseEntity.ok(eventMemberId); // 回傳 EM001
	}

	
	

	
	
	
//	// 有參加活動的會員才能 新增 本人的留言
//	@PostMapping("/comments")
//	public CommentsDTO insertComments(@RequestBody Comments comments, HttpSession session) {
//	    Object obj = session.getAttribute("eventMember");
//	    EventMember eventMember = (obj instanceof EventMember) ? (EventMember) obj : null;
//
//	    if (eventMember == null) {
//	        throw new RuntimeException("尚未登入或未參加活動");
//	    }
//
//	    
//	    
//	    comments.setEventMember(eventMember);
//	    Comments saved = commentsService.addCommentsReturnSaved(comments);
//
//	    CommentsDTO dto = new CommentsDTO();
//	    dto.setCommentId(saved.getCommentId());
//	    dto.setCommentMessage(saved.getCommentMessage());
//	    dto.setCommentTime(saved.getCommentTime());
//	    dto.setEventMemberId(eventMember.getEventMemberId());
//	    //String memberId = eventMember.getMember().getMemberId();
//	   //String memberName = eventMember.getMember().getMemberName();
//	   //dto.setMemberName("<a href=\"/members/" + memberId + "/profile\">" + memberName + "</a>");
//		dto.setMemberName(comments.getEventMember().getMember().getMemberName());	
//		//dto.setImageUrl(comments.getEventMember().getMember().getMemberImage()); // 或其他頭像欄位
//  	//dto.setImageUrl(eventMember.getMember().getMemberImage());
//		dto.setImageUrl("/member/image/" + member.getMemberId());
//
//
//	    return dto;
//	}

//	// 有參加活動的會員才能 編輯 本人的留言
//	@PutMapping("/comments/{commentId}")
//	public ResponseEntity<?> updateComment(@PathVariable String commentId,
//	                                        @RequestBody Comments updatedComment,
//	                                        HttpSession session) {
//	    Object obj = session.getAttribute("eventMember");
//	    EventMember eventMember = (obj instanceof EventMember) ? (EventMember) obj : null;
//	    if (eventMember == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入或未參加活動");
//	    }
//
//	    Comments original = commentsService.getOneComments(commentId);
//	    if (original == null || !original.getEventMember().getEventMemberId().equals(eventMember.getEventMemberId())) {
//	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權限編輯他人留言");
//	    }
//
//	    original.setCommentMessage(updatedComment.getCommentMessage());
//	    commentsService.updateComments(original);
//	    return ResponseEntity.ok("留言已更新");
//	}
	
	
//	// 有參加活動的會員才能 編輯 本人的留言
//	@PutMapping("/comments/{commentId}")
//	public ResponseEntity<?> updateComment(@PathVariable String commentId,
//	                                       @RequestBody CommentsDTO dto,
//	                                       HttpSession session) {
//	    String eventMemberId = commentsService.findEventMemberIdBySessionMemberAndEvent(dto.getEventId(), session);
//	    if (eventMemberId == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入或未參加活動");
//	    }
//
//	    return commentsService.updateComment(commentId, dto.getCommentMessage(), eventMemberId);
//	}
	
	
	
//	// 有參加活動的會員才能 編輯 本人的留言
//	@PutMapping("/comments/{commentId}")
//	public ResponseEntity<?> updateComment(
//	        @PathVariable String commentId,
//	        @RequestBody CommentsDTO updated,
//	        HttpSession session) {
//
//	    Object obj = session.getAttribute("eventMember");
//	    EventMember sessionEventMember = (obj instanceof EventMember) ? (EventMember) obj : null;
//
//	    if (sessionEventMember == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入或未參加活動");
//	    }
//
//	    Comments original = commentsService.getOneComments(commentId);
//	    if (original == null) {
//	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("留言不存在");
//	    }
//
//	    if (!original.getEventMember().getEventMemberId().equals(sessionEventMember.getEventMemberId())) {
//	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權限編輯他人留言");
//	    }
//
//	    original.setCommentMessage(updated.getCommentMessage());
//	    commentsService.updateComments(original);
//	    return ResponseEntity.ok("留言已更新");
//	}

	
//	// 有參加活動的會員才能 編輯 本人的留言
//	@PutMapping("/comments/{commentId}")
//	public ResponseEntity<?> updateComment(
//	        @PathVariable String commentId,
//	        @RequestBody CommentsDTO updated,
//	        HttpSession session) {
//
//	    String memberId = SessionUtils.getLoginMemberId(session);
//	    if (memberId == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");
//	    }
//
//	    Comments original = commentsService.getOneComments(commentId);
//	    if (original == null) {
//	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("留言不存在");
//	    }
//
//	    // 比對目前登入會員與留言者是否為同一人（透過 event + member 關聯）
//	    String expectedEventMemberId = commentsService.findEventMemberId(memberId, original.getEvent().getEventId());
//	    if (!original.getEventMember().getEventMemberId().equals(expectedEventMemberId)) {
//	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權限編輯他人留言");
//	    }
//
//	    original.setCommentMessage(updated.getCommentMessage());
//	    commentsService.updateComments(original);
//	    return ResponseEntity.ok("留言已更新");
//	}
	
	
//	// 有參加活動的會員才能 編輯 本人的留言
//	@PutMapping("/comments/{commentId}")
//	public ResponseEntity<?> updateComment(@PathVariable String commentId,
//	                                       @RequestBody CommentsDTO dto,
//	                                       HttpSession session) {
//	    // 🔴 Step 1：取得登入會員 ID
//	    String memberId = SessionUtils.getLoginMemberId(session);
//	    if (memberId == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
//	    }
//
//	    // 🔴 Step 2：根據 memberId 與 eventId 找出 eventMember
//	    String eventId = dto.getEventId(); // 記得 DTO 要帶 eventId
//	    EventMember eventMember = eventMemberRepository.findByMemberMemberIdAndEventEventId(memberId, eventId);
//
//	    if (eventMember == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未參加活動");
//	    }
//
//	    // ✅ Step 3：設入 session（讓後續的權限檢查可用）
//	    session.setAttribute("eventMember", eventMember);
//
//	    // ✅ Step 4：交給原本的邏輯做檢查與更新
//	    return commentsService.updateCommentWithCheck(commentId, dto, eventMember);
//	}
	
	
	
	// 有參加活動的會員才能 編輯 本人的留言
	@PutMapping("/comments/{commentId}")
	public ResponseEntity<?> updateComment(@PathVariable String commentId,
	                                       @RequestBody CommentsDTO dto,
	                                       HttpSession session) {
		// 取得登入會員 ID
	    String memberId = SessionUtils.getLoginMemberId(session);
	    if (memberId == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入");
	    }
	    
	    // 根據 memberId 與 eventId 找出 eventMember
	    String eventId = dto.getEventId();
	    EventMember eventMember = eventMemberRepository.findByMemberMemberIdAndEventEventId(memberId, eventId);
	    if (eventMember == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未參加活動");
	    }

	    return commentsService.updateComment(commentId, dto.getCommentMessage(), eventMember.getEventMemberId());
	}

	
	

	// 有參加活動的會員才能 刪除 本人的留言
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<?> deleteComment(@PathVariable String commentId, HttpSession session) {
	    Object obj = session.getAttribute("eventMember");
	    EventMember eventMember = (obj instanceof EventMember) ? (EventMember) obj : null;
	    if (eventMember == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入或未參加活動");
	    }

	    Comments original = commentsService.getOneComments(commentId);
	    if (original == null || !original.getEventMember().getEventMemberId().equals(eventMember.getEventMemberId())) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權限刪除他人留言");
	    }

	    commentsService.deleteComments(commentId);
	    return ResponseEntity.ok("留言已刪除");
	}

	// 前端點擊該留言的會員名稱或大頭貼可查詢會員資料
	@GetMapping("/members/{eventMemberId}/profile")
	public ResponseEntity<?> getMemberProfileFromEventMember(@PathVariable String eventMemberId) {
	    Optional<EventMember> emOpt = eventMemberRepository.findById(eventMemberId);
	    if (emOpt.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到活動會員：" + eventMemberId);
	    }

	    Member member = emOpt.get().getMember();

	    Map<String, Object> data = new HashMap<>();
	    data.put("memberId", member.getMemberId());
	    data.put("memberName", member.getMemberName());
	    data.put("email", member.getEmail());
//	    data.put("phone", member.getPhone());
//	    data.put("birthday", member.getBirthday());

	    return ResponseEntity.ok(data);
	}

	
	
	
//	// 新增留言（從 session 拿 EventMember 與 Member，不用修改 SessionUtils）
//	@PostMapping("/comments")
//	public CommentsDTO insertComment(@RequestBody Comments comments, HttpSession session) {
//	    Object obj = session.getAttribute("eventMember");
//	    EventMember eventMember = (obj instanceof EventMember) ? (EventMember) obj : null;
//
//	    if (eventMember == null) {
//	        throw new RuntimeException("未登入或未參加活動");
//	    }
//
//	    Member member = eventMember.getMember();
//
//	    comments.setEventMember(eventMember);
//	    comments.setEvent(eventMember.getEvent());
//	    Comments saved = commentsService.addCommentsReturnSaved(comments);
//
//	    CommentsDTO dto = new CommentsDTO();
//	    dto.setCommentId(saved.getCommentId());
//	    dto.setCommentMessage(saved.getCommentMessage());
//	    dto.setCommentTime(saved.getCommentTime());
//	    dto.setEventMemberId(eventMember.getEventMemberId());
//	    dto.setMemberName("<a href=\"/members/" + eventMember.getEventMemberId() + "/profile\">" + member.getMemberName() + "</a>");
//	    dto.setProfilePictureUrl(member.getImageUrl());
//
//	    return dto;
//	}

//	// 查詢留言（只查該活動的留言）
//	@GetMapping("/comments/page/{page}/{size}")
//	public List<CommentsDTO> getCommentsPageForCurrentEvent(@PathVariable int page,
//	                                                         @PathVariable int size,
//	                                                         HttpSession session) {
//	    Object obj = session.getAttribute("eventMember");
//	    EventMember eventMember = (obj instanceof EventMember) ? (EventMember) obj : null;
//
//	    if (eventMember == null) {
//	        throw new RuntimeException("未登入或未參加活動");
//	    }
//
//	    String eventId = eventMember.getEvent().getEventId();
//	    return commentsService.getCommentsDTOPageByEvent(eventId, page, size);
//	}


	
	
	// 判斷是否為活動參加者，限制留言與查詢，檢查 session
//	@PostMapping("/comments")
//	public ResponseEntity<String> insert(@RequestBody Comments comments, HttpSession session) {
//	    EventMember eventMember = SessionUtils.getCurrentEventMember(session);
//	    if (eventMember == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未參加活動，無法留言");
//	    }
//	    comments.setEventMember(eventMember);
//	    commentsService.addComments(comments);
//	    return ResponseEntity.ok("留言成功");
//	}

//	@GetMapping("/comments/page")
//	public ResponseEntity<?> getCommentsPage(@RequestParam int page, @RequestParam int size, HttpSession session) {
//	    EventMember eventMember = SessionUtils.getCurrentEventMember(session);
//	    if (eventMember == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未參加活動，無法查看留言");
//	    }
//	    return ResponseEntity.ok(commentsService.getCommentsDTOPage(page, size));
//	}

	
	
}
