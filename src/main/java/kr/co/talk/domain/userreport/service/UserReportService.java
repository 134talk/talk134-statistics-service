package kr.co.talk.domain.userreport.service;

import kr.co.talk.domain.statistics.model.StatisticsEntity;
import kr.co.talk.domain.statistics.repository.StatisticsRepository;
import kr.co.talk.domain.userreport.dto.DetailedUserReportDto;
import kr.co.talk.domain.userreport.dto.UserProfileDto;
import kr.co.talk.domain.userreport.dto.UserReportDateListDto;
import kr.co.talk.global.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.aggregations.AverageAggregation;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserReportService {
    private final OpenSearchClient openSearchClient;
    private final UserClient userClient;

    public UserReportDateListDto getUserReportDateList(Long userId) {
        String index = "sample-index";
        String aggName = "1";
        SearchResponse<Void> response = openSearchClient.search(searchBuilder ->
                searchBuilder
                    .index(index)
                    .size(0)
                    .query(query -> query
                        .bool(bool -> bool
                            .filter(filter -> filter
                                .term(term -> term
                                    .field("users.userId")
                                    .value(FieldValue.of(userId)
                                    )
                                )
                            )
                        )
                    )
                    .aggregations(aggName,
                        aggs -> aggs
                            .avg(AverageAggregation.of(avg -> avg
                                .field(""))))
                , StatisticsEntity.class);
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
                    if (feedback.getToUserId() == userId) {
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
}
