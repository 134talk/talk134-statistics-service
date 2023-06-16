package kr.co.talk.domain.statistics.messagequeue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.talk.domain.statistics.dto.FeedbackDto;
import kr.co.talk.domain.statistics.model.StatisticsEntity;
import kr.co.talk.domain.statistics.model.StatisticsEntity.Users;
import kr.co.talk.global.constants.KafkaConstants;
import kr.co.talk.global.constants.RedisConstants;
import kr.co.talk.global.service.redis.RedisService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticsConsumer {

    private final ObjectMapper objectMapper;
    
    private final DynamoDBMapper dynamoDBMapper;
    
    private final RedisService redisService;

    @KafkaListener(topics = KafkaConstants.TOPIC_END_CHATTING,
            groupId = KafkaConstants.GROUP_STATISTICS,
            containerFactory = "concurrentKafkaListenerContainerFactory")
    public void endChatting(String kafkaMessage, Acknowledgment ack)
            throws JsonMappingException, JsonProcessingException {
        log.info("Received Msg statistics server, message : {}", kafkaMessage);

        KafkaEndChatroomDTO endChatroomDTO =
                objectMapper.readValue(kafkaMessage, KafkaEndChatroomDTO.class);

        try {
            
            long roomId = endChatroomDTO.getRoomId();
            long userId = endChatroomDTO.getUserId();
            
            FeedbackDto feedbackDto = (FeedbackDto) redisService.getValueByMap(RedisConstants.FEEDBACK_ + roomId, String.valueOf(userId), FeedbackDto.class);
            
            StatisticsEntity statisticsEntity = new StatisticsEntity();
            statisticsEntity.setRoomId(roomId);
            statisticsEntity.setTeamCode("teamCode");
            statisticsEntity.setTime(endChatroomDTO.localDateTime);
            statisticsEntity.setUsers(feedbackDto);
            
            dynamoDBMapper.save(statisticsEntity);
            
            // kafka commit
            ack.acknowledge();
        } catch (Exception e) {
            log.error("acknowledge error : {}", e);
        }

    }

    @Builder
    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    private static class KafkaEndChatroomDTO {
        private long roomId;
        private long userId;
        private LocalDateTime localDateTime;
    }
}
