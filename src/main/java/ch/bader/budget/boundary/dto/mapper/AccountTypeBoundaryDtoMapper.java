package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.AccountType;
import org.mapstruct.Mapper;

@Mapper
public interface AccountTypeBoundaryDtoMapper {

    default ValueEnumBoundaryDto mapToDto(final AccountType domain) {
        return ValueEnumBoundaryDto.builder().name(domain.getName()).value(domain.getValue()).build();
    }

    default AccountType mapToDomain(final ValueEnumBoundaryDto dto) {
        return AccountType.forValue(dto.getValue());
    }
}