package ch.bader.budget.boundary.dto.mapper;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.YearMonth;

@ApplicationScoped
public class YearMonthMapper {

    YearMonth mapToYearMonth(final int year, final int month) {
        return YearMonth.of(year, month);
    }
}
