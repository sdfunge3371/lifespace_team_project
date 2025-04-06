package com.lifespace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.lifespace.entity.Comments;

public interface CommentsRepository extends JpaRepository<Comments, String>{

	@Transactional
	@Modifying
	@Query(value = "delete from comments where comment_id =?1", nativeQuery = true)
	void deleteByCommentId(String commentId);

//	//● (自訂)條件查詢
//	@Query(value = "from Comments where comment_id=?1 and event_member_id=?2 and comment_message=?3 and comment_time=?4 order by comment_time")
//	List<Comments> findByOthers(String commentId , String eventMemberId , String commentMessage , java.sql.Date commentTime);
}
