package com.lifespace.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lifespace.dto.EventMemberResponse;
import com.lifespace.dto.EventRequest;
import com.lifespace.dto.EventResponse;
import com.lifespace.entity.Event;
import com.lifespace.entity.EventCategory;
import com.lifespace.entity.Orders;
import com.lifespace.repository.EventRepository;
import com.lifespace.repository.OrdersRepository;
import com.lifespace.service.EventPhotoService;
import com.lifespace.service.EventService;
import com.lifespace.service.OrdersService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


@RestController
@CrossOrigin
@RequestMapping("/lifespace/event")
public class EventController {

	@Autowired
	private EventService eventSvc;
	
	@Autowired
	private OrdersService orderSvc;
	
	@Autowired
	private EventPhotoService eventPhotoSvc;
	
	@Autowired
    private EventRepository eventRepository;
	
	@Autowired
    private OrdersRepository ordersRepository;
	
	@PostMapping("/add")
    public  ResponseEntity<?> insert(
    		@Valid @RequestPart("eventRequest") EventRequest eventRequest,
            BindingResult bindingResult, // 新增這個參數來接收驗證錯誤
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            HttpSession session) {

		String organizerId = (String) session.getAttribute("loginMember");

	    if (organizerId == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入再建立活動");
	    }

	    eventRequest.setOrganizerId(organizerId);

	 // 驗證活動新增的參數是否符合基本規範
	    if (bindingResult.hasErrors()) {
	        StringBuilder errorMsg = new StringBuilder("輸入資料格式錯誤: ");
	        bindingResult.getFieldErrors().forEach(error ->
	            errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ")
	        );
	        return ResponseEntity.badRequest().body(errorMsg.toString());
	    }
	    
	    // 驗證活動時間是否在訂單時間範圍內
	    Orders order = ordersRepository.findById(eventRequest.getOrderId()).orElse(null);
	    Timestamp eventStart = eventRequest.getEventStartTime();
	    Timestamp eventEnd = eventRequest.getEventEndTime();
	   
	    final Pattern numberPattern = Pattern.compile("^[1-9]\\d*$");

	    
	    if (eventStart.before(order.getOrderStart()) || eventEnd.after(order.getOrderEnd())) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("活動時間需在訂單時間範圍內");
	    }else if(eventRequest.getMaximumOfParticipants() > order.getSpace().getSpacePeople()) {
	    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("活動人數上限不可超出空間容量");
	    }

