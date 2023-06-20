package kr.co.talk.global.converter;

import java.io.IOException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.talk.domain.statistics.model.StatisticsEntity.KeywordSet;

public class KeywordConverter<T extends KeywordSet>
        implements DynamoDBTypeConverter<String, KeywordSet> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(KeywordSet object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Unable to parse JSON");
    }

    @Override
    public KeywordSet unconvert(String object) {
        try {
            KeywordSet unconvertedObject = objectMapper.readValue(object,
                    new TypeReference<KeywordSet>() {});
            return unconvertedObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
