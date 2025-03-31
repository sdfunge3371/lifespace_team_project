package com.lifespace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.lifespace.entity.CommentLikeVO;


public interface CommentLikeRepository extends JpaRepository<CommentLikeVO, Integer>{

	@Transactional
	@Modifying
	@Query(value = "delete from comment_like where like_id =?1", nativeQuery = true)
	void deleteByLikeId(Integer likeId);

//	//● (自訂)條件查詢
//	@Query(value = "from CommentLikeVO where like_id=?1 and member_id ?2 and comment_id=?3 and created_time=?4 order by created_time")
//	List<CommentLikeVO> findByOthers(int likeId , String memberId , String commentId, java.sql.Date createdTime);
}
