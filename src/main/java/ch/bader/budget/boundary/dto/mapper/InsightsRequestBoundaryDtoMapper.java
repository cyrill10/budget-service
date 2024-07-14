package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.InsightsRequestBoundaryDto;
import ch.bader.budget.domain.statistics.InsightsRequest;
import org.mapstruct.Mapper;

@Mapper
public interface InsightsRequestBoundaryDtoMapper {

    InsightsRequest mapFromBoundaryDto(InsightsRequestBoundaryDto dto);
}
