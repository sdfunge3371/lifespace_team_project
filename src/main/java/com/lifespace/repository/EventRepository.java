package com.lifespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lifespace.dto.EventResponse;
import com.lifespace.entity.Event;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepository extends JpaRepository<Event,String>{

	
	@Query(value = "SELECT " +
		    "e.event_id, e.event_name, e.event_date, e.event_start_time, e.event_end_time, " +
		    "e.event_category, e.space_id, e.member_id, e.number_of_participants, " +
		    "e.maximum_of_participants, e.event_briefing, e.remarks, e.host_speaking, " +
		    "e.created_time, mem.member_name, br.branch_addr, GROUP_CONCAT(ep.photo) AS photo_urls " +
		    "FROM event e "+
		    "LEFT JOIN event_photo ep ON e.event_id = ep.event_id " +
		    "LEFT JOIN member mem ON e.member_id = mem.member_id " +
		    "LEFT JOIN space sp ON e.space_id = sp.space_id " +
		    "LEFT JOIN branch br ON sp.branch_id = br.branch_id " +
		    "WHERE (:eventName IS NULL OR e.event_name LIKE CONCAT('%', :eventName, '%')) " +
		    "AND (:startTime IS NULL OR e.event_start_time >= :startTime) " +
		    "AND (:endTime IS NULL OR e.event_end_time <= :endTime) " +
		    "AND (:category IS NULL OR e.event_category = :category) "+
		    "GROUP BY e.event_id ",
		    countQuery = "SELECT " +
				    "e.event_id, e.event_name, e.event_date, e.event_start_time, e.event_end_time, " +
				    "e.event_category, e.space_id, e.member_id, e.number_of_participants, " +
				    "e.maximum_of_participants, e.event_briefing, e.remarks, e.host_speaking, " +
				    "e.created_time, mem.member_name, br.branch_addr, GROUP_CONCAT(ep.photo) AS photo_urls " +
				    "FROM event e "+
				    "LEFT JOIN event_photo ep ON e.event_id = ep.event_id " +
				    "LEFT JOIN member mem ON e.member_id = mem.member_id " +
				    "LEFT JOIN space sp ON e.space_id = sp.space_id " +
				    "LEFT JOIN branch br ON sp.branch_id = br.branch_id " +
				    "WHERE (:eventName IS NULL OR e.event_name LIKE CONCAT('%', :eventName, '%')) " +
				    "AND (:startTime IS NULL OR e.event_start_time >= :startTime) " +
				    "AND (:endTime IS NULL OR e.event_end_time <= :endTime) " +
				    "AND (:category IS NULL OR e.event_category = :category) "+
				    "GROUP BY e.event_id ",
		    nativeQuery = true)
	    Page<EventResponse> findEventsByConditions(
	            @Param("eventName") String eventName,
	            @Param("startTime") Timestamp startTime,
	            @Param("endTime") Timestamp endTime,
	            @Param("category") String category,
	            Pageable pageable);
	 
	 
	 	List<Event> findByMemberId(String userId);

	 
}
