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
        long totalStatusEnergyBefore = 0;
        long totalStatusRelationBefore = 0;
        long totalStatusStressBefore = 0;
        long totalStatusStableBefore = 0;

        for (Users user : users) {
            totalStatusEnergy += user.getStatusEnergy();
            totalStatusEnergyBefore += user.getStatusEnergyBefore();
            totalStatusRelation += user.getStatusRelation();
            totalStatusRelationBefore += user.getStatusRelationBefore();
            totalStatusStress += user.getStatusStress();
            totalStatusStressBefore += user.getStatusStressBefore();
            totalStatusStable += user.getStatusStable();
            totalStatusStableBefore += user.getStatusStableBefore();
        }

        return FeedbackReportDetailDto.builder()
                .energyPercent(percent(totalStatusEnergyBefore, totalStatusEnergy, totalSize))
                .relationPercent(percent(totalStatusRelationBefore, totalStatusRelation, totalSize))
                .stressPercent(percent(totalStatusStressBefore, totalStatusStress, totalSize))
                .stablePercent(percent(totalStatusStableBefore, totalStatusStable, totalSize))
                .build();
 

    }

    private int percent(long before, long current, int size) {
        return (int) ((current - before) / size / before * 100) ;
    }

}
