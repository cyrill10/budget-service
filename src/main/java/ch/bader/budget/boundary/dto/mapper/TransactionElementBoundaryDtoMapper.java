package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.TransactionElementBoundaryDto;
import ch.bader.budget.domain.TransactionListElement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TransactionElementBoundaryDtoMapper {

    @Mapping(target = "amount", source = "effectiveAmount")
    @Mapping(target = "balance", source = "effectiveBalance")
    TransactionElementBoundaryDto mapToDto(TransactionListElement domain);
}
