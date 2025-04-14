package com.lifespace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lifespace.entity.EventPhoto;

public interface EventPhotoRepository extends JpaRepository<EventPhoto,String>{

	
	// 【04/14薇婷新增】找到活動照片
	List<EventPhoto> findByEventEventId(String EventId);
}
