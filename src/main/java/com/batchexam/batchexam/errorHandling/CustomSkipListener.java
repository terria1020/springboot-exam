package com.batchexam.batchexam.errorHandling;

import org.springframework.batch.core.SkipListener;

public class CustomSkipListener implements SkipListener<Object, Object> {

    /**
     * Reader에서 Skip된 경우
     */
    @Override
    public void onSkipInRead(Throwable t) {
        System.err.println("❌ [READ SKIP] 읽기 중 오류 발생: " + t.getMessage());
        System.err.println("    예외 타입: " + t.getClass().getSimpleName());
    }

    /**
     * Processor에서 Skip된 경우
     */
    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        System.err.println("❌ [PROCESS SKIP] 처리 중 오류 발생");
        System.err.println("    아이템: " + item);
        System.err.println("    오류: " + t.getMessage());
        System.err.println("    예외 타입: " + t.getClass().getSimpleName());
    }

    /**
     * Writer에서 Skip된 경우
     */
    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        System.err.println("❌ [WRITE SKIP] 쓰기 중 오류 발생");
        System.err.println("    아이템: " + item);
        System.err.println("    오류: " + t.getMessage());
        System.err.println("    예외 타입: " + t.getClass().getSimpleName());
    }
}