package com.lifespace.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lifespace.dto.EventRequest;
import com.lifespace.entity.Event;
import com.lifespace.repository.EventRepository;
import com.lifespace.service.EventPhotoService;
import com.lifespace.service.EventService;


@RestController
@CrossOrigin
@RequestMapping("/lifespace/event")
public class EventController {

	@Autowired
	EventService eventSvc;
	
	@Autowired
	EventPhotoService eventPhotoSvc;
	
	@Autowired
    private EventRepository eventRepository;

	@PostMapping("/add")
    public String insert(
            @RequestPart("eventRequest") EventRequest eventRequest,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        eventSvc.addEvent(eventRequest, photos);
        return "執行 insert event jpa 方法";
    }
    
    @PostMapping("/update")
    public String update(@RequestBody Event eventRequest) {
    	eventSvc.updateEvent(eventRequest);
        return "執行update event jpa方法";
    }
    
    @GetMapping("/getAll")
    public List<Event> getAll() {
    	System.out.println("被要求檔案");
    	  List<Event> events = eventSvc.getAll();
          for (Event event : events) {
              event.getPhotoUrls(); // 確保 photoUrls 被填充
          }
        return events;
    }
    
    @GetMapping("/getOne")
    public Event getOneEvent(@RequestParam String eventId) {
    	System.out.println("被要求檔案");
    	Event event = eventSvc.getOneEvent(eventId);
        
        event.getPhotoUrls(); // 確保 photoUrls 被填充
        
        return event;
    }
    
}
