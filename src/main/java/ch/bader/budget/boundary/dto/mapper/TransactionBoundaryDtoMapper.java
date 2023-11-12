package ch.bader.budget.boundary.dto.mapper;

import ch.bader.budget.boundary.dto.TransactionBoundaryDto;
import ch.bader.budget.domain.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi", uses = {VirtualAccountBoundaryDtoMapper.class, PaymentStatusBoundaryDtoMapper.class, PaymentTypeBoundaryDtoMapper.class, TransactionIndicationBoundaryDtoMapper.class})
public interface TransactionBoundaryDtoMapper {

    Transaction mapToDomain(TransactionBoundaryDto dto);

    TransactionBoundaryDto mapToDto(Transaction domain);

//    Transaction mapToDomain(TransactionDbo entity);
//
//    @Mapping(target = "creditedAccountId", source = "creditedAccount.id")
//    @Mapping(target = "debitedAccountId", source = "debitedAccount.id")
//    TransactionDbo mapToEntity(Transaction domain);
}
