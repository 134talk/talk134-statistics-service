package kr.co.talk.domain.statistics.messagequeue;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.talk.domain.statistics.dto.FeedbackDto;
import kr.co.talk.domain.statistics.model.StatisticsEntity;
import kr.co.talk.domain.statistics.model.StatisticsEntity.KeywordSet;
import kr.co.talk.domain.statistics.model.StatisticsEntity.RoomEmoticon;
import kr.co.talk.domain.statistics.model.StatisticsEntity.Users;
import kr.co.talk.global.client.UserClient;
import kr.co.talk.global.constants.KafkaConstants;
import kr.co.talk.global.constants.RedisConstants;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
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

	private final UserClient userClient;
	
	private final KafkaTemplate<String, String> kafkaTemplate;

	@KafkaListener(topics = KafkaConstants.TOPIC_END_CHATTING, groupId = "${spring.kafka.group}", containerFactory = "concurrentKafkaListenerContainerFactory")
	public void endChatting(String kafkaMessage, Acknowledgment ack) throws JsonProcessingException {
		log.info("Received Msg statistics server, message : {}", kafkaMessage);

		KafkaEndChatroomDTO endChatroomDTO = objectMapper.readValue(kafkaMessage, KafkaEndChatroomDTO.class);

		try {

			long roomId = endChatroomDTO.getRoomId();
			long userId = endChatroomDTO.getUserId();

			FeedbackDto feedbackDto = (FeedbackDto) redisService.getValueByMap(RedisConstants.FEEDBACK_ + roomId,
					String.valueOf(userId), FeedbackDto.class);

			StatisticsEntity loadEntity = dynamoDBMapper.load(StatisticsEntity.class, 48);

			if (loadEntity == null) {
				List<RoomEmoticon> emoticonList = redisService.getEmoticonList(roomId);
				KeywordSet keywordSet = redisService.getKeywordSet(roomId, userId);

				String teamCode = userClient.findTeamCode(userId).getTeamCode();

				StatisticsEntity statisticsEntity = new StatisticsEntity();
				statisticsEntity.setRoomId(roomId);
				statisticsEntity.setTeamCode(teamCode);
				statisticsEntity.setChatroomEndtime(endChatroomDTO.localDateTime);
				statisticsEntity.setUsers(feedbackDto);
				statisticsEntity.setEmoticonsWithRedis(emoticonList);
				statisticsEntity.setKeywordSet(keywordSet);

				dynamoDBMapper.save(statisticsEntity);
			} else {
				// 해당 user가 이미 dynamodb로 save되었는지 확인
				boolean alreadyCommitted = loadEntity.getUsers().stream().map(Users::getUserId)
						.anyMatch(id -> id == feedbackDto.getUserId());

				if (!alreadyCommitted) {
					loadEntity.setUsers(feedbackDto);
					dynamoDBMapper.save(loadEntity);
				}
			}

			long decrement = redisService.decreaseValue(String.valueOf(roomId));
			
			if(decrement == 0) {
				// 채팅방에 있는 유저들이 모두 feedback을 했는지 check하기 위해
				log.info("chat remove event publishing, chatroomId is :: {}" , roomId);
				 ListenableFuture<SendResult<String, String>> future =
			                kafkaTemplate.send(KafkaConstants.TOPIC_REMOVE_CHATTING,
			                		String.valueOf(roomId));
			        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

			            @Override
			            public void onSuccess(SendResult<String, String> result) {
			                RecordMetadata recordMetadata = result.getRecordMetadata();
			                log.info("sent topic=[{}] roodId [{}] with offset=[{}]", recordMetadata.topic(),
			                        roomId,
			                        recordMetadata.offset());
			            }

			            @Override
			            public void onFailure(Throwable ex) {
			                log.error("unable to send message roomId=[{}] due to : {}", roomId,
			                        ex.getMessage());
			                throw new CustomException(CustomError.KAFKA_ERROR);
			            }
			        });
			}

			// kafka commit
			ack.acknowledge();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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
