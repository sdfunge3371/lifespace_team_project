package com.lifespace.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

import com.lifespace.entity.CommentsVO;
import com.lifespace.service.CommentsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class CommentsController {

	@Autowired
	CommentsService commentsSvc;
	
	@PostMapping("/comments")
	public String insert(@RequestBody CommentsVO commentsVO) {
		commentsSvc.addComments(commentsVO);
		return "執行資料庫的 Create 操作";
	}
	
	@PutMapping("/comments/{commentId}")
	public String update(@PathVariable String commentId,
						 @RequestBody CommentsVO commentsVO) {
		commentsVO.setCommentId(commentId); //這樣就可以設定commentsVO裡面的id的值
		commentsSvc.updateComments(commentsVO);
		return "執行資料庫的 Update 操作";
	}
	
	@DeleteMapping("/comments/{commentId}")
	public String delete(@PathVariable String commentId) {
		commentsSvc.deleteComments(commentId);
		return "執行資料庫的 Delete 操作";
	}
	
	@GetMapping("/comments/{commentId}")
	public CommentsVO read(@PathVariable String commentId) {
		CommentsVO commentsVO = commentsSvc.getOneComments(commentId);
		return commentsVO;
	}
	
	@GetMapping("/comments")
	public List<CommentsVO> read() {
		List<CommentsVO> commentsVO = commentsSvc.getAll();
		return commentsVO;
	}
	
}
