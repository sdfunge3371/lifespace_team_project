package com.lifespace.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lifespace.SessionUtils;
import com.lifespace.dto.CommentsDTO;
import com.lifespace.entity.Comments;
import com.lifespace.entity.Event;
import com.lifespace.entity.EventMember;
import com.lifespace.entity.EventPhoto;
import com.lifespace.entity.Member;
import com.lifespace.entity.Orders;
import com.lifespace.repository.CommentsRepository;
import com.lifespace.repository.EventMemberRepository;
import com.lifespace.repository.EventPhotoRepository;
import com.lifespace.repository.EventRepository;
import com.lifespace.repository.OrdersRepository;

import jakarta.servlet.http.HttpSession;


@Service("commentsService")
public class CommentsService {

	@Autowired
	private CommentsRepository commentsRepository;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private OrdersRepository ordersRepository;
	
	@Autowired
	private EventMemberRepository eventMemberRepository;
	
	@Autowired
	private EventPhotoRepository eventPhotoRepository;
	
	@Autowired
	private EventRepository eventRepository;
	
	
	public void addComments(Comments comments) {
		commentsRepository.save(comments);
	}

	public void updateComments(Comments comments) {
		comments.setCommentId(comments.getCommentId()); 
		commentsRepository.save(comments);
	}

	public void deleteComments(String commentId) {
		if (commentsRepository.existsById(commentId))
			commentsRepository.deleteByCommentId(commentId);
//		    commentsRepository.deleteById(commentId);
	}

