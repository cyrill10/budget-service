package ch.bader.budget.core.balance;

import ch.bader.budget.core.balance.function.BudgetedAmountFunction;
import ch.bader.budget.core.balance.function.EffectiveAmountFunction;
import ch.bader.budget.domain.Account;
import ch.bader.budget.domain.Balance;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.TransactionListElement;
import ch.bader.budget.domain.VirtualAccount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedList;
import java.util.List;

@ApplicationScoped
public class TransactionListElementBuilderService {

    public static final EffectiveAmountFunction EFFECTIVE_AMOUNT_FUNCTION = new EffectiveAmountFunction();
    public static final BudgetedAmountFunction BUDGETED_AMOUNT_FUNCTION = new BudgetedAmountFunction();

    @Inject
    VirtualAccountBalanceService virtualAccountBalanceService;

    @Inject
    RealAccountBalanceService realAccountBalanceService;

    public List<TransactionListElement> getTransactionListElementsForMonth(final List<Transaction> allTransactionsForAccount,
                                                                           final VirtualAccount virtualAccount,
                                                                           final YearMonth month) {


        if (virtualAccount.isAlienAccount() || virtualAccount.isPrebudgetedAccount()) {
            return calculateTransactionListElements(new Balance(), allTransactionsForAccount, virtualAccount, month);
        }
        final Balance balanceAtBeginn = virtualAccountBalanceService.getBalanceAtYearMonth(virtualAccount,
            month.minusMonths(1),
            allTransactionsForAccount);

        return calculateTransactionListElements(balanceAtBeginn, allTransactionsForAccount, virtualAccount, month);

    }

    public List<TransactionListElement> getTransactionListElementsForMonth(final List<Transaction> allTransactionsForAccount,
                                                                           final RealAccount realAccount,
                                                                           final List<VirtualAccount> virtualAccounts,
                                                                           final YearMonth month) {

        if (realAccount.isAlienAccount() || realAccount.isPrebudgetedAccount()) {
            return calculateTransactionListElements(new Balance(), allTransactionsForAccount, realAccount, month);
        }
        final Balance balanceAtBeginn = realAccountBalanceService.getBalanceAtYearMonth(realAccount,
            virtualAccounts,
            month.minusMonths(1),
            allTransactionsForAccount);

        return calculateTransactionListElements(balanceAtBeginn, allTransactionsForAccount, realAccount, month);

    }

    private <T extends Account> List<TransactionListElement> calculateTransactionListElements(final Balance balance,
                                                                                              final List<Transaction> allTransactionsForAccount,
                                                                                              final T account,
                                                                                              final YearMonth month) {

        final Balance transactionsWithAlien = new Balance(BigDecimal.ZERO, BigDecimal.ZERO);

        final LinkedList<TransactionListElement> transactionList = new LinkedList<>();

        transactionList.add(new TransactionListElement("Before", balance.getEffective(), balance.getBudgeted(), "0"));

        allTransactionsForAccount
            .stream()
            .filter(t -> !month.atDay(1).isAfter(t.getDate()))
            .filter(t -> !month.atEndOfMonth().isBefore(t.getDate()))
            .filter(account::isRelevantForTransaction)
            .forEach(transaction -> {
                final TransactionListElement transactionListElement = createTransactionElement(transaction,
                    account,
                    balance);
                updateAlienTransaction(transactionsWithAlien, transaction, transactionListElement);
                transactionList.add(transactionListElement);
            });

        transactionList.add(new TransactionListElement("After",
            balance.getEffective(),
            balance.getBudgeted(),
            String.valueOf(Integer.MAX_VALUE - 1)));

        if (account instanceof VirtualAccount) {
            transactionList.add(new TransactionListElement(transactionsWithAlien.getEffective(),
                transactionsWithAlien.getEffective()));
        }
        return transactionList;
    }

    private void updateAlienTransaction(final Balance transactionWithAlien,
                                        final Transaction transaction,
                                        final TransactionListElement transactionListElement) {
        if (isTransactionWithAlienAccount(transaction)) {
            transactionWithAlien.add(transactionListElement.getEffectiveAmount(),
                transactionListElement.getBudgetedAmount());
        }

    }

