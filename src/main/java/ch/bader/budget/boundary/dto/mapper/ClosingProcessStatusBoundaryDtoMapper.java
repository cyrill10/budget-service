package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.type.ClosingProcessStatus;
import org.mapstruct.Mapper;

@Mapper
public interface ClosingProcessStatusBoundaryDtoMapper {

//    ValueEnumDbo mapToDbo(ClosingProcessStatus domain);

    ValueEnumBoundaryDto mapToDto(ClosingProcessStatus domain);

//    default ClosingProcessStatus mapToDomain(ValueEnumDbo entity) {
//        if (entity != null) {
//            return ClosingProcessStatus.forValue(entity.getValue());
//        }
//        return ClosingProcessStatus.NEW;
//    }

}