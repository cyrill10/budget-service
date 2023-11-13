package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.ScannedTransactionAdapterDbo;
import ch.bader.budget.domain.ScannedTransaction;
import org.mapstruct.Mapper;

@Mapper(uses = {ObjectIdAdapterDboMapper.class, YearMonthAdapterDboMapper.class})
public interface ScannedTransactionAdapterDboMapper {

    ScannedTransaction mapToDomain(ScannedTransactionAdapterDbo entity);

    ScannedTransactionAdapterDbo mapToDbo(ScannedTransaction domain);
}
