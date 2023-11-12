package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.AccountType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface AccountTypeBoundaryDtoMapper {

//    ValueEnumDbo mapToDbo(AccountType domain);

    default ValueEnumBoundaryDto mapToDto(AccountType domain) {
        return ValueEnumBoundaryDto.builder().name(domain.getName()).value(domain.getValue()).build();
    }

//    default AccountType mapToDomain(ValueEnumDbo entity) {
//        return AccountType.forValue(entity.getValue());
//    }

    default AccountType mapToDomain(ValueEnumBoundaryDto dto) {
        return AccountType.forValue(dto.getValue());
    }
}