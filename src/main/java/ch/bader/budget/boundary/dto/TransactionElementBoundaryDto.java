package ch.bader.budget.boundary.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionElementBoundaryDto {

    private String name;
    private BigDecimal amount;
    private BigDecimal budgetedAmount;
    private BigDecimal balance;
    private BigDecimal budgetedBalance;
    private boolean hasAmount;
    private String id;


}
