package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.TransactionBoundaryDto;
import ch.bader.budget.domain.Transaction;
import org.mapstruct.Mapper;

@Mapper(uses = {VirtualAccountBoundaryDtoMapper.class, PaymentStatusBoundaryDtoMapper.class, PaymentTypeBoundaryDtoMapper.class, TransactionIndicationBoundaryDtoMapper.class, LocalDateMapper.class})
public interface TransactionBoundaryDtoMapper {

    Transaction mapToDomain(TransactionBoundaryDto dto);

    TransactionBoundaryDto mapToDto(Transaction domain);
}
