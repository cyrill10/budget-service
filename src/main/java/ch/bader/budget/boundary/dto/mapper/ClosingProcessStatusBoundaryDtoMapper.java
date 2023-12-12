package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.ClosingProcessStatus;
import org.mapstruct.Mapper;

@Mapper
public interface ClosingProcessStatusBoundaryDtoMapper {

    default ValueEnumBoundaryDto mapToDto(final ClosingProcessStatus domain) {
        if (domain == null) {
            return null;
        }
        return ValueEnumBoundaryDto.builder().value(domain.getValue()).name(domain.getName()).build();
    }
}