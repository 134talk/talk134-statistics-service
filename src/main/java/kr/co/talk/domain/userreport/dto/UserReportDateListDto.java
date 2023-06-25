package kr.co.talk.domain.userreport.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class UserReportDateListDto {
    private Set<LocalDate> myReportList;
}
