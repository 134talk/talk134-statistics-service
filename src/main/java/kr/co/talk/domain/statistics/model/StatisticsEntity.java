package kr.co.talk.domain.statistics.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import kr.co.talk.domain.statistics.dto.FeedbackDto;
import kr.co.talk.domain.statistics.dto.FeedbackDto.Feedback;
import kr.co.talk.domain.statistics.model.StatisticsEntity.Users;
import kr.co.talk.global.constants.AppConstants;
import kr.co.talk.global.converter.FeedbackConverter;
import kr.co.talk.global.converter.LocalDateTimeConverter;
import kr.co.talk.global.converter.UsersConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@DynamoDBTable(tableName = AppConstants.TABLE_STATISTICS)
public class StatisticsEntity {
    @DynamoDBHashKey
    private long roomId;

    private String teamCode;


    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute
    private LocalDateTime time; // 대화방 종료 시간


    private List<Users> users = new ArrayList<>();

    @DynamoDBAttribute(attributeName = "users")
    @DynamoDBTypeConverted(converter = UsersConverter.class)
    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
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
        private int statusEnergyBefore;
        private int statusRelationBefore;
        private int statusStressBefore;
        private int statusStableBefore;

        @DynamoDBAttribute
        @DynamoDBTypeConverted(converter = FeedbackConverter.class)
        private List<Feedback> feedback;
    }

    public void setUsers(FeedbackDto feedbackDto) {
        this.users.add(Users.builder()
                .userId(feedbackDto.getUserId())
                .score(feedbackDto.getScore())
                .sentence(feedbackDto.getSentence())
                .statusEnergy(feedbackDto.getStatusEnergy())
                .statusEnergyBefore(feedbackDto.getStatusEnergyBefore())
                .statusRelation(feedbackDto.getStatusRelation())
                .statusRelationBefore(feedbackDto.getStatusRelationBefore())
                .statusStable(feedbackDto.getStatusStable())
                .statusStableBefore(feedbackDto.getStatusStableBefore())
                .statusStress(feedbackDto.getStatusStress())
                .statusStressBefore(feedbackDto.getStatusStressBefore())
                .feedback(feedbackDto.getFeedback())
                .build());
    }

}