	    eventSvc.addEvent(eventRequest, photos);
	    return ResponseEntity.ok("活動建立成功");
    }
    
	@PutMapping("/updateStatus")
    public String updateStatus(@RequestParam(required = true) String eventId, @RequestParam(required = true) String status) {
    	eventSvc.updateEventStatus(eventId, status);
        return "執行update event status jpa方法";
    }
    
    @PutMapping("/addMemToEvent")
    public ResponseEntity<String> addToEvent(@RequestParam(required = true) String eventId, HttpSession session )
    		 throws Exception {
    	
        String memberId = (String) session.getAttribute("loginMember");
        
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入才能參加活動");
        }
        
    	eventSvc.addMemberToEvent(memberId, eventId);
        return ResponseEntity.ok("執行add event member jpa方法");
    }
    
    @PutMapping("/removeMemFromEvent")
    public ResponseEntity<String> removeFromEvent(@RequestParam(required = true) String eventId, HttpSession session ) {
    	String memberId = (String) session.getAttribute("loginMember");
       
    	if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入才能取消參加活動");
        }
    	
        eventSvc.removeMemberFromEvent(memberId, eventId);
        return ResponseEntity.ok("執行remove event member jpa方法");
    }
    
    @GetMapping("/getNewEvents")
    public Page<Event> getNewEvents(
    		 @RequestParam(defaultValue = "6") @Max(10) @Min(0) Integer size,
             @RequestParam(defaultValue = "0") @Min(0) Integer page) {
    	
    	// 創建分頁和排序條件
        Pageable pageable = PageRequest.of( page, size,
        			Sort.by("createdTime").descending()
        );
        
    	  Page<Event> events = eventSvc.getAll(pageable);
//          for (Event event : events) {
//              event.getPhotoUrls(); // 確保 photoUrls 被填充
//          }
          
        return events;
    }
    
    @GetMapping("/getAll")
    public Page<Event> getAllforOverview(
    		 @RequestParam(defaultValue = "5") @Max(10) @Min(0) Integer size,
             @RequestParam(defaultValue = "0") @Min(0) Integer page) {
    	
    	// 創建分頁和排序條件
        Pageable pageable = PageRequest.of( page, size,
        		Sort.by("numberOfParticipants").descending() );
        
    	  Page<Event> events = eventSvc.getAll(pageable);
    	//載入圖片資料（若有必要）
//    	  for (Event event : events.getContent()) {
//    		  Hibernate.initialize(event.getPhotoUrls());
//    	}
          
        return events;
    }
    
    @GetMapping("/getOne")
    public EventResponse getOneEvent(@RequestParam String eventId) {
    	System.out.println("被要求檔案");
    	EventResponse event = eventSvc.getOneEvent(eventId);
        
        event.getPhotoUrls(); // 確保放入 photoUrls 圖片地址
        
        return event;
    }
    
    //獲取所有活動類別
    @GetMapping("/getAllCategories")
    public ResponseEntity<List<EventCategory>> getAllCategories() {
    	System.out.println("被要求檔案");
    	List<EventCategory> categories = eventSvc.findAllEventsCategory();
    	
        return ResponseEntity.ok(categories);
    }
 
    @GetMapping("/search/native")
    public ResponseEntity<Page<EventResponse>> searchEvents( @RequestParam(required = false) String eventName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String branch,
            @RequestParam(defaultValue = "5") @Max(10) @Min(0) Integer size,
            @RequestParam(defaultValue = "0") @Min(0) Integer page) {
        // 創建分頁和排序條件
        Pageable pageable = PageRequest.of( page, size);
        
        // 處理空字符串
        eventName = (eventName != null && eventName.trim().isEmpty()) ? null : eventName;
        category = (category != null && category.trim().isEmpty()) ? null : category;
        branch = (branch != null && branch.trim().isEmpty()) ? null : branch;
        
        // 將LocalDateTime轉換為Timestamp
        Timestamp eventStartTime = startTime != null ? Timestamp.valueOf(startTime) : null;
        Timestamp eventEndTime = endTime != null ? Timestamp.valueOf(endTime) : null;
        // 呼叫服務層的搜尋方法
        Page<EventResponse> result = eventSvc.searchEvents(eventName, eventStartTime, eventEndTime, category,
        		branch, pageable);
        
        return ResponseEntity.ok(result);
    }
    
    //使用者根據種類篩選出活動列表 
    @GetMapping("/search/ByMember")
    public ResponseEntity<Page<EventMemberResponse>> filterEventsByUser(
    		HttpSession session, // 取得使用者資訊
    		@RequestParam(required = true) String userCategory,
    		//@RequestParam(required = false) String memberId,
            @RequestParam(required = false) String participateStatus,
            @RequestParam(required = false) String eventStatus,
            @RequestParam(required = false) String organizerId,
            @RequestParam(defaultValue = "5") @Max(10) @Min(0) Integer size,
            @RequestParam(defaultValue = "0") @Min(0) Integer page) {
    	
    	 // 從 session 取得會員 ID
        String memberId = (String) session.getAttribute("loginMember"); 

        // 檢查是否成功取得使用者 ID
        if (memberId == null) {
            // 如果 session 中沒有使用者 ID，表示使用者未登入，回傳錯誤或導向登入頁面
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
        // 創建分頁和排序條件
        Pageable pageable = PageRequest.of( page, size
        			//Sort.by("eventStartTime").descending()
        );
        // 處理空字符串
        memberId = (memberId != null && memberId.trim().isEmpty()) ? null : memberId;
        participateStatus = (participateStatus != null && participateStatus.trim().isEmpty()) ? null : participateStatus;
        eventStatus = (eventStatus != null && eventStatus.trim().isEmpty()) ? null : eventStatus;
        organizerId = (organizerId != null && organizerId.trim().isEmpty()) ? null : organizerId;
       
        Page<EventMemberResponse> result = eventSvc.filterEventsByUser(
        		userCategory, memberId, participateStatus, eventStatus, organizerId, pageable);
        
        return ResponseEntity.ok(result);
    }
    
  //舉辦者取消活動
    @PutMapping("/cancell")
    public ResponseEntity<String> cancellEvent(
    		  @RequestParam String eventId,
    	      HttpSession session ){
    	
    	 String organizerId = (String) session.getAttribute("loginMember");

    	 // 未登入時拒絕操作
    	  if (organizerId == null) {
    	       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入後再取消活動");
    	  }

    	  try {
    	        eventSvc.cancellEvent(organizerId, eventId); 
    	        return ResponseEntity.ok("活動取消成功");
    	    } catch (Exception e) {
    	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("取消活動失敗：" + e.getMessage());
    	    }
    }
    
    //檢查使用者在特定活動的參與狀態
    @GetMapping("/check/eventMemberStatus")
    public ResponseEntity<?> checkParticipationStatus(@RequestParam(required = true) String eventId, HttpSession session) {
        String memberId = (String) session.getAttribute("loginMember");
        
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
        }
        
        Map<String, Object> response = new HashMap<>();
        //若使用者已參加或已候補
        if( eventSvc.checkMemberEventStatus(eventId, memberId) ) {
             response.put( "participateStatus", "PARTICIPATING" );
             return ResponseEntity.ok(response);
        }else {
        	//若使用者未參加也未候補
        	 response.put("participateStatus", "NOT_PARTICIPATING");
             return ResponseEntity.ok(response);
        }
    }
}
