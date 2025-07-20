package com.batchexam.batchexam.errorHandling;

/**
 * 재시도가 필요한 일시적 오류를 나타내는 사용자 정의 예외
 */
public class CustomRetryableException extends RuntimeException {
    
    public CustomRetryableException(String message) {
        super(message);
    }
    
    public CustomRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}