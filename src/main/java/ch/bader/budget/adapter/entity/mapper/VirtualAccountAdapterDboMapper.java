package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.VirtualAccountAdapterDbo;
import ch.bader.budget.domain.VirtualAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {RealAccountAdapterDboMapper.class, ObjectIdAdapterDboMapper.class, BigDecimalAdapterDboMapper.class})
public interface VirtualAccountAdapterDboMapper {

    @Mapping(target = "underlyingAccountId", source = "underlyingAccount.id")
    VirtualAccountAdapterDbo mapToDbo(VirtualAccount domain);

    @Mapping(target = "underlyingAccount", ignore = true)
    VirtualAccount mapToDomain(VirtualAccountAdapterDbo entity);
}
