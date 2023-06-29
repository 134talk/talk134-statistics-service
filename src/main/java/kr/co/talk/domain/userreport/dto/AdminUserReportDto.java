package kr.co.talk.domain.userreport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserReportDto {
    private String nickname;
    private String name;
    private int chatCount;
    private int energyPercent;
    private int relationPercent;
    private int stressPercent;
    private int stablePercent;
    private List<List<ReceivedEmoticon>> receivedEmoticons;
    private int scorePercent;

    @Builder
    @Data
    public static class ReceivedEmoticon {
        private String emoticon;
        private int totalCount;
    }

}