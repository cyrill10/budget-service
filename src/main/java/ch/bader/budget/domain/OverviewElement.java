package ch.bader.budget.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class OverviewElement {

    private String name;
    private BigDecimal balanceAfter;
    private BigDecimal budgetedBalanceAfter;
    private BigDecimal projection;
    private BigDecimal budgetedProjection;
    private boolean isRealAccount;
    private String id;
}