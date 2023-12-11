package ch.bader.budget.core.function;

import ch.bader.budget.domain.Transaction;

import java.math.BigDecimal;
import java.util.function.Function;

public class EffectiveAmountFunction implements Function<Transaction, BigDecimal> {

    @Override
    public BigDecimal apply(final Transaction t) {
        if (t.getEffectiveAmount().compareTo(BigDecimal.ZERO) == 0 && !isTransactionPrebudgeted(t)) {
            return t.getBudgetedAmount();
        }
        return t.getEffectiveAmount();
    }

    private boolean isTransactionPrebudgeted(final Transaction t) {
        return t.getCreditedAccount().isPrebudgetedAccount() || t.getDebitedAccount().isPrebudgetedAccount();
    }
}
