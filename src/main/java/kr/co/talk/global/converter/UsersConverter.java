package kr.co.talk.global.converter;

import java.io.IOException;
import java.util.List;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.talk.domain.statistics.model.StatisticsEntity.Users;

public class UsersConverter<T extends Users>
        implements DynamoDBTypeConverter<String, List<Users>> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(List<Users> object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Unable to parse JSON");
    }

    @Override
    public List<Users> unconvert(String object) {
        try {
            List<Users> unconvertedObject = objectMapper.readValue(object,
                    new TypeReference<List<Users>>() {});
            return unconvertedObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
