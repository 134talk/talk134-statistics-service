package kr.co.talk.global.converter;

import java.io.IOException;
import java.util.List;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.talk.domain.statistics.dto.FeedbackDto.Feedback;

public class FeedbackConverter<T extends Feedback>
        implements DynamoDBTypeConverter<String, List<Feedback>> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(List<Feedback> object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Unable to parse JSON");
    }

    @Override
    public List<Feedback> unconvert(String object) {
        try {
            List<Feedback> unconvertedObject = objectMapper.readValue(object,
                    new TypeReference<List<Feedback>>() {});
            return unconvertedObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
