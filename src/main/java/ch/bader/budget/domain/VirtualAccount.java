package ch.bader.budget.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.BiPredicate;

@Data
@Builder
public class VirtualAccount implements Comparable<VirtualAccount> {
    private String id;
    private String name;
    private BigDecimal balance;
    private Boolean isDeleted;
    private RealAccount underlyingAccount;

    public boolean isPrebudgetedAccount() {
        return getUnderlyingAccount().isPrebudgetedAccount();
    }

    public boolean isAlienAccount() {
        return getUnderlyingAccount().isAlienAccount();
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(isDeleted);
    }

    private static BiPredicate<Transaction, LocalDate> noFilter = (transaction, date) -> true;

    private static BiPredicate<Transaction, LocalDate> onlyLastMonthFilter =
        (transaction, date) -> {
            LocalDate firstOfLastMonth = date.minusMonths(1);
            return !transaction.getDate().isBefore(firstOfLastMonth);
        };

    @Override
    public int compareTo(VirtualAccount o) {
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
