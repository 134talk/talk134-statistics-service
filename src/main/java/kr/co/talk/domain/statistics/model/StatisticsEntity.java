package kr.co.talk.domain.statistics.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedJson;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import kr.co.talk.global.constants.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@DynamoDBTable(tableName = AppConstants.TABLE_STATISTICS)
public class StatisticsEntity {
	@DynamoDBHashKey
	private long roomId;
	
	private String teamCode;
	

	@DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
	@DynamoDBAttribute
	private LocalDateTime time; // 대화방 시작 시간?

	@DynamoDBAttribute
	@DynamoDBTypeConvertedJson
	private List<Users> users = new ArrayList<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Users {
		private long userId;
		private String sentence;
		private int score;
	}

	public static class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

		@Override
		public String convert(final LocalDateTime time) {
			return time.toString();
		}

		@Override
		public LocalDateTime unconvert(final String stringValue) {
			return LocalDateTime.parse(stringValue);
		}
	}
}
