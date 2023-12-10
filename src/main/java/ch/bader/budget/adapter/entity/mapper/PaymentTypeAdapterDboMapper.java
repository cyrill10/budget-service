package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.ValueEnumAdapterDbo;
import ch.bader.budget.type.PaymentType;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentTypeAdapterDboMapper {

    ValueEnumAdapterDbo mapToDbo(PaymentType domain);

    default PaymentType mapToDomain(final ValueEnumAdapterDbo entity) {
        if (entity == null) {
            return null;
        }
        return PaymentType.forValue(entity.getValue());
    }
}