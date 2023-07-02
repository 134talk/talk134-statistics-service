package kr.co.talk.global.client;

import kr.co.talk.domain.statistics.dto.RequestDto.AdminSearchUserIdResponseDto;
import kr.co.talk.domain.statistics.dto.RequestDto.TeamCodeResponseDto;
import kr.co.talk.domain.userreport.dto.UserProfileDto;
import kr.co.talk.global.config.FeignLoggingConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "USER-SERVICE", configuration = FeignLoggingConfig.class)
public interface UserClient {
    @GetMapping("/user/findTeamCode/{userId}")
    public TeamCodeResponseDto findTeamCode(@PathVariable("userId") long userId);

    @GetMapping("/user/team/profile-code/{teamCode}")
    List<String> getTeamProfileCode(@PathVariable("teamCode") String teamCode);

    @GetMapping("/user/profiles")
    List<UserProfileDto> getProfiles(@RequestParam("userIds") String userIds);

    @GetMapping( "/user/admin/search/{searchId}")
    AdminSearchUserIdResponseDto adminSearchUser(@RequestHeader(value = "userId") long userId, @PathVariable(name = "searchId") long searchId);
}
