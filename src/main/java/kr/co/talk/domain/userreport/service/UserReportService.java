package kr.co.talk.domain.userreport.service;

import kr.co.talk.domain.statistics.dto.RequestDto.AdminSearchUserIdResponseDto;
import kr.co.talk.domain.statistics.model.StatisticsEntity;
import kr.co.talk.domain.statistics.repository.StatisticsRepository;
import kr.co.talk.domain.userreport.dto.AdminUserReportDto;
import kr.co.talk.domain.userreport.dto.DetailedUserReportDto;
import kr.co.talk.domain.userreport.dto.UserProfileDto;
import kr.co.talk.domain.userreport.dto.UserReportDateListDto;
import kr.co.talk.global.client.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserReportService {
    private final StatisticsRepository statisticsRepository;
    private final UserClient userClient;

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

    public DetailedUserReportDto getDetailedUserReport(long userId, String teamCode, LocalDate date) {
        var list = statisticsRepository.getStatisticsListByTeamCode(teamCode);
        List<StatisticsEntity> dateFilteredList = list.stream()
                .filter(entity -> entity.getUsers().stream()
                        .anyMatch(user -> user.getUserId() == userId))
                .filter(entity -> entity.getChatroomEndtime().toLocalDate().isEqual(date))
                .sorted(Comparator.comparing(StatisticsEntity::getChatroomEndtime))
                .collect(Collectors.toUnmodifiableList());

        DetailedUserReportDto.Effect effect = DetailedUserReportDto.Effect.builder().build();
        List<List<DetailedUserReportDto.ReceivedEmoticon>> emoticons = new ArrayList<>();
        List<String> sentences = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
        List<DetailedUserReportDto.ReceivedFeedback> feedbacks = new ArrayList<>();

        dateFilteredList.forEach(entity -> {
            entity.getUsers().stream()
                    .filter(users -> users.getUserId() == userId)
                    .forEach(users -> {
                        sentences.add(StringUtils.defaultString(users.getSentence(), ""));
                        scores.add(users.getScore());
                        effect.setEnergy(effect.getEnergy() + users.getStatusEnergy());
                        effect.setRelation(effect.getRelation() + users.getStatusRelation());
                        effect.setStress(effect.getStress() + users.getStatusStress());
                        effect.setStable(effect.getStable() + users.getStatusStable());
                    });
            List<DetailedUserReportDto.ReceivedEmoticon> emoticonList = new ArrayList<>();
            entity.getRoomEmoticons().stream()
                    .filter(emoticon -> emoticon.getToUserId() == userId)
                    .collect(Collectors.groupingBy(StatisticsEntity.RoomEmoticon::getEmoticonCode))
                    .forEach((emoticonCode, roomEmoticons) -> {
                        emoticonList.add(DetailedUserReportDto.ReceivedEmoticon.builder()
                                .name(emoticonCode.getName())
                                .count(roomEmoticons.size())
                                .build());
                    });
            emoticons.add(emoticonList);
            entity.getUsers().forEach(users -> {
                users.getFeedback().forEach(feedback -> {
                    if (StringUtils.isNotBlank(feedback.getReview()) && feedback.getToUserId() == userId) {
                        feedbacks.add(DetailedUserReportDto.ReceivedFeedback.builder()
                                .userId(users.getUserId())
                                .content(feedback.getReview())
                                .build());
                    }
                });
            });
        });

        if (!feedbacks.isEmpty()) {
            List<UserProfileDto> profiles = userClient.getProfiles(feedbacks.stream()
                    .map(DetailedUserReportDto.ReceivedFeedback::getUserId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));

            feedbacks.forEach(feedback -> {
                var profile = profiles.stream()
                        .filter(dto -> dto.getUserId() == feedback.getUserId())
                        .findFirst()
                        .orElseThrow();
                feedback.setNickname(profile.getNickname());
                feedback.setProfileImgUrl(profile.getProfileUrl());
            });
        }

        int count = dateFilteredList.size();

        // 평균 작업
        effect.setEnergy(effect.getEnergy() / count);
        effect.setStable(effect.getStable() / count);
        effect.setStress(effect.getStress() / count);
        effect.setRelation(effect.getRelation() / count);

        return DetailedUserReportDto.builder()
                .date(date)
                .count(count)
                .effect(effect)
                .receivedEmoticons(emoticons)
                .remainedSentences(sentences)
                .feedbacks(feedbacks)
                .scores(scores)
                .build();
    }

    public AdminUserReportDto adminGetTeamUserReport(long userId, long searchId, String teamCode) {
        List<StatisticsEntity> list = statisticsRepository.getStatisticsListByTeamCode(teamCode);
        AdminSearchUserIdResponseDto responseDto = userClient.adminSearchUser(userId, searchId);
        AdminUserReportDto reportDto = new AdminUserReportDto();

        List<AdminUserReportDto.ReceivedEmoticon> emoticons = new ArrayList<>();

        list.forEach(entity -> {
            entity.getUsers().stream()
                    .filter(users -> users.getUserId() == searchId)
                    .forEach(users -> {
                        reportDto.setScorePercent(reportDto.getScorePercent() + users.getScore());
                        reportDto.setEnergyPercent(reportDto.getEnergyPercent() + users.getStatusEnergy());
                        reportDto.setRelationPercent(reportDto.getRelationPercent() + users.getStatusRelation());
                        reportDto.setStressPercent(reportDto.getStressPercent() + users.getStatusStress());
                        reportDto.setStablePercent(reportDto.getStablePercent() + users.getStatusStable());
                    });

            entity.getRoomEmoticons().stream()
                    .filter(emoticon -> emoticon.getToUserId() == userId)
                    .collect(Collectors.groupingBy(StatisticsEntity.RoomEmoticon::getEmoticonCode))
                    .forEach((emoticonCode, roomEmoticons) -> {
                        emoticons.add(AdminUserReportDto.ReceivedEmoticon.builder()
                                .emoticon(emoticonCode.getName())
                                .totalCount(roomEmoticons.size())
                                .build());
                    });

        });

        List<StatisticsEntity.Users> userList = statisticsRepository.getUsers(teamCode);
        List<Long> userIdList = userList.stream().map(StatisticsEntity.Users::getUserId).collect(Collectors.toList());
        int userCount = (int) userIdList.stream().filter(c -> Objects.equals(c, searchId)).count();

        int scorePercent = (int) Math.ceil((double) reportDto.getScorePercent() / userCount);
        int energyPercent = userCount == 0 ? 0 : reportDto.getEnergyPercent() / userCount;
        int stablePercent = userCount == 0 ? 0 : reportDto.getStablePercent() / userCount;
        int stressPercent = userCount == 0 ? 0 : reportDto.getStressPercent() / userCount;
        int relationPercent = userCount == 0 ? 0 : reportDto.getRelationPercent() / userCount;

        return AdminUserReportDto.builder()
                .chatCount(userCount)
                .name(responseDto.getName())
                .nickname(responseDto.getNickname())
                .energyPercent(energyPercent)
                .stablePercent(stablePercent)
                .stressPercent(stressPercent)
                .relationPercent(relationPercent)
                .receivedEmoticons(emoticons)
                .scorePercent(scorePercent)
                .build();
    }
}
