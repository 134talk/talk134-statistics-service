package kr.co.talk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import kr.co.talk.domain.statistics.model.StatisticsEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class Talk134StatisticsServiceApplication implements ApplicationRunner {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    public static void main(String[] args) {
        SpringApplication.run(Talk134StatisticsServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("application runner run~~");

        CreateTableRequest tableRequest =
                dynamoDBMapper.generateCreateTableRequest(StatisticsEntity.class);
        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        boolean created = TableUtils.createTableIfNotExists(amazonDynamoDB, tableRequest);

        if (created) {
            log.debug("amazonDynamoDB created table statistics");
        } else {
            log.debug("amazonDynamoDB already exist table statistics");
        }
    }

}
