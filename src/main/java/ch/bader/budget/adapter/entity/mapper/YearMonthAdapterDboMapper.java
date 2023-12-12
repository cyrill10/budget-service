package ch.bader.budget.adapter.entity.mapper;

import org.mapstruct.Mapper;

import java.time.YearMonth;

@Mapper
public interface YearMonthAdapterDboMapper {

    default String toDbo(final YearMonth yearMonth) {
        return yearMonth.toString();
    }

    default YearMonth toDomain(final String s) {
        return YearMonth.parse(s);
    }
}
