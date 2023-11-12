package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.TransferDetailBoundaryDto;
import ch.bader.budget.domain.TransferDetails;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface TransferDetailBoundaryDtoMapper {

    TransferDetailBoundaryDto mapToDto(TransferDetails domain);
}
