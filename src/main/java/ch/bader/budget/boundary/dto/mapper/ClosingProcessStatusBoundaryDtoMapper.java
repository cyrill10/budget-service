package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.ClosingProcessStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface ClosingProcessStatusBoundaryDtoMapper {

//    ValueEnumDbo mapToDbo(ClosingProcessStatus domain);

    default ValueEnumBoundaryDto mapToDto(ClosingProcessStatus domain) {
        if (domain == null) {
            return null;
        }
        return ValueEnumBoundaryDto.builder().value(domain.getValue()).name(domain.getName()).build();
    }

//    default ClosingProcessStatus mapToDomain(ValueEnumDbo entity) {
//        if (entity != null) {
//            return ClosingProcessStatus.forValue(entity.getValue());
//        }
//        return ClosingProcessStatus.NEW;
//    }

}