package com.lifespace.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.entity.Comments;
import com.lifespace.repository.CommentsRepository;


@Service("commentsService")
public class CommentsService {

	@Autowired
	CommentsRepository commentsRepository;
	
	public void addComments(Comments comments) {
		commentsRepository.save(comments);
	}

	public void updateComments(Comments comments) {
		comments.setCommentId(comments.getCommentId()); 
		commentsRepository.save(comments);
	}

	public void deleteComments(String commentId) {
		if (commentsRepository.existsById(commentId))
			commentsRepository.deleteByCommentId(commentId);
//		    commentsRepository.deleteById(commentId);
	}

	public Comments getOneComments(String commentId) {
		Optional<Comments> optional = commentsRepository.findById(commentId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<Comments> getAll() {
//		List<Comments> list = commentsrepository.findAll();
//		return list;
		return commentsRepository.findAll(); //上面兩行簡寫為此行。
	}
	
}
