package com.chris.poker.dto;

public class ApiResponse<T> {
	private boolean success;
	private String message;
	private T data;
	private String errorCode;
	private long timestamp;
	
	
	public ApiResponse(boolean success, String message, T data, String errorCode) {
		super();
		this.success = success;
		this.message = message;
		this.data = data;
		this.errorCode = errorCode;
		this.timestamp = System.currentTimeMillis();
	}
	
	//成功純數據
	public static <T> ApiResponse<T> success(T data){
		return new ApiResponse<T>(true, "操作成功", data, null);
	}
	//成功含自定義訊息
	public static <T> ApiResponse<T> success(String message, T data){
		return new ApiResponse<T>(true, message, data, null);
	}
	//成功純訊息
	public static <T> ApiResponse<T> success(String message){
		return new ApiResponse<T>(true, message, null, null);
	}
	
	//失敗含錯誤碼
	public static <T> ApiResponse<T> error(String message,String errorCode){
		return new ApiResponse<T>(false, message, null, errorCode);
	}
	
	//失敗純訊息
	public static  <T> ApiResponse<T> error(String message){
		return new ApiResponse<T>(false, message, null, null);
	}
	
	
	
	public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public T getData() {
        return data;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
	
}
