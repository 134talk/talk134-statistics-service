package kr.co.talk.domain.userreport.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDto {
    private long userId;
    private String nickname;
    private String profileUrl;
}
