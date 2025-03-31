package com.lifespace.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.entity.CommentsVO;
import com.lifespace.repository.CommentsRepository;


@Service("commentsService")
public class CommentsService {

	@Autowired
	CommentsRepository commentsRepository;
	
	public void addComments(CommentsVO commentsVO) {
		commentsRepository.save(commentsVO);
	}

	public void updateComments(CommentsVO commentsVO) {
		commentsVO.setCommentId(commentsVO.getCommentId()); 
		commentsRepository.save(commentsVO);
	}

	public void deleteComments(String commentId) {
		if (commentsRepository.existsById(commentId))
			commentsRepository.deleteByCommentId(commentId);
//		    commentsRepository.deleteById(commentId);
	}

	public CommentsVO getOneComments(String commentId) {
		Optional<CommentsVO> optional = commentsRepository.findById(commentId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<CommentsVO> getAll() {
//		List<CommentsVO> list = commentsrepository.findAll();
//		return list;
		return commentsRepository.findAll(); //上面兩行簡寫為此行。
	}
	
}
