package ch.bader.budget.domain.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatisticsRequest {

    private String realAccountId;
    private String virtualAccountId;
    private YearMonth from;
    private YearMonth to;

}
