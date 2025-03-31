package com.lifespace.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lifespace.entity.CommentReportVO;
import com.lifespace.repository.CommentReportRepository;

@Service("commentReportService")
public class CommentReportService {

	@Autowired
	CommentReportRepository commentReportRepository;
	
	public void addCommentReport(CommentReportVO commentReportVO) {
		commentReportRepository.save(commentReportVO);
	}

	public void updateCommentReport(CommentReportVO commentReportVO) {
		commentReportVO.setReportId(commentReportVO.getReportId()); 
		commentReportRepository.save(commentReportVO);
	}

	public void deleteCommentReport(String reportId) {
		if (commentReportRepository.existsById(reportId))
			commentReportRepository.deleteByReportId(reportId);
//		    commentReportRepository.deleteById(reportId);
	}

	public CommentReportVO getOneCommentReport(String reportId) {
		Optional<CommentReportVO> optional = commentReportRepository.findById(reportId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<CommentReportVO> getAll() {
//		List<CommentReportVO> list = commentReportRepository.findAll();
//		return list;
		return commentReportRepository.findAll(); //上面兩行簡寫為此行。
	}

}
