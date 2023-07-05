package kr.co.talk.domain.statistics.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import kr.co.talk.domain.statistics.dto.EmoticonCode;
import kr.co.talk.domain.statistics.dto.FeedbackDto;
import kr.co.talk.domain.statistics.dto.FeedbackDto.Feedback;
import kr.co.talk.domain.statistics.dto.RequestDto.UserInfoDto;
import kr.co.talk.global.constants.AppConstants;
import kr.co.talk.global.converter.EmoticonConverter;
import kr.co.talk.global.converter.FeedbackConverter;
import kr.co.talk.global.converter.KeywordConverter;
import kr.co.talk.global.converter.LocalDateTimeConverter;
import kr.co.talk.global.converter.UsersConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@DynamoDBTable(tableName = AppConstants.TABLE_STATISTICS)
public class StatisticsEntity {
    @DynamoDBHashKey
    private long roomId;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "team-code-idx", attributeName = "teamCode")
    private String teamCode;


    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "chatroomEndtime")
    private LocalDateTime chatroomEndtime; // 대화방 종료 시간

    private List<Users> users = new ArrayList<>();

    private List<RoomEmoticon> roomEmoticons = new ArrayList<>();

    private KeywordSet keywordSet;

    @DynamoDBAttribute(attributeName = "chatroomUsers")
    @DynamoDBTypeConverted(converter = UsersConverter.class)
    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    @DynamoDBAttribute(attributeName = "emoticons")
    @DynamoDBTypeConverted(converter = EmoticonConverter.class)
    public List<RoomEmoticon> getRoomEmoticons() {
        return roomEmoticons;
    }

    public void setRoomEmoticons(List<RoomEmoticon> roomEmoticons) {
        this.roomEmoticons = roomEmoticons;
    }

    @DynamoDBAttribute(attributeName = "keywordSet")
    @DynamoDBTypeConverted(converter = KeywordConverter.class)
    public KeywordSet getKeywordSet() {
        return keywordSet;
    }

    public void setKeywordSet(KeywordSet keywordSet) {
        this.keywordSet = keywordSet;
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @DynamoDBDocument
    public static class Users {
        private long userId;
        private String sentence;
        private int score;
        private int statusEnergy;
        private int statusRelation;
        private int statusStress;
        private int statusStable;

        private String profileUrl;
        private String name;
        private String nickname;
        
        @DynamoDBAttribute
        @DynamoDBTypeConverted(converter = FeedbackConverter.class)
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



    public void setUsers(FeedbackDto feedbackDto, UserInfoDto userInfoDto) {
        this.users.add(Users.builder()
                .userId(userInfoDto.getUserId())
                .name(userInfoDto.getUserName())
                .nickname(userInfoDto.getNickname())
                .profileUrl(userInfoDto.getProfileUrl())
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
        emoticonList.forEach(emoticon -> this.roomEmoticons.add(emoticon));
    }

}
