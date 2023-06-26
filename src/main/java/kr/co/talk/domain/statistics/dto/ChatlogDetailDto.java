package kr.co.talk.domain.statistics.dto;

import java.util.List;

import kr.co.talk.domain.statistics.dto.NicknameRankingResponseDto.Type1;
import kr.co.talk.domain.statistics.dto.NicknameRankingResponseDto.Type2;
import kr.co.talk.domain.statistics.dto.NicknameRankingResponseDto.Type3;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대화 기록 상세 리포트
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatlogDetailDto {
	
	private List<ChatlogDetailEmoticon> emoticonScore;
	
	private List<ChatlogDetailKeyword> keywordScore;
	
	private List<String> questionList;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChatlogDetailEmoticon {
		private int code;
		private String name;
		private int score;
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChatlogDetailKeyword {
		private int code;
		private int score;
	}
}
