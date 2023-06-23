package kr.co.talk.domain.statistics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/feedback/detail/{teamCode}")
    public ResponseEntity<?> feedbackDetail(@PathVariable("teamCode") String teamCode) {
        return ResponseEntity.ok(statisticsService.feedbackDetail(teamCode));
    }
}
