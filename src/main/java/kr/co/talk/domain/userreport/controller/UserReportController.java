package kr.co.talk.domain.userreport.controller;

import kr.co.talk.domain.userreport.dto.UserReportDateListDto;
import kr.co.talk.domain.userreport.service.UserReportService;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/statistics")
@RestController
public class UserReportController {
    private final UserReportService userReportService;

    @GetMapping("/user/report/{teamCode}")
    public UserReportDateListDto getUserReportList(@RequestHeader(value = "userId") Long userId, @PathVariable String teamCode) {
        if (userId == null) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        }
        return userReportService.getUserReportDateList(userId, teamCode);
    }
}
