package ch.bader.budget.domain;

import ch.bader.budget.type.AccountType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class RealAccount extends Account implements Comparable<RealAccount> {
    private String id;
    private String name;
    private AccountType accountType;

    @Override
    public boolean isPrebudgetedAccount() {
        return getAccountType().isPrebudgetedAccount();
    }

    @Override
    public int compareTo(final RealAccount o) {
        final int compareType = getAccountType().compareTo(o.getAccountType());
        if (compareType == 0) {
            return getName().compareTo(o.getName());
        }
        return compareType;
    }

    @Override
    public boolean isAlienAccount() {
        return getAccountType().isAlienAccount();
    }

    @Override
    public boolean isForeignCurrencyAccount() {
        return AccountType.FOREIGN_CURRENCY.equals(getAccountType());
    }

    @Override
    public boolean isRelevantForTransaction(final Transaction transaction) {
        return this.equals(transaction.getCreditedAccount().getUnderlyingAccount()) || this.equals(transaction
            .getDebitedAccount()
            .getUnderlyingAccount());
    }

    @Override
    public boolean isDebitedAccount(final Transaction transaction) {
        return this.equals(transaction.getDebitedAccount().getUnderlyingAccount());
    }

    @Override
    public boolean isCreditedAccount(final Transaction transaction) {
        return this.equals(transaction.getCreditedAccount().getUnderlyingAccount());
    }

}
