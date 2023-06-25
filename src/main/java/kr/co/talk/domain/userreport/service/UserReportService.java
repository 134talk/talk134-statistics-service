package kr.co.talk.domain.userreport.service;

import kr.co.talk.domain.statistics.model.StatisticsEntity;
import kr.co.talk.domain.statistics.repository.StatisticsRepository;
import kr.co.talk.domain.userreport.dto.UserReportDateListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserReportService {
    private final StatisticsRepository statisticsRepository;

    public UserReportDateListDto getUserReportDateList(Long userId, String teamCode) {
        var list = statisticsRepository.getStatisticsListByTeamCode(teamCode);
        var dateSet = list.stream()
                .filter(entity -> entity.getUsers().stream()
                        .anyMatch(user -> user.getUserId() == userId))
                .map(StatisticsEntity::getChatroomEndtime)
                .map(LocalDateTime::toLocalDate)
                .collect(Collectors.toUnmodifiableSet());

        return UserReportDateListDto.builder()
                .myReportList(dateSet)
                .build();
    }
}
