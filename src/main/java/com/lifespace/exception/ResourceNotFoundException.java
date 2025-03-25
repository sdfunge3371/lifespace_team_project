package com.lifespace.exception;


// 自定例外 (幾乎都是放查無資料時產生的Exception(404 NOT FOUND))
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
