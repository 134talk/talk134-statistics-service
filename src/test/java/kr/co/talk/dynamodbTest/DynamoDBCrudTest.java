package kr.co.talk.dynamodbTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.talk.domain.statistics.dto.EmoticonCode;
import kr.co.talk.domain.statistics.model.StatisticsEntity;
import kr.co.talk.domain.statistics.model.StatisticsEntity.KeywordSet;
import kr.co.talk.domain.statistics.model.StatisticsEntity.RoomEmoticon;
import kr.co.talk.domain.statistics.model.StatisticsEntity.Users;
import kr.co.talk.domain.statistics.repository.StatisticsRepository;

@SpringBootTest
public class DynamoDBCrudTest {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    public void setup() throws Exception {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest =
                dynamoDBMapper.generateCreateTableRequest(StatisticsEntity.class);
        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(5L, 5L);
        tableRequest.setProvisionedThroughput(provisionedThroughput);
        tableRequest.getGlobalSecondaryIndexes()
                .forEach(v -> v.setProvisionedThroughput(provisionedThroughput));

        boolean created = TableUtils.createTableIfNotExists(amazonDynamoDB, tableRequest);

        if (created) {
            System.out.println("테이블 생성됨");
        } else {
            System.out.println("이미 테이블 있음");
        }
    }

    @Test
    @DisplayName("statistics save test")
    public void saveTest() {
        List<Users> users = new ArrayList<>();

        users.add(Users.builder().userId(82).score(5).sentence("test sentence").build());

        users.add(Users.builder().userId(78).score(8).sentence("test sentence2").build());

        StatisticsEntity entity = StatisticsEntity.builder().roomId(48).teamCode("uaCGJl")
                .chatroomEndtime(LocalDateTime.now())
                .users(users).build();

        dynamoDBMapper.save(entity);
    }

    @Test
    @DisplayName("statistics find test")
    public void findTest() {
        StatisticsEntity load = dynamoDBMapper.load(StatisticsEntity.class, 48);
        System.out.println("load::" + load);
    }

    @Test
    @DisplayName("팀 코드만 가지고 chatroom 전체 조회")
    public void findAllWithTeamCode() {
        Map<String, AttributeValue> params = new HashMap<>();
        params.put(":v1", new AttributeValue().withS("teamCode"));


        DynamoDBQueryExpression<StatisticsEntity> dbQueryExpression =
                new DynamoDBQueryExpression<StatisticsEntity>()
                        .withConsistentRead(false)
                        .withIndexName("team-code-idx")
                        .withKeyConditionExpression("teamCode = :v1")
                        .withExpressionAttributeValues(params);

        // DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        //
        // Table table = dynamoDB.getTable("STATISTICS");
        //
        // Index teamCodeIdx = table.getIndex("team-code-idx");
        // ItemCollection<QueryOutcome> items = teamCodeIdx.query(new QuerySpec()
        // .withKeyConditionExpression("teamCode = :v1")
        // .withValueMap(new ValueMap().withString(":v1", "uaCGJl"))

        //
        // Iterator<Item> iter = items.iterator();
        //
        // while(iter.hasNext()) {
        // Item next = iter.next();
        // System.out.println(next.toJSONPretty());
        // }


        List<StatisticsEntity> query =
                dynamoDBMapper.query(StatisticsEntity.class, dbQueryExpression);

        // query를 보게 되면 pk값만 가져오게 된다
        System.out.println("query:::" + query);



    }

    // scan - full scan
    // query - key 를 기반으로 scan
    @Test
    @DisplayName("statistics find with query")
    public void queryTest() {
        Map<String, AttributeValue> params = new HashMap<>();
        params.put("roomId", new AttributeValue().withS("48"));

        StatisticsEntity statisticsEntity = new StatisticsEntity();
        statisticsEntity.setRoomId(48);

        DynamoDBQueryExpression<StatisticsEntity> dbQueryExpression =
                new DynamoDBQueryExpression<StatisticsEntity>()
                        .withHashKeyValues(statisticsEntity);

        List<StatisticsEntity> query =
                dynamoDBMapper.query(StatisticsEntity.class, dbQueryExpression);

        System.out.println(query);
    }

    @Test
    @DisplayName("리포트 상세 조회")
    public void reportDetailTest() {
        List<StatisticsEntity> statisticsEntityList = new ArrayList<>();
        String teamCode = "uaCGJl";

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

        System.out.println("statisticsEntityList:::" + statisticsEntityList);

    }


    @Test
    @DisplayName("대화기록 리포트 상세 조회")
    void chatLogDetailTest() {
        String teamCode = "uaCGJl";
        List<KeywordSet> keywordSet = new ArrayList<>();
        List<RoomEmoticon> roomEmoticon = new ArrayList<>();

        roomEmoticon.add(RoomEmoticon.builder().emoticonCode(EmoticonCode.EMOTICON_TP1).build());
        roomEmoticon.add(RoomEmoticon.builder().emoticonCode(EmoticonCode.EMOTICON_TP1).build());
        roomEmoticon.add(RoomEmoticon.builder().emoticonCode(EmoticonCode.EMOTICON_TP2).build());
        roomEmoticon.add(RoomEmoticon.builder().emoticonCode(EmoticonCode.EMOTICON_TP2).build());
        roomEmoticon.add(RoomEmoticon.builder().emoticonCode(EmoticonCode.EMOTICON_TP4).build());
        roomEmoticon.add(RoomEmoticon.builder().emoticonCode(EmoticonCode.EMOTICON_TP5).build());
        roomEmoticon.add(RoomEmoticon.builder().emoticonCode(EmoticonCode.EMOTICON_TP1).build());

        List<StatisticsEntity> statisticsListByTeamCode =
                statisticsRepository.getStatisticsListByTeamCode(teamCode);

        statisticsListByTeamCode.forEach(se -> {
            keywordSet.add(se.getKeywordSet());
            roomEmoticon.addAll(se.getRoomEmoticons());
        });

        Map<EmoticonCode, List<RoomEmoticon>> collect = roomEmoticon.stream()
                .collect(Collectors.groupingBy(RoomEmoticon::getEmoticonCode));

        
//        Map<EmoticonCode, Long> collect2 = roomEmoticon.stream()
//                .collect(
//                        Collectors.groupingBy(RoomEmoticon::getEmoticonCode, Collectors.counting()))
//                .entrySet().stream()
//                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                .collect(Collectors.toMap(Entry::getKey, entry-> entry.getValue()))
//                .limit(3)
//                .collect(Collectors.toMap(Entry::getKey, entry-> entry.getValue() / roomEmoticon.size()));
        
    Map<EmoticonCode, Long> collect2 = roomEmoticon.stream()
      .collect(
              Collectors.groupingBy(RoomEmoticon::getEmoticonCode, Collectors.counting()));
        
    Map<EmoticonCode, Long> collect3 = collect2.entrySet().stream()
      .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
      .limit(3)
      .collect(Collectors.toMap(Entry::getKey, entry-> entry.getValue()));
        
        System.out.println("collect2:::"+collect2);

    }
}
