package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.PaymentStatus;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentStatusBoundaryDtoMapper {

//    ValueEnumDbo mapToDbo(PaymentStatus domain);

    ValueEnumBoundaryDto mapToDto(PaymentStatus domain);

//    default PaymentStatus mapToDomain(ValueEnumDbo entity) {
//        return PaymentStatus.forValue(entity.getValue());
//    }

    default PaymentStatus mapToDomain(ValueEnumBoundaryDto dto) {
        return PaymentStatus.forValue(dto.getValue());
    }
}