package ru.yandex.practicum.commerce.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortedContentResponseDto<T> {
    private List<T> content;
    private List<SortInfo> sort;
    
    public static <T> SortedContentResponseDto<T> of(List<T> content, List<SortInfo> sort) {
        return new SortedContentResponseDto<>(content, sort);
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SortInfo {
        private String property;
        private String direction;
    }
}