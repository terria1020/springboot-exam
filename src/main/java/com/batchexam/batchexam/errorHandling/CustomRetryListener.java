package com.batchexam.batchexam.errorHandling;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

public class CustomRetryListener implements RetryListener {

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        // Retry 시작 시 호출
        System.out.println("🔄 [RETRY START] 재시도 시작");
        return true; // true를 반환해야 retry 진행
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
            Throwable throwable) {
        // 각 재시도 실패 시 호출
        int retryCount = context.getRetryCount();
        System.err.printf("🔄 [RETRY %d] 재시도 실패: %s%n", retryCount, throwable.getMessage());
        System.err.printf("    예외 타입: %s%n", throwable.getClass().getSimpleName());
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
            Throwable throwable) {
        // Retry 종료 시 호출 (성공/실패 모두)
        int retryCount = context.getRetryCount();

        if (throwable == null) {
            System.out.println("✅ [RETRY SUCCESS] 재시도 성공! 총 " + retryCount + "번 시도");
        } else {
            System.err.println("❌ [RETRY EXHAUSTED] 모든 재시도 실패! 총 " + retryCount + "번 시도");
            System.err.println("    최종 오류: " + throwable.getMessage());
        }
    }
}