package kr.co.talk.global.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import kr.co.talk.domain.statistics.dto.RequestDto.TeamCodeResponseDto;
import kr.co.talk.global.config.FeignLoggingConfig;

@FeignClient(name = "USER-SERVICE", configuration = FeignLoggingConfig.class)
public interface UserClient {
    @GetMapping("/user/findTeamCode/{userId}")
    public TeamCodeResponseDto findTeamCode(@PathVariable("userId") long userId);
}
