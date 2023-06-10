package kr.co.talk.global.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

@Configuration
@EnableDynamoDBRepositories
public class DynamoDBConfig {

	@Value("${amazon.dynamodb.endpoint}")
	private String amazonDynamoDBEndpoint;

	@Value("${amazon.dynamodb.region}")
	private String region;

	@Value("${amazon.aws.accesskey}")
	private String amazonAWSAccessKey;

	@Value("${amazon.aws.secretkey}")
	private String amazonAWSSecretKey;

	@Bean
	public DynamoDBMapper dynamoDBMapper() {
		return new DynamoDBMapper(buildAmazonDynamoDB());
	}

	@Bean
	public AmazonDynamoDB buildAmazonDynamoDB() {
		return AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, region))
				.withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials()))
				.build();
	}

	private AWSCredentials amazonAWSCredentials() {
		return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
	}
}