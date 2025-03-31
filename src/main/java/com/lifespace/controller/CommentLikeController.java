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

import com.lifespace.entity.CommentLikeVO;
import com.lifespace.service.CommentLikeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/lifespace/commentlike")
public class CommentLikeController {

	@Autowired
	CommentLikeService commentLikeSvc;
		
	@PostMapping("/commentlikes")
	public String insert(@RequestBody CommentLikeVO commentLikeVO) {
		commentLikeSvc.addCommentLike(commentLikeVO);
		return "執行資料庫的 Create 操作";
	}
	
	@PutMapping("/commentlikes/{likeId}")
	public String update(@PathVariable Integer likeId,
						 @RequestBody CommentLikeVO commentLikeVO) {
		commentLikeVO.setLikeId(likeId); //這樣就可以設定commentsVO裡面的id的值
		commentLikeSvc.updateCommentLike(commentLikeVO);
		return "執行資料庫的 Update 操作";
	}
	
	@DeleteMapping("/commentlikes/{likeId}")
	public String delete(@PathVariable Integer likeId) {
		commentLikeSvc.deleteCommentLike(likeId);
		return "執行資料庫的 Delete 操作";
	}
	
	@GetMapping("/commentlikes/{likeId}")
	public CommentLikeVO read(@PathVariable Integer likeId) {
		CommentLikeVO commentLikeVO = commentLikeSvc.getOneCommentLike(likeId);
		return commentLikeVO;
	}
	
	@GetMapping("/commentlikes")
	public List<CommentLikeVO> read() {
		List <CommentLikeVO> commentLikeVO = commentLikeSvc.getAll();
		return commentLikeVO;
	}
	
}
