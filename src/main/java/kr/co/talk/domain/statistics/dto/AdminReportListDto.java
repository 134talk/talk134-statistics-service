package kr.co.talk.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportListDto {
    private long userId;
    private String profileUrl;
    private String name;
    private String nickname;
    private long chatCount;
}
