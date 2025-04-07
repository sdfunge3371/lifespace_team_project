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

import com.lifespace.dto.OrdersDTO;
import com.lifespace.entity.Comments;
import com.lifespace.service.CommentsService;
import com.lifespace.service.OrdersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class CommentsController {

	@Autowired
	CommentsService commentsService;
	
	@Autowired
    private OrdersService ordersSvc;
	
	@PostMapping("/comments")
	public String insert(@RequestBody Comments comments) {
		commentsService.addComments(comments);
		return "執行資料庫的 Create 操作";
	}
	
	@PutMapping("/comments/{commentId}")
	public String update(@PathVariable String commentId,
						 @RequestBody Comments comments) {
		comments.setCommentId(commentId); //這樣就可以設定commentsVO裡面的id的值
		commentsService.updateComments(comments);
		return "執行資料庫的 Update 操作";
	}
	
	@DeleteMapping("/comments/{commentId}")
	public String delete(@PathVariable String commentId) {
		commentsService.deleteComments(commentId);
		return "執行資料庫的 Delete 操作";
	}
	
	@GetMapping("/comments/{commentId}")
	public Comments read(@PathVariable String commentId) {
		Comments comments = commentsService.getOneComments(commentId);
		return comments;
	}
	
	@GetMapping("/comments")
	public List<Comments> read() {
		List<Comments> comments = commentsService.getAll();
		return comments;
	}
	
	@GetMapping("/comments/getAll")
    public List<OrdersDTO> getAllOrders() {

        return ordersSvc.getAllOrdersDTOs();
    }
	
	
}
