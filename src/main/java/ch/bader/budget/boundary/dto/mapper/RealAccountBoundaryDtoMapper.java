package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.RealAccountBoundaryDto;
import ch.bader.budget.domain.RealAccount;
import org.mapstruct.Mapper;

@Mapper(uses = AccountTypeBoundaryDtoMapper.class)
public interface RealAccountBoundaryDtoMapper {

    RealAccount mapToDomain(RealAccountBoundaryDto dto);

    RealAccountBoundaryDto mapToDto(RealAccount domain);

//    RealAccountDbo mapToEntity(RealAccount domain);
//
//    RealAccount mapToDomain(RealAccountDbo entity);
}
