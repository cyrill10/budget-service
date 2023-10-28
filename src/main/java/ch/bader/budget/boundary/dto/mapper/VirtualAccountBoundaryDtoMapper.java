package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.VirtualAccountBoundaryDto;
import ch.bader.budget.domain.VirtualAccount;
import org.mapstruct.Mapper;

@Mapper(uses = RealAccountBoundaryDtoMapper.class)
public interface VirtualAccountBoundaryDtoMapper {

    VirtualAccount mapToDomain(VirtualAccountBoundaryDto dto);

    VirtualAccountBoundaryDto mapToDto(VirtualAccount domain);

//    @Mapping(target = "underlyingAccountId", source = "underlyingAccount.id")
//    VirtualAccountDbo mapToEntity(VirtualAccount domain);
//
//    VirtualAccount mapToDomain(VirtualAccountDbo entity);
}
