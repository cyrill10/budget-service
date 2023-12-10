package ch.bader.budget.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.BiPredicate;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class VirtualAccount extends Account implements Comparable<VirtualAccount> {
    private String id;
    private String name;
    private BigDecimal balance;
    private Boolean isDeleted;
    private RealAccount underlyingAccount;

    @Override
    public boolean isPrebudgetedAccount() {
        return getUnderlyingAccount().isPrebudgetedAccount();
    }

    @Override
    public boolean isAlienAccount() {
        return getUnderlyingAccount().isAlienAccount();
    }

    @Override
    public boolean isRelevantForTransaction(final Transaction transaction) {
        return this.equals(transaction.getCreditedAccount()) || this.equals(transaction.getDebitedAccount());
    }

    @Override
    public boolean isDebitedAccount(final Transaction transaction) {
        return this.equals(transaction.getDebitedAccount());
    }

    @Override
    public boolean isCreditedAccount(final Transaction transaction) {
        return this.equals(transaction.getCreditedAccount());
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(isDeleted);
    }

    private static BiPredicate<Transaction, LocalDate> noFilter = (transaction, date) -> true;

    private static BiPredicate<Transaction, LocalDate> onlyLastMonthFilter = (transaction, date) -> {
        LocalDate firstOfLastMonth = date.minusMonths(1);
        return !transaction.getDate().isBefore(firstOfLastMonth);
    };

    @Override
    public int compareTo(final VirtualAccount o) {
        return getName().compareTo(o.getName());
    }

    public BiPredicate<Transaction, LocalDate> getTransactionPredicate() {
        if (isPrebudgetedAccount()) {
            return onlyLastMonthFilter;
        }
        return noFilter;
    }

    public Balance getInitialBalance() {
        if (getUnderlyingAccount().getAccountType().isAlienAccount()) {
            return new Balance(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return new Balance(getBalance(), getBalance());
    }
}
