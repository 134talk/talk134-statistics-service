package kr.co.talk.global.converter;

import java.io.IOException;
import java.util.List;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.talk.domain.statistics.model.StatisticsEntity.RoomEmoticon;

public class EmoticonConverter<T extends RoomEmoticon>
        implements DynamoDBTypeConverter<String, List<RoomEmoticon>> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(List<RoomEmoticon> object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Unable to parse JSON");
    }

    @Override
    public List<RoomEmoticon> unconvert(String object) {
        try {
            List<RoomEmoticon> unconvertedObject = objectMapper.readValue(object,
                    new TypeReference<List<RoomEmoticon>>() {});
            return unconvertedObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
