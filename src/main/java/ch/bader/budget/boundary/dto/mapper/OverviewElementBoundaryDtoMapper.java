package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.OverviewElementBoundaryDto;
import ch.bader.budget.domain.OverviewElement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface OverviewElementBoundaryDtoMapper {

    OverviewElementBoundaryDto mapToDto(OverviewElement domain);
}
