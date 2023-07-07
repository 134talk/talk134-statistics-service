package kr.co.talk.domain.statistics.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 피드백 필수값 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private long roomId;
	private String sentence;
	private int score;

	private int statusEnergy;
	private int statusRelation;
	private int statusStress;
	private int statusStable;

	private List<Feedback> feedback;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Feedback implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private long toUserId;
		private String review;
//		private int feedbackScore;
	}

}
