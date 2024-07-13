package ch.bader.budget.domain.statistics;

import ch.bader.budget.type.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatistics {

    private String id;
    private String name;
    private AccountType accountType;
    private BigDecimal balanceIngoing;
    private BigDecimal balanceOutgoing;
}