    private boolean isTransactionWithAlienAccount(final Transaction transaction) {
        return transaction.getDebitedAccount().isAlienAccount() || transaction
            .getCreditedAccount()
            .isAlienAccount() || transaction.getDebitedAccount().isPrebudgetedAccount() || transaction
            .getCreditedAccount()
            .isPrebudgetedAccount();
    }


    public TransactionListElement createTransactionElement(final Transaction transaction,
                                                           final VirtualAccount virtualAccount,
                                                           final Balance accountBalance) {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal budgetedAmount = BigDecimal.ZERO;
        if (virtualAccount.equals(transaction.getDebitedAccount())) {
            amount = transaction.getEffectiveAmount();
            budgetedAmount = transaction.getBudgetedAmount();
            accountBalance.add(EFFECTIVE_AMOUNT_FUNCTION.apply(transaction),
                BUDGETED_AMOUNT_FUNCTION.apply(transaction, virtualAccount.isPrebudgetedAccount()));
        }
        if (virtualAccount.equals(transaction.getCreditedAccount())) {
            amount = BigDecimal.ZERO.subtract(transaction.getEffectiveAmount());
            budgetedAmount = BigDecimal.ZERO.subtract(transaction.getBudgetedAmount());

            accountBalance.subtract(EFFECTIVE_AMOUNT_FUNCTION.apply(transaction),
                BUDGETED_AMOUNT_FUNCTION.apply(transaction, virtualAccount.isPrebudgetedAccount()));
        }

        return new TransactionListElement(transaction,
            amount,
            budgetedAmount,
            accountBalance.getEffective(),
            accountBalance.getBudgeted());
    }

    public TransactionListElement createTransactionElement(final Transaction transaction,
                                                           final RealAccount realAccount,
                                                           final Balance accountBalance) {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal budgetedAmount = BigDecimal.ZERO;
        if (realAccount.equals(transaction.getDebitedAccount().getUnderlyingAccount())) {
            amount = transaction.getEffectiveAmount();
            budgetedAmount = transaction.getBudgetedAmount();
            accountBalance.add(EFFECTIVE_AMOUNT_FUNCTION.apply(transaction),
                BUDGETED_AMOUNT_FUNCTION.apply(transaction, realAccount.isPrebudgetedAccount()));
        }
        if (realAccount.equals(transaction.getCreditedAccount().getUnderlyingAccount())) {
            amount = BigDecimal.ZERO.subtract(transaction.getEffectiveAmount());
            budgetedAmount = BigDecimal.ZERO.subtract(transaction.getBudgetedAmount());

            accountBalance.subtract(EFFECTIVE_AMOUNT_FUNCTION.apply(transaction),
                BUDGETED_AMOUNT_FUNCTION.apply(transaction, realAccount.isPrebudgetedAccount()));
        }

        return new TransactionListElement(transaction,
            amount,
            budgetedAmount,
            accountBalance.getEffective(),
            accountBalance.getBudgeted());
    }

    public <T extends Account> TransactionListElement createTransactionElement(final Transaction transaction,
                                                                               final T account,
                                                                               final Balance accountBalance) {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal budgetedAmount = BigDecimal.ZERO;
        if (account.isDebitedAccount(transaction)) {
            amount = transaction.getEffectiveAmount();
            budgetedAmount = transaction.getBudgetedAmount();
            accountBalance.add(EFFECTIVE_AMOUNT_FUNCTION.apply(transaction),
                BUDGETED_AMOUNT_FUNCTION.apply(transaction, account.isPrebudgetedAccount()));
        }
        if (account.isCreditedAccount(transaction)) {
            amount = BigDecimal.ZERO.subtract(transaction.getEffectiveAmount());
            budgetedAmount = BigDecimal.ZERO.subtract(transaction.getBudgetedAmount());

            accountBalance.subtract(EFFECTIVE_AMOUNT_FUNCTION.apply(transaction),
                BUDGETED_AMOUNT_FUNCTION.apply(transaction, account.isPrebudgetedAccount()));
        }

        return new TransactionListElement(transaction,
            amount,
            budgetedAmount,
            accountBalance.getEffective(),
            accountBalance.getBudgeted());
    }
}
