package com.lifespace.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	
	// [用於取得留言所屬的活動資訊]
	// 根據活動 ID 查詢留言（回傳所有留言清單），主要拿第一筆留言去找出關聯活動
	List<Comments> findByEventMember_Event_EventId(String eventId);
	
	// [用於留言清單分頁顯示]
	// 根據活動 ID 查詢留言（支援分頁、排序），用於留言板主列表顯示
	Page<Comments> findByEventMember_Event_EventId(String eventId, Pageable pageable);



//	//● (自訂)條件查詢
//	@Query(value = "from Comments where comment_id=?1 and event_member_id=?2 and comment_message=?3 and comment_time=?4 order by comment_time")
//	List<Comments> findByOthers(String commentId , String eventMemberId , String commentMessage , java.sql.Date commentTime);
}
