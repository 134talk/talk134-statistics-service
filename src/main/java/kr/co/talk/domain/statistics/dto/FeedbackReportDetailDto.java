package kr.co.talk.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackReportDetailDto {
    private int energyPercent;
    private int relationPercent;
    private int stressPercent;
    private int stablePercent;
}
