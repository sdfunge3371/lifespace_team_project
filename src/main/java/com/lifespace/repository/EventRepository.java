package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lifespace.entity.Event;

import java.sql.Timestamp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepository extends JpaRepository<Event,String>{

	
	 @Query(value = "SELECT DISTINCT e.* FROM event e " +
	           "LEFT JOIN event_photo ep ON e.event_id = ep.event_id " +
	           "WHERE (:eventName IS NULL OR e.event_name LIKE CONCAT('%', :eventName, '%')) " +
	           "AND (:startTime IS NULL OR e.event_start_time >= :startTime) " +
	           "AND (:endTime IS NULL OR e.event_end_time <= :endTime) " +
	           "AND (:category IS NULL OR e.event_category = :category)",
	           countQuery = "SELECT COUNT(DISTINCT e.event_id) FROM event e " +
	           "LEFT JOIN event_photo ep ON e.event_id = ep.event_id " +
	           "WHERE (:eventName IS NULL OR e.event_name LIKE CONCAT('%', :eventName, '%')) " +
	           "AND (:startTime IS NULL OR e.event_start_time >= :startTime) " +
	           "AND (:endTime IS NULL OR e.event_end_time <= :endTime) " +
	           "AND (:category IS NULL OR e.event_category = :category)",
	           nativeQuery = true)
	    Page<Event> findEventsByConditions(
	            @Param("eventName") String eventName,
	            @Param("startTime") Timestamp startTime,
	            @Param("endTime") Timestamp endTime,
	            @Param("category") String category,
	            Pageable pageable);

	 
}
