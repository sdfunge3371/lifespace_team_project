package com.lifespace.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(HttpMessageNotReadableException.class)   // 400
	public ResponseEntity<String> handleFormatError(HttpMessageNotReadableException ex) {
	    String message = ex.getMostSpecificCause().getMessage();
	    // 需輸入數字的欄位如果寫其他型別時觸發
	    if (message.contains("spacePeople")) {
	        return ResponseEntity.badRequest().body("空間人數：請輸入數字");
	    } else if (message.contains("spaceSize")) {
	        return ResponseEntity.badRequest().body("空間大小：請輸入數字");
	    } else if (message.contains("spaceHourlyFee")) {
	    	return ResponseEntity.badRequest().body("時租費率：請輸入數字");
	    } else if (message.contains("spaceDailyFee")) {
	    	return ResponseEntity.badRequest().body("日租費率：請輸入數字");
	    } 
	    
	    // 其他沒寫到的
	    return ResponseEntity.badRequest().body("請確認輸入資料格式是否正確");
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)  // 400: 格式錯誤
	public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
	    String errorMsg = ex.getBindingResult().getFieldErrors().stream()
	        .map(e -> e.getDefaultMessage())
	        .collect(Collectors.joining(", "));
	    return ResponseEntity.badRequest().body(errorMsg);
	}
	
    @ExceptionHandler(ResourceNotFoundException.class)   // 404 (使用自訂例外)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
	
//	@ExceptionHandler(Exception.class)  // 500
//	public ResponseEntity<String> handleGenericException(Exception ex) {
//	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("伺服器錯誤，請稍後再試");
//	}

}
