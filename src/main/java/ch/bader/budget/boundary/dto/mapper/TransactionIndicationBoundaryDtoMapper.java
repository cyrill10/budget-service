package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.TransactionIndication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface TransactionIndicationBoundaryDtoMapper {

//    ValueEnumDbo mapToDbo(TransactionIndication domain);

    default ValueEnumBoundaryDto mapToDto(TransactionIndication domain) {
        return ValueEnumBoundaryDto.builder().value(domain.getValue()).name(domain.getName()).build();
    }

//    default TransactionIndication mapToDomain(ValueEnumDbo entity) {
//        return TransactionIndication.forValue(entity.getValue());
//    }

    default TransactionIndication mapToDomain(ValueEnumBoundaryDto dto) {
        return TransactionIndication.forValue(dto.getValue());
    }
}