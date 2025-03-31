package com.lifespace.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lifespace.entity.CommentReportVO;
import com.lifespace.service.CommentReportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/lifespace/commentlikes")
public class CommentReportController {

	@Autowired
	CommentReportService commentReportSvc;
		
	@PostMapping("/commentlikes")
	public String insert(@RequestBody CommentReportVO commentReportVO) {
		commentReportSvc.addCommentReport(commentReportVO);
		return "執行資料庫的 Create 操作";
	}
	
	@PutMapping("/commentlikes/{likeId}")
	public String update(@PathVariable String reportId,
						 @RequestBody CommentReportVO commentReportVO) {
		commentReportVO.setReportId(reportId); //這樣就可以設定commentReportVO裡面的id的值
		commentReportSvc.updateCommentReport(commentReportVO);
		return "執行資料庫的 Update 操作";
	}
	
	@DeleteMapping("/commentlikes/{likeId}")
	public String delete(@PathVariable String reportId) {
		commentReportSvc.deleteCommentReport(reportId);
		return "執行資料庫的 Delete 操作";
	}
	
	@GetMapping("/commentlikes/{likeId}")
	public CommentReportVO read(@PathVariable String reportId) {
		CommentReportVO commentReportVO = commentReportSvc.getOneCommentReport(reportId);
		return commentReportVO;
	}
	
	@GetMapping("/commentlikes")
	public List<CommentReportVO> read() {
		List <CommentReportVO> commentReportVO = commentReportSvc.getAll();
		return commentReportVO;
	}
	
}
