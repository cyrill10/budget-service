package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.PaymentType;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentTypeBoundaryDtoMapper {

    default ValueEnumBoundaryDto mapToDto(final PaymentType domain) {
        return ValueEnumBoundaryDto.builder().value(domain.getValue()).name(domain.getName()).build();
    }

    default PaymentType mapToDomain(final ValueEnumBoundaryDto dto) {
        return PaymentType.forValue(dto.getValue());
    }
}