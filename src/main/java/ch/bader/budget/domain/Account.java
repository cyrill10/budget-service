package ch.bader.budget.domain;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class Account {

    public abstract boolean isPrebudgetedAccount();

    public abstract boolean isAlienAccount();

    public abstract boolean isForeignCurrencyAccount();

    public abstract boolean isRelevantForTransaction(Transaction transaction);

    public abstract boolean isDebitedAccount(final Transaction transaction);

    public abstract boolean isCreditedAccount(final Transaction transaction);

}
