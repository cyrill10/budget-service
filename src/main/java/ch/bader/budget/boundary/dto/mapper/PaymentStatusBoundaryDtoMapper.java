package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.PaymentStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface PaymentStatusBoundaryDtoMapper {

//    ValueEnumDbo mapToDbo(PaymentStatus domain);

    default ValueEnumBoundaryDto mapToDto(PaymentStatus domain) {
        return ValueEnumBoundaryDto.builder().value(domain.getValue()).name(domain.getName()).build();
    }

//    default PaymentStatus mapToDomain(ValueEnumDbo entity) {
//        return PaymentStatus.forValue(entity.getValue());
//    }

    default PaymentStatus mapToDomain(ValueEnumBoundaryDto dto) {
        return PaymentStatus.forValue(dto.getValue());
    }
}