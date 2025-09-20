package com.chris.poker.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.chris.poker.dto.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingPathVariableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {
	 private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	    
	    // 處理業務邏輯異常
	    @ExceptionHandler(IllegalArgumentException.class)
	    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
	        logger.warn("參數錯誤: {}", e.getMessage());
	        return ResponseEntity.badRequest()
	            .body(ApiResponse.error(e.getMessage(), "INVALID_ARGUMENT"));
	    }
	    
	    // 處理遊戲狀態異常
	    @ExceptionHandler(IllegalStateException.class)
	    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(IllegalStateException e) {
	        logger.warn("狀態錯誤: {}", e.getMessage());
	        return ResponseEntity.badRequest()
	            .body(ApiResponse.error(e.getMessage(), "INVALID_STATE"));
	    }
	    
	    // 處理空指針異常
	    @ExceptionHandler(NullPointerException.class)
	    public ResponseEntity<ApiResponse<Object>> handleNullPointerException(NullPointerException e) {
	        logger.error("空指針異常", e);
	        return ResponseEntity.status(500)
	            .body(ApiResponse.error("系統內部錯誤", "NULL_POINTER"));
	    }
	    
	    // 處理類型轉換異常（Map取值時常見）
	    @ExceptionHandler(ClassCastException.class)
	    public ResponseEntity<ApiResponse<Object>> handleClassCastException(ClassCastException e) {
	        logger.warn("數據類型錯誤: {}", e.getMessage());
	        return ResponseEntity.badRequest()
	            .body(ApiResponse.error("請求數據格式錯誤", "DATA_FORMAT_ERROR"));
	    }
	    
	    // 處理數字格式異常
	    @ExceptionHandler(NumberFormatException.class)
	    public ResponseEntity<ApiResponse<Object>> handleNumberFormatException(NumberFormatException e) {
	        logger.warn("數字格式錯誤: {}", e.getMessage());
	        return ResponseEntity.badRequest()
	            .body(ApiResponse.error("數字格式錯誤", "NUMBER_FORMAT_ERROR"));
	    }
	    
	    // 處理JSON解析異常
	    @ExceptionHandler(HttpMessageNotReadableException.class)
	    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
	        logger.warn("JSON解析錯誤: {}", e.getMessage());
	        return ResponseEntity.badRequest()
	            .body(ApiResponse.error("請求格式錯誤", "JSON_PARSE_ERROR"));
	    }
	    
	    // 處理請求方法不支持異常
	    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	    public ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
	        logger.warn("請求方法不支持: {}", e.getMessage());
	        return ResponseEntity.status(405)
	            .body(ApiResponse.error("請求方法不支持: " + e.getMethod(), "METHOD_NOT_SUPPORTED"));
	    }
	    
	    // 處理路徑參數缺失異常
	    @ExceptionHandler(MissingPathVariableException.class)
	    public ResponseEntity<ApiResponse<Object>> handleMissingPathVariableException(MissingPathVariableException e) {
	        logger.warn("路徑參數缺失: {}", e.getMessage());
	        return ResponseEntity.badRequest()
	            .body(ApiResponse.error("缺少必要的路徑參數: " + e.getVariableName(), "MISSING_PATH_VARIABLE"));
	    }
	    
	    // 處理所有其他未捕獲的異常
	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception e) {
	        logger.error("未處理的異常", e);
	        return ResponseEntity.status(500)
	            .body(ApiResponse.error("系統發生未知錯誤", "SYSTEM_ERROR"));
	    }
	    
	   
}
