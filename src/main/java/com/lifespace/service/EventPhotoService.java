package com.lifespace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.entity.EventPhoto;
import com.lifespace.repository.EventPhotoRepository;

@Service("eventPhotoService")
public class EventPhotoService {

	@Autowired
	EventPhotoRepository repository;

	public void addEventPhoto(EventPhoto eventPhotoEntity) {
		repository.save(eventPhotoEntity);
	}

	public void updateEventPhoto(EventPhoto eventPhotoEntity) {
		repository.save(eventPhotoEntity);
	}

	public void deleteEventPhoto(String eventPhotono) {
		if (repository.existsById(eventPhotono)) {
			repository.deleteById(eventPhotono);
		}
//		    repository.deleteById(empno);
	}

	public EventPhoto getOneEventPhoto(String eventPhotono) {
		Optional<EventPhoto> optional = repository.findById(eventPhotono);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<EventPhoto> getAllEventPhoto() {
		return repository.findAll();
	}
}