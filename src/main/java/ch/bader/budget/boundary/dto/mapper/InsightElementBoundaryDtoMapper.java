package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.InsightElementBoundaryDto;
import ch.bader.budget.domain.statistics.InsightElement;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface InsightElementBoundaryDtoMapper {

    InsightElementBoundaryDto mapToBoundaryDto(InsightElement entity);

    List<InsightElementBoundaryDto> mapToBoundaryDtoAsList(List<InsightElement> entities);
}
