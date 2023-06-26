package kr.co.talk.domain.userreport.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<ReceivedEmoticon> receivedEmoticons;
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
    public static class ReceivedEmoticon {
        private String name;
        private int count;
    }

    @Builder
    @Data
    public static class ReceivedFeedback {
        @JsonIgnore
        private long userId;

        private String nickname;
        private String profileImgUrl;
        private String content;
    }
}
