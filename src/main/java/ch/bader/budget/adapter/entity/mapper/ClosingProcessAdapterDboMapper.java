package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.ClosingProcessAdapterDbo;
import ch.bader.budget.domain.ClosingProcess;
import org.mapstruct.Mapper;

@Mapper(uses = {ClosingProcessStatusAdapterDboMapper.class, YearMonthAdapterDboMapper.class, ObjectIdAdapterDboMapper.class})
public interface ClosingProcessAdapterDboMapper {

    ClosingProcess mapToDomain(ClosingProcessAdapterDbo entity);

    ClosingProcessAdapterDbo mapToDbo(ClosingProcess domain);
}
