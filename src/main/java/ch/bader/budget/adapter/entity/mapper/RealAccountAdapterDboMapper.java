package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.RealAccountAdapterDbo;
import ch.bader.budget.domain.RealAccount;
import org.mapstruct.Mapper;

@Mapper(uses = AccountTypeAdapterDboMapper.class)
public interface RealAccountAdapterDboMapper {

    RealAccountAdapterDbo mapToDbo(RealAccount domain);

    RealAccount mapToDomain(RealAccountAdapterDbo entity);
}
