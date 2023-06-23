package kr.co.talk.domain.statistics.service;

import java.util.List;
import org.springframework.stereotype.Service;
import kr.co.talk.domain.statistics.dto.FeedbackReportDetailDto;
import kr.co.talk.domain.statistics.model.StatisticsEntity.Users;
import kr.co.talk.domain.statistics.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    public FeedbackReportDetailDto feedbackDetail(String teamCode) {
        List<Users> users = statisticsRepository.getUsers(teamCode);
        int totalSize = users.size();

        long totalStatusEnergy = 0;
        long totalStatusRelation = 0;
        long totalStatusStress = 0;
        long totalStatusStable = 0;

        for (Users user : users) {
            totalStatusEnergy += user.getStatusEnergy();
            totalStatusRelation += user.getStatusRelation();
            totalStatusStress += user.getStatusStress();
            totalStatusStable += user.getStatusStable();
        }

        return FeedbackReportDetailDto.builder()
                .energyPercent(percent(totalStatusEnergy, totalSize))
                .relationPercent(percent(totalStatusRelation, totalSize))
                .stressPercent(percent(totalStatusStress, totalSize))
                .stablePercent(percent(totalStatusStable, totalSize))
                .build();


    }

    private int percent(long current, int size) {
        return (int) (current / size);
    }

}
