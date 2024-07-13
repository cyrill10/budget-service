package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.AccountStatisticsBoundaryDto;
import ch.bader.budget.domain.statistics.AccountStatistics;
import org.mapstruct.Mapper;

@Mapper(uses = AccountTypeBoundaryDtoMapper.class)
public interface AccountStatisticsBoundaryDtoMapper {

    AccountStatisticsBoundaryDto mapToBoundaryDto(AccountStatistics entity);
}
