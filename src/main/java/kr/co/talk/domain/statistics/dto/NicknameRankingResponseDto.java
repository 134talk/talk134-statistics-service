package kr.co.talk.domain.statistics.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NicknameRankingResponseDto {
    private List<Type1> type1;
    private List<Type2> type2;
    private List<Type3> type3;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Type1 {
        private String emotion;
        private int emotionCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Type2 {
        private String act;
        private int actCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Type3 {
        private String status;
        private int statusCount;
    }
}
