package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.ValueEnumAdapterDbo;
import ch.bader.budget.type.AccountType;
import org.mapstruct.Mapper;

@Mapper
public interface AccountTypeAdapterDboMapper {

    ValueEnumAdapterDbo mapToDbo(AccountType domain);

    default AccountType mapToDomain(final ValueEnumAdapterDbo entity) {
        return AccountType.forValue(entity.getValue());
    }
}