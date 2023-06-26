package kr.co.talk.domain.statistics.service;

import java.util.*;
import java.util.stream.Collectors;

import kr.co.talk.domain.statistics.dto.NicknameRankingResponseDto;
import kr.co.talk.global.client.UserClient;
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
    private final UserClient userClient;

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

    public NicknameRankingResponseDto nicknameRankingResponseDto(String teamCode) {
        List<String> nicknameList = userClient.getTeamProfileCode(teamCode);

        return NicknameRankingResponseDto.builder()
                .type1(getType1List(nicknameList))
                .type2(getType2List(nicknameList))
                .type3(getType3List(nicknameList))
                .build();
    }

    private List<NicknameRankingResponseDto.Type1> getType1List(List<String> nicknameList) {
        List<String> valueList = nicknameList.stream().map(s -> s.substring(0, 2)).collect(Collectors.toList());
        Map<String, Integer> top3ValuePercentage = eachPercentage(valueList);

        List<NicknameRankingResponseDto.Type1> type1List = new ArrayList<>();
        for (String key : top3ValuePercentage.keySet()) {
            type1List.add(NicknameRankingResponseDto.Type1.builder()
                    .emotion(emotionValue(key))
                    .score(top3ValuePercentage.get(key))
                    .build());
        }
        return type1List;
    }

    private List<NicknameRankingResponseDto.Type2> getType2List(List<String> nicknameList) {
        List<String> valueList = nicknameList.stream().map(s -> s.substring(3, 4)).collect(Collectors.toList());
        Map<String, Integer> top3ActPercentage = eachPercentage(valueList);
        List<NicknameRankingResponseDto.Type2> type2List = new ArrayList<>();
        for (String key : top3ActPercentage.keySet()) {
            type2List.add(NicknameRankingResponseDto.Type2.builder()
                    .act(actValue(key))
                    .score(top3ActPercentage.get(key))
                    .build());
        }
        return type2List;
    }

    private List<NicknameRankingResponseDto.Type3> getType3List(List<String> nicknameList) {
        List<String> valueList = nicknameList.stream().map(s -> s.substring(5)).collect(Collectors.toList());
        Map<String, Integer> top3StatusPercentage = eachPercentage(valueList);
        List<NicknameRankingResponseDto.Type3> type3List = new ArrayList<>();
        for (String key : top3StatusPercentage.keySet()) {
            type3List.add(NicknameRankingResponseDto.Type3.builder()
                    .status(statusValue(key))
                    .score(top3StatusPercentage.get(key))
                    .build());
        }
        return type3List;
    }

    private static Map<String, Integer> eachPercentage(List<String> valueList) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        int totalCount = valueList.size();
        for (String value : valueList) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(frequencyMap.entrySet());
        Collections.sort(sortedEntries, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        Map<String, Integer> percentageMap = new LinkedHashMap<>();
        int count = 0;
        int maxCount = Math.min(sortedEntries.size(), 3); // 최대 개수 조정
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            double percentage = (double) entry.getValue() / totalCount * 100;
            percentageMap.put(entry.getKey(), formatPercentage(percentage));
            count++;
            if (count == maxCount) { // 최대 개수에 도달하면 종료
                break;
            }
        }

        return percentageMap;
    }

    private static int formatPercentage(Double value) {
        int roundedValue = (int) Math.round(value);
        return roundedValue;
    }

    private String emotionValue(String emotion) {
        String value = new String();

        if (emotion.equals("fl")) {
            value = "희망이 조금씩 생기는";
        } else if (emotion.equals("co")) {
            value = "점차 걱정이 생기는";
        } else if (emotion.equals("re")) {
            value = "속이 타는 마음";
        } else if (emotion.equals("up")) {
            value = "정신없이 요동치는";
        } else if (emotion.equals("si")) {
            value = "고요하고 감사한";
        }
        return value;
    }

    private String actValue (String act) {
        String value = new String();

        if (act.equals("t")) {
            value = "경청을 잘하고, 전체 의견을 조율하는";
        } else if (act.equals("w")) {
            value = "열정적인 목표 달성과 리더십";
        } else if (act.equals("a")) {
            value = "치밀한 계획과 효율이 중요한";
        } else if (act.equals("d")) {
            value = "힘을 빼고, 유쾌하게 목표 달성";
        } else if (act.equals("r")) {
            value = "고요하게, 반격을 준비하는";
        }
        return value;
    }

    private String statusValue (String status) {
        String value = new String();

        if (status.equals("ng")) {
            value = "배움/성장";
        } else if (status.equals("sp")) {
            value = "휴식/여행";
        } else if (status.equals("ha")) {
            value = "공감/평화";
        } else if (status.equals("fu")) {
            value = "성과달성/보상받기";
        } else if (status.equals("bl")) {
            value = "변화/해결";
        }
        return value;
    }
}
