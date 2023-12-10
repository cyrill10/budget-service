package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.TransactionAdapterDbo;
import ch.bader.budget.domain.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {VirtualAccountAdapterDboMapper.class, PaymentStatusAdapterDboMapper.class, PaymentTypeAdapterDboMapper.class, TransactionIndicationAdapterDboMapper.class, ObjectIdAdapterDboMapper.class, BigDecimalAdapterDboMapper.class, LocalDateAdapterDboMapper.class})
public interface TransactionAdapterDboMapper {

    Transaction mapToDomain(TransactionAdapterDbo entity);

    @Mapping(target = "creditedAccountId", source = "creditedAccount.id")
    @Mapping(target = "debitedAccountId", source = "debitedAccount.id")
    TransactionAdapterDbo mapToDbo(Transaction domain);
}