	public Comments getOneComments(String commentId) {
		Optional<Comments> optional = commentsRepository.findById(commentId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<Comments> getAll() {
//		List<Comments> list = commentsrepository.findAll();
//		return list;
		int i = 1;
		return commentsRepository.findAll(); //上面兩行簡寫為此行。abc
	}
	
	
	public CommentsDTO convertToDTO(Comments comments) {
	    CommentsDTO dto = new CommentsDTO();
	    dto.setCommentId(comments.getCommentId());
	    dto.setCommentMessage(comments.getCommentMessage());
	    dto.setCommentTime(comments.getCommentTime());
	    dto.setEventMemberId(comments.getEventMember().getEventMemberId());

	    EventMember em = comments.getEventMember();
	    Member member = (em != null) ? em.getMember() : null;

	    if (member != null) {
	        dto.setMemberName(member.getMemberName());
	        dto.setImageUrl("/member/image/" + member.getMemberId());
	    } else {
	        dto.setMemberName("匿名");
	        dto.setImageUrl(null);
	    }
	    return dto;
	}
	
	
	/**
	 * 從留言資料中找出活動資訊（活動名稱、圖片、主辦人、留言期間）
	 * 給前端留言板的「活動資訊區」使用
	 *
	 * 重點：留言必須至少有一筆，才能往回取得活動 → 訂單 → 主辦人
	 *
	 * @param eventId 活動 ID
	 * @return Map 包含活動資訊（eventName, photoUrls, organizerName, orderStart, orderEnd）
	 */	
	// 從留言中取得關聯活動資訊
    public Map<String, Object> getEventInfoFromComments(String eventId) {
        Map<String, Object> data = new HashMap<>();

//        List<Comments> commentsList = commentsRepository.findByEventMember_Event_EventId(eventId);
//        if (commentsList.isEmpty()) return data;
//
//        // 取得留言所屬活動（只取第一筆留言）
//        Event event = commentsList.get(0).getEventMember().getEvent();
        Event event = eventRepository.findById(eventId).orElse(null);

        // 活動名稱abc
        data.put("eventName", event.getEventName());

        // 活動圖片
//        List<String> photoUrls = event.getPhotoUrls();
//        data.put("photoUrls", photoUrls != null ? photoUrls : Collections.emptyList());
        
/**
 * 還有報錯需要處理，故先註解
        List<EventPhoto> photoList = event.getEventPhotos();
        List<String> photoUrls = encodePhotosToBase64(photoList); // ✅ 呼叫轉換方法
        data.put("photoUrls", photoUrls);
*/

        // 取得主辦人姓名、地址
        Orders orders = ordersRepository.findByEventEventId(eventId).orElse(null);
        
//待刪除開始~       
//        if (orders != null) {
//            System.out.println("✔ Order Start: " + orders.getOrderStart());
//            System.out.println("✔ Order End: " + orders.getOrderEnd());
//        } else {
//            System.out.println("⚠ 查無 orders 資料");
//        }
//
//        
//        String holderName = orders.getMember().getMemberName();
//        data.put("holderName", holderName);
//        String branchId = orders.getBranch().getBranchAddr();
//        data.put("spaceLocation", branchId);
//        
//        
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        if (orders != null) {
//            data.put("orderStart", orders.getOrderStart() != null ? orders.getOrderStart().format(formatter) : "");
//            data.put("orderEnd", orders.getOrderEnd() != null ? orders.getOrderEnd().format(formatter) : "");
//        }

        
//        Orders orders = ordersRepository.findByEventEventId(eventId).orElse(null);
//待刪除結束
        
        if (orders != null) {
            String holderName = orders.getMember().getMemberName();
            data.put("holderName", holderName);

            // 留言板開放時間／結束時間
            data.put("orderStart", orders.getOrderStart());
            data.put("orderEnd", orders.getOrderEnd());

            String branchId = orders.getBranch().getBranchAddr();
            data.put("spaceLocation", branchId);
        }

        
        
        
        // 找到參加活動者的會員
        List<EventMember> eventMembers = eventMemberRepository.findByEvent_EventId(eventId);
        
        List<Map<String, Object>> memberInfos = new ArrayList<>();
    
        for(EventMember eventMember : eventMembers) {
        	Map<String, Object> memberInfo = new HashMap<>();
        	memberInfo.put("memberName", eventMember.getMember().getMemberName());
        	memberInfo.put("memberImage", eventMember.getMember().getMemberImage());
        	memberInfos.add(memberInfo);
        	
        }
        
        data.put("eventMembers", memberInfos);
        
        // 找活動照片
//        List<EventPhoto> photos = eventPhotoRepository.findByEventEventId(eventId);
//        data.put("eventPhotos", photos);
        
        
        
        
//        // 目前已經不使用輪播圖片，故先註解這段邏輯，避免找不到指定圖片
//        List<EventPhoto> photos = eventPhotoRepository.findByEventEventId(eventId);
//        
//        // 把每一張照片讀出來並轉成 base64，然後只回傳 base64 字串陣列（或物件）
//        List<String> photoBase64List = new ArrayList<>();
//        for (EventPhoto photo : photos) {
//            String path = photo.getPhoto(); // 假設 photo 欄位是檔案路徑
//            try {
//                byte[] fileContent = Files.readAllBytes(Paths.get(path));
//                String base64 = Base64.getEncoder().encodeToString(fileContent);
//                photoBase64List.add(base64);
//            } catch (IOException e) {
//                e.printStackTrace();
//                // 可選：photoBase64List.add("error")
//            }
//        }
//
//        data.put("eventPhotos", photoBase64List);

        
        
//        List<Orders> orders = event.getOrdersList(); // 使用自己寫的 getOrdersList()
//        if (!orders.isEmpty()) {
//            Orders first = orders.get(0);
//            data.put("orderStart", first.getOrderStart());
//            data.put("orderEnd", first.getOrderEnd());
//
//            Member organizer = first.getMember();
//            String organizerName = (organizer != null) ? organizer.getMemberName() : "未知主辦人";
//            data.put("organizerName", organizerName);
//        } else {
//            data.put("organizerName", "未指定");
//            data.put("orderStart", null);
//            data.put("orderEnd", null);
//        }
//
//        data.put("spaceLocation", first.getSpaceLocation());
        
        return data;
    }
	
    
    
    
    
    public String findEventMemberId(String memberId, String eventId) {
        EventMember eventMember = eventMemberRepository.findByMemberMemberIdAndEventEventId(memberId, eventId);
        return eventMember != null ? eventMember.getEventMemberId() : null;
    }
    
    
    
    /**
     * 根據目前登入會員 (從 session 拿) 及活動 ID，查出對應的 event_member_id
     */
    public String findEventMemberIdBySessionMemberAndEvent(String eventId, HttpSession session) {
        String memberId = SessionUtils.getLoginMemberId(session); // 從 session 拿 memberId

        if (memberId == null || eventId == null) {
            return null;
        }

        EventMember eventMember = eventMemberRepository.findByMemberMemberIdAndEventEventId(memberId, eventId);
        if (eventMember == null) {
            return null; // 表示此會員沒有參加這個活動
        }

        return eventMember.getEventMemberId();
    }

    


	// 讓 Controller 拿到儲存後的留言資訊（包含 commentId 與 commentTime）
	public Comments addCommentsReturnSaved(Comments comments) {
		return commentsRepository.save(comments);
	}
	
	
	// 【留言分頁查詢的邏輯處理】
	// 根據分頁參數（第幾頁、每頁幾筆）從資料庫取得留言
	// 把每筆留言（Comments）轉換成 CommentsDTO 給前端使用
	public List<CommentsDTO> getCommentsDTOPage(int page, int size) {
	    // 依留言時間（commentTime）做「舊到新」排序
	    Pageable pageable = PageRequest.of(page, size, Sort.by("commentTime").ascending());

	    return commentsRepository.findAll(pageable)
	            .stream()
	            .map(comments -> {
	                CommentsDTO dto = new CommentsDTO();

	                // 基本留言資訊
	                dto.setCommentId(comments.getCommentId());
	                dto.setCommentMessage(comments.getCommentMessage());
	                dto.setCommentTime(comments.getCommentTime());

	                // 留言者身份（透過 EventMember → Member）
	                dto.setEventMemberId(comments.getEventMember().getEventMemberId());

	                EventMember em = comments.getEventMember();
	                Member member = (em != null) ? em.getMember() : null;
	                if (member != null) {
	                    dto.setMemberName(member.getMemberName()); // 顯示留言者名稱
	                    dto.setImageUrl("/member/image/" + member.getMemberId()); // 大頭貼網址
	                } else {
	                    dto.setMemberName("匿名");
	                    dto.setImageUrl(null);
	                }

	                return dto;
	            })
	            .collect(Collectors.toList());
	}
	
	
	
	/**
	 * 根據活動 ID 取得留言清單（支援分頁）
	 * 用於前端留言板主列表顯示（一次抓幾筆）
	 *
	 * @param eventId 活動 ID（留言所屬的活動編號）
	 * @param page 頁數（第幾頁，從 0 開始）
	 * @param size 每頁筆數
	 * @return 留言 DTO 清單
	 */
	public List<CommentsDTO> getCommentsDTOByEventId(String eventId, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by("commentTime").ascending());

	    return commentsRepository.findByEventMember_Event_EventId(eventId, pageable)
	            .stream()
	            .map(comments -> {
	                CommentsDTO dto = new CommentsDTO();
	                dto.setCommentId(comments.getCommentId());
	                dto.setCommentMessage(comments.getCommentMessage());
	                dto.setCommentTime(comments.getCommentTime());
	                dto.setEventMemberId(comments.getEventMember().getEventMemberId());

	                Member member = comments.getEventMember().getMember();
	                if (member != null) {
	                    dto.setMemberName(member.getMemberName());
	                    dto.setImageUrl("/member/image/" + member.getMemberId());
	                }

//	                // 👇 主辦人與留言時間補充
//	                EventMember em = comments.getEventMember();
//	                Event event = em.getEvent();
//	                if (event != null && !event.getOrdersList().isEmpty()) {
//	                    Orders first = event.getOrdersList().get(0);
//	                    dto.setOrderStart(first.getOrderStart());
//	                    dto.setOrderEnd(first.getOrderEnd());
////	                    dto.setSpaceLocation(first.getSpace().getSpaceName()); // 記得 Event → Orders → Space → SpaceName
//
//
//	                    Member organizer = first.getMember();
//	                    dto.setOrganizerName(organizer != null ? organizer.getMemberName() : "未知主辦人");
//	                    
////	                    dto.setSpaceLocation(first.getSpace().getSpaceName()); // 新增這行來取得空間地點名稱
//	                }
	                
	                
//	                Member member = comments.getEventMember().getMember();
//	                if (member != null) {
//	                    dto.setMemberName(member.getMemberName());
//	                    dto.setImageUrl("/member/image/" + member.getMemberId());
//	                }

	                return dto;
	            })
	            .collect(Collectors.toList());
	}
	
	
	public ResponseEntity<?> updateComment(String commentId, String newMessage, String eventMemberId) {
	    Comments original = getOneComments(commentId);
	    if (original == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("留言不存在");
	    }

	    if (!original.getEventMember().getEventMemberId().equals(eventMemberId)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權限編輯他人留言");
	    }

	    original.setCommentMessage(newMessage);
	    updateComments(original);
	    
	    	    

	    return ResponseEntity.ok("留言已更新");
	    
	    
	}

	
	

/**
 * 還有報錯需要處理，故先註解	
	// 🔧 將活動照片轉為 Base64 字串格式（供前端圖片輪播顯示）
	private List<String> encodePhotosToBase64(List<EventPhoto> photos) {
	    List<String> encodedPhotos = new ArrayList<>();
	    for (EventPhoto photo : photos) {
	        byte[] photoBytes = photo.getPhoto(); // getPhoto() 為 byte[]
	        if (photoBytes != null && photoBytes.length > 0) {
	            String base64 = Base64.getEncoder().encodeToString(photoBytes);
	            encodedPhotos.add(base64);
	        }
	    }
	    return encodedPhotos;
	}
*/


    
	
	
//	public Map<String, Object> getEventInfoFromComments(String eventId) {
//	    Map<String, Object> data = new HashMap<>();
//
//	    // 透過留言找到該活動
//	    List<Comments> commentsList = commentsRepository.findByEvent_EventId(eventId);
//	    if (commentsList.isEmpty()) return data;
//
//	    Event event = commentsList.get(0).getEventMember().getEvent();
//
//	    // 活動名稱
//	    data.put("eventName", event.getEventName());
//
//	    // 活動輪播圖片
//	    data.put("photoUrls", event.getPhotoUrls());
//
//	    // 從活動訂單中取第一筆訂單
//	    List<Orders> orders = new ArrayList<>(event.getOrdersList());
//	    if (!orders.isEmpty()) {
//	        Orders first = orders.get(0);
//	        data.put("orderStart", first.getOrderStart());
//	        data.put("orderEnd", first.getOrderEnd());
//
//	        // 用 memberId + service 取主辦人名稱
//	        String memberId = first.getMemberId();
//	        Optional<Member> organizerOpt = memberService.findByIdMem(memberId);
//	        String organizerName = organizerOpt.map(Member::getMemberName).orElse("未知");
//	        data.put("organizerName", organizerName);
//	    } else {
//	        data.put("organizerName", "未指定");
//	    }
//
//	    return data;
//	}

	
	
//	public List<CommentsDTO> getCommentsDTOPage(int page, int size) {
//	    Pageable pageable = PageRequest.of(page, size, Sort.by("commentTime").descending());
//	    return commentsRepository.findAll(pageable)
//	            .stream()
//	            .map(comment -> {
//	                CommentsDTO dto = new CommentsDTO();
//
//	                // ✅ 這裡開始補內容
//	                dto.setCommentId(comment.getCommentId());
//	                dto.setCommentMessage(comment.getCommentMessage());
//	                dto.setCommentTime(comment.getCommentTime());
//
//	                // 取得 EventMember → Member
//	                EventMember em = comment.getEventMember();
//	                Member member = (em != null) ? em.getMember() : null;
//	                if (member != null) {
//	                    dto.setMemberName(member.getMemberName());
//	                    dto.setImageUrl("/member/image/" + member.getMemberId());
//	                    dto.setEventMemberId(em.getEventMemberId());
//	                }
//
//	                // 取得 Event → Orders
//	                Event event = em.getEvent();
//	                if (event != null && event.getOrdersList() != null && !event.getOrdersList().isEmpty()) {
//	                    Orders firstOrder = event.getOrdersList().get(0);
//	                    dto.setOrderStart(firstOrder.getOrderStart());
//	                    dto.setOrderEnd(firstOrder.getOrderEnd());
//
//	                    Member organizer = firstOrder.getMember();
//	                    dto.setOrganizerName(organizer != null ? organizer.getMemberName() : "未知主辦人");
//	                } else {
//	                    dto.setOrganizerName("尚無訂單");
//	                }
//
//	                return dto;
//	            })
//	            .collect(Collectors.toList());
//	}

	
	
	
	
//	@Autowired
//	private MemberService memberService;
//
//	public Map<String, Object> getEventInfoFromComments(String eventId) {
//	    Map<String, Object> data = new HashMap<>();
//
//	    // 透過留言找到該活動
//	    List<Comments> commentsList = commentsRepository.findByEvent_EventId(eventId);
//	    if (commentsList.isEmpty()) return data;
//
//	    Event event = commentsList.get(0).getEventMember().getEvent();
//
//	    // 活動名稱
//	    data.put("eventName", event.getEventName());
//
//	    // 活動輪播圖片（已封裝方法）
//	    data.put("photoUrls", event.getPhotoUrls());
//
//	    // 從活動訂單中取第一筆訂單，作為留言時間與主辦人來源
//	    List<Orders> orders = new ArrayList<>(event.getOrders());
//	    if (!orders.isEmpty()) {
//	        Orders first = orders.get(0);
//	        data.put("orderStart", first.getOrderStart());
//	        data.put("orderEnd", first.getOrderEnd());
//
//	        // 從第一筆訂單取得主辦人（Member）
//	        Member organizer = first.getMember();
//	        if (organizer != null) {
//	            data.put("organizerName", organizer.getMemberName());
//	        } else {
//	            data.put("organizerName", "未知");
//	        }
//	    } else {
//	        data.put("organizerName", "未指定");
//	    }
//
//	    return data;
//	}
	

	
	
	
	
//	// 前台留言板分頁功能	
//	public List<CommentsDTO> getCommentsDTOPage(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("commentTime").ascending());
//        return commentsRepository.findAll(pageable).stream().map(comments -> {
//            CommentsDTO dto = new CommentsDTO();
//            dto.setCommentId(comments.getCommentId());
//            dto.setCommentMessage(comments.getCommentMessage());
//            dto.setCommentTime(comments.getCommentTime());
//            //dto.setMemberName(comments.getEventMember().getMember().getMemberName());
//            dto.setEventMemberId(comments.getEventMember().getEventMemberId()); // 搭配JS判斷留言是否屬於本人
//            
//            
//            EventMember em = comments.getEventMember(); // 拿到留言的活動會員
//            Member member = (em != null) ? em.getMember() : null; // 拿到實際會員的資料（Member）
//
//            if (member != null) {
//                dto.setMemberName(member.getMemberName()); // 留言者名稱
//                //dto.setImageUrl(member.getMemberImage());  // ✅ 使用 getMemberImage()
//                dto.setImageUrl("/member/image/" + member.getMemberId());
//
//            } else {
//                dto.setMemberName("匿名");
//                dto.setImageUrl(null);
//            }
//
//            
//            
//            //dto.setMemberName(eventMember.getMember().getMemberName());
//            //dto.setCommentMessage(comment.getCommentMessage());
//            //dto.setCommentTime(comment.getCommentTime());
//
//            
//            //String memberId = comments.getEventMember().getMember().getMemberId();
//            //dto.setProfilePictureUrl("/members/" + memberId + "/image");
//
//            return dto;
//        }).collect(Collectors.toList());
//    }

	
}