package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.AccountType;
import org.mapstruct.Mapper;

@Mapper
public interface AccountTypeBoundaryDtoMapper {

//    ValueEnumDbo mapToDbo(AccountType domain);

    ValueEnumBoundaryDto mapToDto(AccountType domain);

//    default AccountType mapToDomain(ValueEnumDbo entity) {
//        return AccountType.forValue(entity.getValue());
//    }

    default AccountType mapToDomain(ValueEnumBoundaryDto dto) {
        return AccountType.forValue(dto.getValue());
    }
}