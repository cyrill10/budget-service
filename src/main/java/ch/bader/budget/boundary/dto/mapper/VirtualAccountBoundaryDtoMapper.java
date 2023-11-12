package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.VirtualAccountBoundaryDto;
import ch.bader.budget.domain.VirtualAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi", uses = RealAccountBoundaryDtoMapper.class)
public interface VirtualAccountBoundaryDtoMapper {

    VirtualAccount mapToDomain(VirtualAccountBoundaryDto dto);

    VirtualAccountBoundaryDto mapToDto(VirtualAccount domain);
}
