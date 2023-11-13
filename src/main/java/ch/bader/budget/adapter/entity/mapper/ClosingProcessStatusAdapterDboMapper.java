package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.ValueEnumAdapterDbo;
import ch.bader.budget.type.ClosingProcessStatus;
import org.mapstruct.Mapper;

@Mapper
public interface ClosingProcessStatusAdapterDboMapper {

    ValueEnumAdapterDbo mapToDbo(ClosingProcessStatus domain);

    default ClosingProcessStatus mapToDomain(final ValueEnumAdapterDbo entity) {
        if (entity != null) {
            return ClosingProcessStatus.forValue(entity.getValue());
        }
        return ClosingProcessStatus.NEW;
    }

}