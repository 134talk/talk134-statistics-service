package kr.co.talk.dynamodbTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.talk.domain.statistics.dto.FeedbackDto.Feedback;
import kr.co.talk.domain.statistics.model.StatisticsEntity;
import kr.co.talk.domain.statistics.model.StatisticsEntity.Users;
import kr.co.talk.domain.statistics.repository.StatisticsRepository;

@SpringBootTest
public class ProductInfoRepositoryIntegrationTest {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;
    
//    @Autowired
//    private StatisticsRepository statisticsRepository;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    public void setup() throws Exception {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest =
                dynamoDBMapper.generateCreateTableRequest(StatisticsEntity.class);
        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        boolean created = TableUtils.createTableIfNotExists(amazonDynamoDB, tableRequest);

        if (created) {
            System.out.println("테이블 생성됨");
        } else {
            System.out.println("이미 테이블 있음");
        }
        // amazonDynamoDB.createTable(tableRequest);

        // ...

        // dynamoDBMapper.batchDelete(
        // (List<ProductInfo>)repository.findAll());
    }

    @Test
    @DisplayName("statistics save test")
    public void saveTest() {
        List<Users> users = new ArrayList<>();

        users.add(Users.builder().userId(82).score(5).sentence("test sentence").build());

        users.add(Users.builder().userId(78).score(8).sentence("test sentence2").build());

        StatisticsEntity entity = StatisticsEntity.builder().roomId(48).teamCode("uaCGJl")
                .time(LocalDateTime.now()).users(users).build();

        dynamoDBMapper.save(entity);
    }

    @Test
    @DisplayName("statistics find test")
    public void findTest() {
        StatisticsEntity load = dynamoDBMapper.load(StatisticsEntity.class, 48);
        System.out.println("load::" + load);
        
//        Optional<StatisticsRepository> findById = statisticsRepository.findById(48L);
        
//        System.out.println("load222::" + findById.get());
//
//        List<Users> users = load.getUsers();
//        
//        for(Users u : users) {
//            System.out.println("users:::"+u);
//        }
//
//        Users users2 = users.get(0);
//        List<Feedback> feedback = users2.getFeedback();
//        System.out.println(feedback + "::::feedback:::");

    }

    // scan - full scan
    // query - key 를 기반으로 scan
    @Test
    @DisplayName("statistics find with scan")
    public void scanTest() {
        // DynamoDBScanExpression expression = new DynamoDBScanExpression()

        // dynamoDBMapper.scan(StatisticsEntity.class, null)
    }
    
    
}
