package com.lifespace.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
	
	private final List<String> errors;
	
	public ValidationException(List<String> errors) {
		super("輸入資料驗證失敗");
		this.errors = errors;
	}
	
	
    public List<String> getErrors() {
        return errors;
    }
	
	

}
