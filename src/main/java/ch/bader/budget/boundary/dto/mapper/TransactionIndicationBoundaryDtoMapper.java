package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.TransactionIndication;
import org.mapstruct.Mapper;

@Mapper
public interface TransactionIndicationBoundaryDtoMapper {

//    ValueEnumDbo mapToDbo(TransactionIndication domain);

    ValueEnumBoundaryDto mapToDto(TransactionIndication domain);

//    default TransactionIndication mapToDomain(ValueEnumDbo entity) {
//        return TransactionIndication.forValue(entity.getValue());
//    }

    default TransactionIndication mapToDomain(ValueEnumBoundaryDto dto) {
        return TransactionIndication.forValue(dto.getValue());
    }
}