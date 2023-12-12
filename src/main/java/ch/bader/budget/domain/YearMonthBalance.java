package ch.bader.budget.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@Builder
@AllArgsConstructor
public class YearMonthBalance {

    private String id;
    private YearMonth yearMonth;
    private String virtualAccountId;
    private BigDecimal balance;
}
