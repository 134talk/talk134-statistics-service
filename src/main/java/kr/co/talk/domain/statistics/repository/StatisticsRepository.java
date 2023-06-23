package kr.co.talk.domain.statistics.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import kr.co.talk.domain.statistics.model.StatisticsEntity;
import kr.co.talk.domain.statistics.model.StatisticsEntity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StatisticsRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    
    /**
     * teamCode에 해당된 StatisticsList 조회
     * 
     * @param teamCode
     * @return
     */
    public List<StatisticsEntity> getStatisticsListByTeamCode(String teamCode){
        List<StatisticsEntity> statisticsEntityList = new ArrayList<>();
        
        Map<String, AttributeValue> params = new HashMap<>();
        params.put(":v1", new AttributeValue().withS(teamCode));


        DynamoDBQueryExpression<StatisticsEntity> dbQueryExpression =
                new DynamoDBQueryExpression<StatisticsEntity>()
                        .withConsistentRead(false)
                        .withIndexName("team-code-idx")
                        .withKeyConditionExpression("teamCode = :v1")
                        .withExpressionAttributeValues(params);

        List<StatisticsEntity> queryByGsi =
                dynamoDBMapper.query(StatisticsEntity.class, dbQueryExpression);

        List<Long> roomIds =
                queryByGsi.stream().map(StatisticsEntity::getRoomId).collect(Collectors.toList());

        roomIds.forEach(
                rid -> statisticsEntityList.add(dynamoDBMapper.load(StatisticsEntity.class, rid)));
        
        return statisticsEntityList;
    }
    
    public List<Users> getUsers(String teamCode){
        List<Users> userList = new ArrayList<>();

        List<StatisticsEntity> statisticsListByTeamCode = getStatisticsListByTeamCode(teamCode);
        
        for(StatisticsEntity sEntity : statisticsListByTeamCode) {
            List<Users> users = sEntity.getUsers();
            userList.addAll(users);
        }
        
        return userList;
    }
}
