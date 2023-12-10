package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.ValueEnumAdapterDbo;
import ch.bader.budget.type.TransactionIndication;
import org.mapstruct.Mapper;

@Mapper
public interface TransactionIndicationAdapterDboMapper {

    ValueEnumAdapterDbo mapToDbo(TransactionIndication domain);

    default TransactionIndication mapToDomain(final ValueEnumAdapterDbo entity) {
        if (entity == null) {
            return null;
        }
        return TransactionIndication.forValue(entity.getValue());
    }
}