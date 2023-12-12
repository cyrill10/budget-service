package ch.bader.budget.adapter.entity.mapper;

import ch.bader.budget.adapter.entity.YearMonthBalanceAdapterDbo;
import ch.bader.budget.domain.YearMonthBalance;
import org.mapstruct.Mapper;

@Mapper(uses = {ObjectIdAdapterDboMapper.class, BigDecimalAdapterDboMapper.class, YearMonthAdapterDboMapper.class})
public interface YearMonthBalanceAdapterDboMapper {

    YearMonthBalance mapToDomain(YearMonthBalanceAdapterDbo dbo);

    YearMonthBalanceAdapterDbo mapToDbo(YearMonthBalance domain);

}
