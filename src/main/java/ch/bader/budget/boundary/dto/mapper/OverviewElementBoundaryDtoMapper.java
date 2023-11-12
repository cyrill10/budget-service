package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.OverviewElementBoundaryDto;
import ch.bader.budget.domain.OverviewElement;
import org.mapstruct.Mapper;

@Mapper
public interface OverviewElementBoundaryDtoMapper {

    OverviewElementBoundaryDto mapToDto(OverviewElement domain);
}
