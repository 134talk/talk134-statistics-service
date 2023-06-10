package kr.co.talk.dynamodbTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

import kr.co.talk.domain.statistics.model.StatisticsEntity;
import kr.co.talk.domain.statistics.model.StatisticsEntity.Users;

@SpringBootTest
public class ProductInfoRepositoryIntegrationTest {

	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	@Autowired
	private AmazonDynamoDB amazonDynamoDB;

	@BeforeEach
	public void setup() throws Exception {
		dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

		CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(StatisticsEntity.class);
		tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

		boolean created = TableUtils.createTableIfNotExists(amazonDynamoDB, tableRequest);

		if (created) {
			System.out.println("테이블 생성됨");
		} else {
			System.out.println("이미 테이블 있음");
		}
//		amazonDynamoDB.createTable(tableRequest);

		// ...

//        dynamoDBMapper.batchDelete(
//          (List<ProductInfo>)repository.findAll());
	}

	@Test
	@DisplayName("statistics save test")
	public void saveTest() {
		List<Users> users = new ArrayList<>();

		users.add(Users.builder().userId(82).score(5).sentence("test sentence").build());

		users.add(Users.builder().userId(78).score(8).sentence("test sentence2").build());

		StatisticsEntity entity = StatisticsEntity.builder().roomId(48).teamCode("uaCGJl").time(LocalDateTime.now()).users(users).build();

		dynamoDBMapper.save(entity);
	}
	
	@Test
	@DisplayName("statistics find test")
	public void findTest() {
		StatisticsEntity load = dynamoDBMapper.load(StatisticsEntity.class, 48);
		
		System.out.println("load::"+load);
		
	}

	// scan - full scan
	// query - key 를 기반으로 scan
	@Test
	@DisplayName("statistics find with scan")
	public void scanTest() {
//		DynamoDBScanExpression expression = new DynamoDBScanExpression()
				
//		dynamoDBMapper.scan(StatisticsEntity.class, null)
	}
}