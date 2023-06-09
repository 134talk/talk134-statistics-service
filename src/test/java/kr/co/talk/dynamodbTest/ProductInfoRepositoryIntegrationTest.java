//package kr.co.talk.dynamodbTest;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.CoreMatchers.is;
//
//import java.util.List;
//
//import org.aspectj.lang.annotation.Before;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
//import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
//
//import kr.co.talk.domain.statistics.model.ProductInfo;
//import kr.co.talk.domain.statistics.repository.ProductInfoRepository;
//
//@SpringBootTest
//public class ProductInfoRepositoryIntegrationTest {
//
//	@Autowired
//    private DynamoDBMapper dynamoDBMapper;
//
//    @Autowired
//    ProductInfoRepository repository;
//
//    private static final String EXPECTED_COST = "20";
//    private static final String EXPECTED_PRICE = "50";
//
//    @Before
//    public void setup() throws Exception {
//        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
//        
//        CreateTableRequest tableRequest = dynamoDBMapper
//          .generateCreateTableRequest(ProductInfo.class);
//        tableRequest.setProvisionedThroughput(
//          new ProvisionedThroughput(1L, 1L));
//        amazonDynamoDB.createTable(tableRequest);
//        
//        //...
//
//        dynamoDBMapper.batchDelete(
//          (List<ProductInfo>)repository.findAll());
//    }
//
//    @Test
//    public void givenItemWithExpectedCost_whenRunFindAll_thenItemIsFound() { 
//        ProductInfo productInfo = new ProductInfo(EXPECTED_COST, EXPECTED_PRICE);
//        repository.save(productInfo); 
//        List<ProductInfo> result = (List<ProductInfo>) repository.findAll();
//
//        assertThat(result.size(), is(greaterThan(0)));
//        assertThat(result.get(0).getCost(), is(equalTo(EXPECTED_COST))); 
//    }
//}