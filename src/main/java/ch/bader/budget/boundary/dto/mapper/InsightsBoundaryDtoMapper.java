package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.InsightsBoundaryDto;
import ch.bader.budget.domain.statistics.Insights;
import org.mapstruct.Mapper;

@Mapper(uses = InsightElementBoundaryDtoMapper.class)
public interface InsightsBoundaryDtoMapper {

    InsightsBoundaryDto mapToBoundaryDto(Insights entity);
}
