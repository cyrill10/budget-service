package ch.bader.budget.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class Balance {

    private BigDecimal effective;
    private BigDecimal budgeted;

    public Balance() {
        this(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public Balance(final BigDecimal initialBalance) {
        this(initialBalance, initialBalance);
    }


    public Balance(final Balance copyBalance) {
        this(copyBalance.getEffective(), copyBalance.getBudgeted());
    }


    public void add(final BigDecimal effective, final BigDecimal budgeted) {
        this.effective = this.effective.add(effective);
        this.budgeted = this.budgeted.add(budgeted);
    }

    public void subtract(final BigDecimal effective, final BigDecimal budgeted) {
        this.effective = this.effective.subtract(effective);
        this.budgeted = this.budgeted.subtract(budgeted);
    }

    public Balance add(final Balance balance) {
        effective = effective.add(balance.effective);
        if (budgeted != null && balance.budgeted != null) {
            budgeted = budgeted.add(balance.budgeted);
        }
        return this;
    }
}
