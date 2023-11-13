package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.ValueEnumAdapterDbo;
import ch.bader.budget.type.PaymentStatus;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentStatusAdapterDboMapper {

    ValueEnumAdapterDbo mapToDbo(PaymentStatus domain);

    default PaymentStatus mapToDomain(final ValueEnumAdapterDbo entity) {
        return PaymentStatus.forValue(entity.getValue());
    }
}