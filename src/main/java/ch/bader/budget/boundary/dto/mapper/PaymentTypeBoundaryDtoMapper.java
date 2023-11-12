package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.PaymentType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface PaymentTypeBoundaryDtoMapper {

//    ValueEnumDbo mapToDbo(PaymentType domain);

    default ValueEnumBoundaryDto mapToDto(PaymentType domain) {
        return ValueEnumBoundaryDto.builder().value(domain.getValue()).name(domain.getName()).build();
    }

//    default PaymentType mapToDomain(ValueEnumDbo entity) {
//        return PaymentType.forValue(entity.getValue());
//    }

    default PaymentType mapToDomain(ValueEnumBoundaryDto dto) {
        return PaymentType.forValue(dto.getValue());
    }
}