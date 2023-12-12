package ch.bader.budget.boundary.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverviewElementBoundaryDto {

    private String name;
    private BigDecimal balanceAfter;
    private BigDecimal budgetedBalanceAfter;
    private BigDecimal projection;
    private BigDecimal budgetedProjection;
    private boolean isRealAccount;
    private String id;
    
}