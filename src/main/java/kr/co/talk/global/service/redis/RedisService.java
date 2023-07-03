package kr.co.talk.global.service.redis;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import kr.co.talk.domain.statistics.model.StatisticsEntity.KeywordSet;
import kr.co.talk.domain.statistics.model.StatisticsEntity.RoomEmoticon;
import kr.co.talk.global.constants.RedisConstants;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private ValueOperations<String, String> valueOps;
    private ListOperations<String, String> opsForList;
    private HashOperations<String, String, String> opsForMap;

    @PostConstruct
    public void init() {
        valueOps = stringRedisTemplate.opsForValue();
        opsForList = redisTemplate.opsForList();
        opsForMap = redisTemplate.opsForHash();
    }

    /**
     * get value
     *
     * @param key
     * @return
     */
    public String getValues(String key) {
        return valueOps.get(key);
    }

    /**
     * set value with timeout
     *
     * @param key
     * @param value
     * @param timeout
     */
    public void setValuesWithTimeout(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, Duration.ofMillis(timeout));
    }

    public void pushList(String key, Object value) {
        try {
            String item = objectMapper.writeValueAsString(value);
            opsForList.leftPush(key, item);
        } catch (JsonProcessingException e) {
            log.error("json parse exception , key is :: {}, value is :: {}", key, value, e);
            throw new RuntimeException(e);
        }
    }

    public List<String> getList(String key) {
        return opsForList.size(key) == 0 ? new ArrayList<>() : opsForList.range(key, 0, -1);
    }

    public List<RoomEmoticon> getEmoticonList(long chatroomId) {
        String key = chatroomId + RedisConstants._ROOM_EMOTICON;
        List<String> emoticonList =
                opsForList.size(key) == 0 ? new ArrayList<>() : opsForList.range(key, 0, -1);

        return emoticonList.stream().map(s -> {
            try {
                return objectMapper.readValue(s, RoomEmoticon.class);
            } catch (JsonProcessingException e) {
                log.error("json parse error", e);
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

    }

    public KeywordSet getKeywordSet(long chatroomId, long userId) {
        String key = chatroomId + "_" + userId + RedisConstants._QUESTION;

        try {
            return objectMapper.readValue(valueOps.get(key), KeywordSet.class);
        } catch (JsonProcessingException e) {
            log.error("json parse error", e);
            throw new RuntimeException(e);
        }
    }

    public void pushMap(String key, String fieldKey, Object value) {
        try {
            String writeValueAsString = objectMapper.writeValueAsString(value);
            opsForMap.put(key, fieldKey, writeValueAsString);
        } catch (JsonProcessingException e) {
            log.error("json parse error", e);
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getEntry(String key, Class<?> clazz) {
        Map<String, String> entries = opsForMap.entries(key);
        return entries.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(),
                e -> {
                    try {
                        return objectMapper.readValue(e.getValue(), clazz);
                    } catch (JsonProcessingException e1) {
                        log.error("json parse error", e);
                    }
                    return null;
                }));
    }

    public Object getValueByMap(String key, String fieldKey, Class<?> clazz) {
        String writeValueAsString = opsForMap.get(key, fieldKey);
        try {
            return objectMapper.readValue(writeValueAsString, clazz);
        } catch (JsonProcessingException e) {
            log.error("json parse error", e);
            throw new RuntimeException(e);
        }
    }

    public void deleteMap(String key, String fieldKey) {
        opsForMap.delete(key, fieldKey);
    }

    
    public long decreaseValue(String roomId) {
    	return valueOps.decrement(roomId + RedisConstants.COUNT);
    }
    
}
