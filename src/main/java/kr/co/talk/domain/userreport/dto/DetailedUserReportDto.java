package kr.co.talk.domain.userreport.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class DetailedUserReportDto {
    private LocalDate date;
    private int count;
    private Effect effect;
    private List<ReceivedEmotion> receivedEmotions;
    private List<String> remainedSentences;
    private List<Integer> scores;
    private List<ReceivedFeedback> feedbacks;

    @Builder
    @Data
    public static class Effect {
        private int energy;
        private int relation;
        private int stable;
        private int stress;
    }

    @Builder
    @Data
    public static class ReceivedEmotion {
        private int code;
        private int count;
    }

    @Builder
    @Data
    public static class ReceivedFeedback {
        private String nickname;
        private String profileImgUrl;
        private String content;
    }
}
