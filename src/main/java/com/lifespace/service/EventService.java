package com.lifespace.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lifespace.entity.EventEntity;
import com.lifespace.entity.EventPhotoEntity;
import com.lifespace.entity.EventRequest;
import com.lifespace.repository.EventPhotoRepository;
import com.lifespace.repository.EventRepository;


@Service("eventService")
public class EventService {

	@Autowired
	EventRepository eventRepository;

	@Autowired
    EventPhotoRepository eventPhotoRepository; // 注入 EventPhotoRepository
	
	public void addEvent(EventRequest eventRequest, List<MultipartFile> photos) {
		
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId("E087");
        eventEntity.setEventName(eventRequest.getEventName());
        eventEntity.setEventDate(eventRequest.getEventDate());
        eventEntity.setEventStartTime(eventRequest.getEventStartTime());
        eventEntity.setEventEndTime(eventRequest.getEventEndTime());
        eventEntity.setEventCategory(eventRequest.getEventCategory());
        eventEntity.setSpaceId(eventRequest.getSpaceId());
        eventEntity.setMemberId(eventRequest.getMemberId());
        eventEntity.setMaximumOfParticipants(eventRequest.getMaximumOfParticipants());
        eventEntity.setEventBriefing(eventRequest.getEventBriefing());
        eventEntity.setRemarks(eventRequest.getRemarks());
        eventEntity.setHostSpeaking(eventRequest.getHostSpeaking());
        eventEntity.setCreatedTime(currentTime);
        eventEntity.setNumberOfParticipants(0); // 確保預設為 0

        eventRepository.save(eventEntity);

        // 處理照片上傳
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                try {
                    // 儲存檔案到指定位置，並取得檔案路徑
                    String photoPath = savePhoto(photo);

                    EventPhotoEntity eventPhoto = new EventPhotoEntity();
                    eventPhoto.setEvent(eventEntity);
                    eventPhoto.setPhoto(photoPath);
                    eventPhoto.setCreatedTime(currentTime);
                    eventPhoto.setPhotoId("P087"); // 生成唯一的 photoId
                    eventPhotoRepository.save(eventPhoto);

                } catch (Exception e) {
                    // 處理檔案儲存失敗的例外
                    e.printStackTrace();
                    // 可以選擇拋出例外或記錄錯誤
                }
            }
        }
	}

	public void updateEvent(EventEntity eventEntity) {
		eventRepository.save(eventEntity);
	}

	public void deleteEvent(String eventno) {
		if (eventRepository.existsById(eventno)) {
			eventRepository.deleteById(eventno);
		}
//		    repository.deleteById(empno);
	}

	public EventEntity getOneEvent(String eventno) {
		Optional<EventEntity> optional = eventRepository.findById(eventno);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<EventEntity> getAll() {
		return eventRepository.findAll();
	}
	
	// 儲存照片並返回檔案路徑
//	private String savePhoto(MultipartFile photo) throws Exception {
//	    String fileName = photo.getOriginalFilename();
//	    String uploadDir = "D://tiba_project//event_images"; // 替換為您選擇的儲存目錄
//
//	    // 確保目錄存在
//	    File dir = new File(uploadDir);
//	    if (!dir.exists()) {
//	        if (!dir.mkdirs()) { // 建立目錄及其父目錄
//	            throw new IOException("無法建立目錄: " + uploadDir);
//	        }
//	    }
//
//	    String filePath = uploadDir + "/" + fileName;
//	    photo.transferTo(new File(filePath));
//	    return filePath;
//	}
	
	private String savePhoto(MultipartFile photo) throws Exception {
	    String fileName = photo.getOriginalFilename();
	    String uploadDir = "D://tiba_project//event_images"; // 替換為您的實際儲存目錄

	    // 確保目錄存在
	    File dir = new File(uploadDir);
	    if (!dir.exists()) {
	        if (!dir.mkdirs()) {
	            throw new IOException("無法建立目錄: " + uploadDir);
	        }
	    }

	    String filePath = uploadDir + "/" + fileName;
	    photo.transferTo(new File(filePath));
	    return "/event-images/" + fileName; // 返回可訪問的 URL
	}
}
