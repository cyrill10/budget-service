package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.AccountStatisticsRequestBoundaryDto;
import ch.bader.budget.domain.statistics.AccountStatisticsRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.YearMonth;

@Mapper
public abstract class AccountStatisticsRequestBoundaryDtoMapper {

    @Mapping(target = "from", expression = "java(mapToYearMonth(dto.getFromYear(), dto.getFromMonth()))")
    @Mapping(target = "to", expression = "java(mapToYearMonth(dto.getToYear(), dto.getToMonth()))")

    public abstract AccountStatisticsRequest mapFromBoundaryDto(AccountStatisticsRequestBoundaryDto dto);

    YearMonth mapToYearMonth(final int year, final int month) {
        return YearMonth.of(year, month);
    }
}
