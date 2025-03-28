package com.lifespace.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(HttpMessageNotReadableException.class)   // 400
	public ResponseEntity<Map<String, String>> handleFormatError(HttpMessageNotReadableException ex) {
		String message = ex.getMostSpecificCause().getMessage();
		Map<String, String> errorBody = new HashMap<>();

		if (message.contains("spacePeople")) {
			errorBody.put("message", "空間人數：請輸入數字");
		} else if (message.contains("spaceSize")) {
			errorBody.put("message", "空間大小：請輸入數字");
		} else if (message.contains("spaceHourlyFee")) {
			errorBody.put("message", "時租費率：請輸入數字");
		} else if (message.contains("spaceDailyFee")) {
			errorBody.put("message", "日租費率：請輸入數字");
//		} else if {		// 後面還可以再加
//		}
		} else {
			// 前面沒寫到的
			errorBody.put("message", "請確認輸入資料格式是否正確");
		}

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON)
				.body(errorBody);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)  // 400: 格式錯誤
	public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
		List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> e.getDefaultMessage())
				.collect(Collectors.toList());

		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put("message", String.join(", ", errorMessages));
		errorBody.put("errors", errorMessages);

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON)
				.body(errorBody);
	}
	
    @ExceptionHandler(ResourceNotFoundException.class)   // 404 (使用自訂例外)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
		Map<String, String> errorBody = new HashMap<>();
		errorBody.put("message", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
    }
}
