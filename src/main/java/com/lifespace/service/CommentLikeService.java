package com.lifespace.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.entity.CommentLike;
import com.lifespace.repository.CommentLikeRepository;

@Service("commentLikeService")
public class CommentLikeService {

	@Autowired
	CommentLikeRepository commentLikeRepository;
		
	public void addCommentLike(CommentLike commentLike) {
		commentLikeRepository.save(commentLike);
	}

	public void updateCommentLike(CommentLike commentLike) {
		commentLike.setLikeId(commentLike.getLikeId()); 
		commentLikeRepository.save(commentLike);
	}

	public void deleteCommentLike(Integer likeId) {
		if (commentLikeRepository.existsById(likeId))
			commentLikeRepository.deleteByLikeId(likeId);
//		    commentLikeRepository.deleteById(likeId);
	}

	public CommentLike getOneCommentLike(Integer likeId) {
		Optional<CommentLike> optional = commentLikeRepository.findById(likeId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<CommentLike> getAll() {
//		List<CommentLike> list = repository.findAll();
//		return list;
		return commentLikeRepository.findAll(); //上面兩行簡寫為此行。
	}
	
}
