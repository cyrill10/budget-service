package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.TransactionIndication;
import org.mapstruct.Mapper;

@Mapper
public interface TransactionIndicationBoundaryDtoMapper {

    default ValueEnumBoundaryDto mapToDto(final TransactionIndication domain) {
        return ValueEnumBoundaryDto.builder().value(domain.getValue()).name(domain.getName()).build();
    }

    default TransactionIndication mapToDomain(final ValueEnumBoundaryDto dto) {
        return TransactionIndication.forValue(dto.getValue());
    }
}