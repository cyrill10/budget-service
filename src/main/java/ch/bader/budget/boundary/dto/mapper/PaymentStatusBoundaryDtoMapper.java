package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.PaymentStatus;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentStatusBoundaryDtoMapper {

    default ValueEnumBoundaryDto mapToDto(final PaymentStatus domain) {
        return ValueEnumBoundaryDto.builder().value(domain.getValue()).name(domain.getName()).build();
    }

    default PaymentStatus mapToDomain(final ValueEnumBoundaryDto dto) {
        return PaymentStatus.forValue(dto.getValue());
    }
}