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

import com.lifespace.entity.CommentLike;
import com.lifespace.service.CommentLikeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class CommentLikeController {

	@Autowired
	CommentLikeService commentLikeService;
		
	@PostMapping("/commentlikes")
	public String insert(@RequestBody CommentLike commentLike) {
		commentLikeService.addCommentLike(commentLike);
		return "執行資料庫的 Create 操作";
	}
	
	@PutMapping("/commentlikes/{likeId}")
	public String update(@PathVariable Integer likeId,
						 @RequestBody CommentLike commentLike) {
		commentLike.setLikeId(likeId); //這樣就可以設定commentsVO裡面的id的值
		commentLikeService.updateCommentLike(commentLike);
		return "執行資料庫的 Update 操作";
	}
	
	@DeleteMapping("/commentlikes/{likeId}")
	public String delete(@PathVariable Integer likeId) {
		commentLikeService.deleteCommentLike(likeId);
		return "執行資料庫的 Delete 操作";
	}
	
	@GetMapping("/commentlikes/{likeId}")
	public CommentLike read(@PathVariable Integer likeId) {
		CommentLike commentLike = commentLikeService.getOneCommentLike(likeId);
		return commentLike;
	}
	
	@GetMapping("/commentlikes")
	public List<CommentLike> read() {
		List <CommentLike> commentLike = commentLikeService.getAll();
		return commentLike;
	}
	
}
