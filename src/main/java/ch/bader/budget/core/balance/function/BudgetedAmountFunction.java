package ch.bader.budget.core.balance.function;

import ch.bader.budget.domain.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.BiFunction;

public class BudgetedAmountFunction implements BiFunction<Transaction, Boolean, BigDecimal> {

    @Override
    public BigDecimal apply(final Transaction t, final Boolean isForPrebudgetedAccount) {
        if (isForPrebudgetedAccount) {
            return t.getBudgetedAmount();
        }
        final LocalDate firstDayOfThisMonth = LocalDate.now().withDayOfMonth(1);
        if (firstDayOfThisMonth.isAfter(t.getDate())) {
            return new EffectiveAmountFunction().apply(t);
        }
        return t.getBudgetedAmount();
    }
}
