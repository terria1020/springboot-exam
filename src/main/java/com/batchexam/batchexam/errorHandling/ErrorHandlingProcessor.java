package com.batchexam.batchexam.errorHandling;

import org.springframework.batch.item.ItemProcessor;

import com.batchexam.batchexam.dto.DataModelDto;

public class ErrorHandlingProcessor implements ItemProcessor<DataModelDto, DataModelDto> {

    @Override
    public DataModelDto process(DataModelDto item) throws Exception {
        // 데이터 검증
        validateData(item);

        // 데이터 변환 (예: 이름을 대문자로)
        DataModelDto processedItem = new DataModelDto(
                item.firstName() != null ? item.firstName().toUpperCase() : null,
                item.lastName() != null ? item.lastName().toUpperCase() : null,
                item.email());

        System.out.println("✅ 처리 완료: " + processedItem);
        return processedItem;
    }

    private void validateData(DataModelDto item) throws IllegalArgumentException {
        // 이름 검증
        if (item.firstName() == null || item.firstName().trim().isEmpty()) {
            throw new IllegalArgumentException("firstName이 비어있습니다: " + item);
        }

        if (item.lastName() == null || item.lastName().trim().isEmpty()) {
            throw new IllegalArgumentException("lastName이 비어있습니다: " + item);
        }

        // 이메일 검증
        if (item.email() == null || !item.email().contains("@")) {
            throw new IllegalArgumentException("유효하지 않은 이메일: " + item.email());
        }

        // 의도적으로 일부 데이터에서 사용자 정의 Exception 발생 (재시도 테스트)
        if (item.firstName() != null && item.firstName().equals("TestRetry")) {
            throw new CustomRetryableException("일시적 오류 발생 - 재시도 필요");
        }
    }
}