package kr.co.talk.domain.statistics.model;

import kr.co.talk.domain.statistics.dto.EmoticonCode;
import kr.co.talk.domain.statistics.dto.FeedbackDto;
import kr.co.talk.domain.statistics.dto.FeedbackDto.Feedback;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StatisticsEntity {
    private long roomId;

    private String teamCode;

    private LocalDateTime chatroomEndtime; // 대화방 종료 시간

    private List<Users> users = new ArrayList<>();

    private List<RoomEmoticon> roomEmoticons = new ArrayList<>();

    private KeywordSet keywordSet;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Users {
        private long userId;
        private String sentence;
        private int score;
        private int statusEnergy;
        private int statusRelation;
        private int statusStress;
        private int statusStable;

        private List<Feedback> feedback;
    }

    /**
     * 대화중 이모티콘을 날리면 redis에 저장하기위한 class
     */
    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RoomEmoticon {
        private EmoticonCode emoticonCode;
        private long toUserId;
        private long fromUserId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordSet {
        private List<Long> questionCode;
        private List<Long> keywordCode;
    }

    public void setUsers(FeedbackDto feedbackDto) {
        this.users.add(Users.builder()
                .userId(feedbackDto.getUserId())
                .score(feedbackDto.getScore())
                .sentence(feedbackDto.getSentence())
                .statusEnergy(feedbackDto.getStatusEnergy())
                .statusRelation(feedbackDto.getStatusRelation())
                .statusStable(feedbackDto.getStatusStable())
                .statusStress(feedbackDto.getStatusStress())
                .feedback(feedbackDto.getFeedback())
                .build());
    }

    public void setEmoticonsWithRedis(List<RoomEmoticon> emoticonList) {
        this.roomEmoticons.addAll(emoticonList);
    }

}
