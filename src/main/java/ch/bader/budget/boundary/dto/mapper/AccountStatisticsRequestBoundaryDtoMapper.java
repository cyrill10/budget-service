package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.AccountStatisticsRequestBoundaryDto;
import ch.bader.budget.domain.statistics.AccountStatisticsRequest;
import jakarta.inject.Inject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class AccountStatisticsRequestBoundaryDtoMapper {

    @Inject
    YearMonthMapper yearMonthMapper;

    @Mapping(target = "from", expression = "java(yearMonthMapper.mapToYearMonth(dto.getFromYear(), dto.getFromMonth()))")
    @Mapping(target = "to", expression = "java(yearMonthMapper.mapToYearMonth(dto.getToYear(), dto.getToMonth()))")
    public abstract AccountStatisticsRequest mapFromBoundaryDto(AccountStatisticsRequestBoundaryDto dto);

}
