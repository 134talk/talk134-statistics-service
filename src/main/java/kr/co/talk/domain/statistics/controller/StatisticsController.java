package kr.co.talk.domain.statistics.controller;

import kr.co.talk.domain.statistics.dto.NicknameRankingResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kr.co.talk.domain.statistics.dto.FeedbackReportDetailDto;
import kr.co.talk.domain.statistics.repository.StatisticsRepository;
import kr.co.talk.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 피드백 리포트 상세 조회 api
     * 
     * @param teamCode
     * @return
     */
    @GetMapping("/feedback/detail/{teamCode}")
    public ResponseEntity<?> feedbackDetail(@PathVariable("teamCode") String teamCode) {
        return ResponseEntity.ok(statisticsService.feedbackDetail(teamCode));
    }

    @GetMapping("/team/character/{teamCode}")
    public NicknameRankingResponseDto teamCharacter(@PathVariable("teamCode") String teamCode) {
        return statisticsService.nicknameRankingResponseDto(teamCode);
    }
    
    @GetMapping("/report/chatlog/{teamCode}")
    public ResponseEntity<?> reportChatlog(@PathVariable("teamCode") String teamCode){
        return ResponseEntity.ok(statisticsService.reportChatlog(teamCode));
    }
    
    @GetMapping("/admin/report/list/{teamCode}")
    public ResponseEntity<?> reportList(@PathVariable("teamCode") String teamCode){
        return ResponseEntity.ok(statisticsService.adminReportList(teamCode));
    }
    
    @GetMapping("/admin/report/search/list/{teamCode}")
    public ResponseEntity<?> reportListWithSearchName(@PathVariable("teamCode") String teamCode, @RequestParam String searchName){
        return ResponseEntity.ok(statisticsService.adminReportListWithSearchName(teamCode, searchName));
    }
}
