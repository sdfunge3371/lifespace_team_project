package com.lifespace.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.entity.CommentLikeVO;
import com.lifespace.repository.CommentLikeRepository;

@Service("commentLikeService")
public class CommentLikeService {

	@Autowired
	CommentLikeRepository commentLikeRepository;
		
	public void addCommentLike(CommentLikeVO commentLikeVO) {
		commentLikeRepository.save(commentLikeVO);
	}

	public void updateCommentLike(CommentLikeVO commentLikeVO) {
		commentLikeVO.setLikeId(commentLikeVO.getLikeId()); 
		commentLikeRepository.save(commentLikeVO);
	}

	public void deleteCommentLike(Integer likeId) {
		if (commentLikeRepository.existsById(likeId))
			commentLikeRepository.deleteByLikeId(likeId);
//		    commentLikeRepository.deleteById(likeId);
	}

	public CommentLikeVO getOneCommentLike(Integer likeId) {
		Optional<CommentLikeVO> optional = commentLikeRepository.findById(likeId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<CommentLikeVO> getAll() {
//		List<CommentLikeVO> list = repository.findAll();
//		return list;
		return commentLikeRepository.findAll(); //上面兩行簡寫為此行。
	}
	
}
