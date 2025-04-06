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

import com.lifespace.entity.CommentReport;
import com.lifespace.service.CommentReportService;

import jakarta.validation.Valid;

@RestController
public class CommentReportController {

	@Autowired
	CommentReportService commentReportService;
		
	@PostMapping("/commentreports")
	public String insert(@RequestBody CommentReport commentReport) {
		commentReportService.addCommentReport(commentReport);
		return "執行資料庫的 Create 操作";
	}
	
	@PutMapping("/commentreports/{reportId}")
	public String update(@PathVariable String reportId,
						 @RequestBody CommentReport commentReport) {
		commentReport.setReportId(reportId); //這樣就可以設定commentReport裡面的id的值
		commentReportService.updateCommentReport(commentReport);
		return "執行資料庫的 Update 操作";
	}
	
	@DeleteMapping("/commentreports/{reportId}")
	public String delete(@PathVariable String reportId) {
		commentReportService.deleteCommentReport(reportId);
		return "執行資料庫的 Delete 操作";
	}
	
	@GetMapping("/commentreports/{reportId}")
	public CommentReport read(@PathVariable String reportId) {
		CommentReport commentReport = commentReportService.getOneCommentReport(reportId);
		return commentReport;
	}
	
	@GetMapping("/commentreports")
	public List<CommentReport> read() {
		List <CommentReport> commentReport = commentReportService.getAll();
		return commentReport;
	}
	
}
