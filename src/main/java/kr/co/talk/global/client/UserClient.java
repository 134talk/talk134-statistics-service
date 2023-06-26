package kr.co.talk.global.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import kr.co.talk.domain.statistics.dto.RequestDto.TeamCodeResponseDto;
import kr.co.talk.global.config.FeignLoggingConfig;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "USER-SERVICE", configuration = FeignLoggingConfig.class)
public interface UserClient {
    @GetMapping("/user/findTeamCode/{userId}")
    public TeamCodeResponseDto findTeamCode(@PathVariable("userId") long userId);

    @GetMapping("/user/team/profile-code/{teamCode}")
    List<String> getTeamProfileCode(@PathVariable("teamCode") String teamCode);
}
