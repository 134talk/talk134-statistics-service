package kr.co.talk.domain.statistics.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestDto {
    @Data
    public static class TeamCodeResponseDto {
        private String teamCode;
    }

    @Data
    public static class AdminSearchUserIdResponseDto {
        private String name;
        private String nickname;
    }

}
