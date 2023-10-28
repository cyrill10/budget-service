package ch.bader.budget.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionListElement implements Comparable<TransactionListElement> {

    private String name;
    private BigDecimal effectiveAmount;
    private BigDecimal budgetedAmount;
    private BigDecimal effectiveBalance;
    private BigDecimal budgetedBalance;
    private Boolean hasAmount;
    private String id;

    public TransactionListElement(Transaction transaction, BigDecimal effectiveAmount,
                                  BigDecimal budgetedAmount,
                                  BigDecimal effectiveBalance,
                                  BigDecimal budgetedBalance) {
        name = transaction.getDescription();
        id = transaction.getId();
        this.effectiveAmount = effectiveAmount;
        this.budgetedAmount = budgetedAmount;
        this.effectiveBalance = effectiveBalance;
        this.budgetedBalance = budgetedBalance;
        hasAmount = true;
    }

    public TransactionListElement(String name, BigDecimal effectiveBalance,
                                  BigDecimal budgetedBalance, String id) {
        this.name = name;
        this.effectiveBalance = effectiveBalance;
        this.budgetedBalance = budgetedBalance;
        hasAmount = false;
        effectiveAmount = BigDecimal.ZERO;
        budgetedAmount = BigDecimal.ZERO;
        this.id = id;
    }

    public TransactionListElement(BigDecimal alienTransactionEffective,
                                  BigDecimal alienTransactionBudgeted) {
        name = "In/Out";
        effectiveBalance = alienTransactionEffective;
        budgetedBalance = alienTransactionBudgeted;
        hasAmount = false;
        id = String.valueOf(Integer.MAX_VALUE);
    }

    @Override
    public int compareTo(TransactionListElement o) {
        return id.compareTo(o.id);

    }
}
