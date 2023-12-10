package ch.bader.budget.core.balance;

import ch.bader.budget.adapter.repository.YearMonthBalanceRepository;
import ch.bader.budget.boundary.time.MonthGenerator;
import ch.bader.budget.core.balance.function.BudgetedAmountFunction;
import ch.bader.budget.core.balance.function.EffectiveAmountFunction;
import ch.bader.budget.domain.Balance;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.YearMonthBalance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VirtualAccountBalanceService {

    public static final BudgetedAmountFunction BUDGETED_AMOUNT_FUNCTION = new BudgetedAmountFunction();
    private static final EffectiveAmountFunction EFFECTIVE_AMOUNT_FUNCTION = new EffectiveAmountFunction();

    @Inject
    YearMonthBalanceRepository yearMonthBalanceRepository;

    @Inject
    MonthGenerator monthGenerator;


    /*
    the input is assumed to be all possilbe transactions in the database, as it is stupid, if everybody filters all the
    transactions for each call here. this is simply to reduce the amount where this is traversed
    if the list is already shorted it is okey as well
     */
    public Balance getBalanceAtYearMonth(final VirtualAccount virtualAccount,
                                         final YearMonth yearMonth,
                                         final List<Transaction> allTransactions) {

        if (virtualAccount.isAlienAccount() || virtualAccount.isPrebudgetedAccount()) {
            return calculateBalanceForAlienAndPrebudgeted(virtualAccount, yearMonth, allTransactions);
        }
        return calculateBalanceForNormalAccount(virtualAccount, yearMonth, allTransactions);
    }

    private Balance calculateBalanceForNormalAccount(final VirtualAccount virtualAccount,
                                                     final YearMonth yearMonth,
                                                     final List<Transaction> transactions) {
        final Optional<YearMonthBalance> latestBalanceForVirtualAccountOptional = yearMonthBalanceRepository.getLatestYearMonthBalanceForVirtualAccount(
            virtualAccount,
            yearMonth);

        return latestBalanceForVirtualAccountOptional
            .map(latestBalanceForVirtualAccount -> getBalanceWithYearMontBalance(virtualAccount,
                yearMonth,
                transactions,
                latestBalanceForVirtualAccount))
            .orElseGet(() -> getBalanceWithoutYearMonthBalance(virtualAccount, yearMonth, transactions));
    }

    private Balance getBalanceWithoutYearMonthBalance(final VirtualAccount virtualAccount,
                                                      final YearMonth yearMonth,
                                                      final List<Transaction> transactions) {
        final Balance balance = new Balance(virtualAccount.getInitialBalance());

        addTransactionsToBalance(balance, transactions, virtualAccount, yearMonth.atEndOfMonth());

        return balance;
    }

    private Balance getBalanceWithYearMontBalance(final VirtualAccount virtualAccount,
                                                  final YearMonth yearMonth,
                                                  final List<Transaction> transactions,
                                                  final YearMonthBalance latestBalanceForVirtualAccount) {

        final Balance balance = new Balance(latestBalanceForVirtualAccount.getBalance());

        if (!yearMonth.equals(latestBalanceForVirtualAccount.getYearMonth())) {
            addTransactionsToBalance(balance,
                transactions,
                virtualAccount,
                latestBalanceForVirtualAccount.getYearMonth().atDay(1),
                yearMonth.atEndOfMonth());
        }
        return balance;
    }

    private Balance calculateBalanceForAlienAndPrebudgeted(final VirtualAccount virtualAccount,
                                                           final YearMonth yearMonth,
                                                           final List<Transaction> transactions) {
        final Balance balance = new Balance();

        addTransactionsToBalance(balance, transactions, virtualAccount, yearMonth.atDay(1), yearMonth.atEndOfMonth());

        return balance;
    }

    private void addTransactionsToBalance(final Balance balance,
                                          final List<Transaction> transactionsForBalance,
                                          final VirtualAccount virtualAccount,
                                          final LocalDate highDate) {

        addTransactionsToBalance(balance,
            transactionsForBalance,
            virtualAccount,
            monthGenerator.getStartDate(),
            highDate);
    }

    private void addTransactionsToBalance(final Balance balance,
                                          final List<Transaction> transactions,
                                          final VirtualAccount virtualAccount,
                                          final LocalDate lowDateIncl,
                                          final LocalDate highDateIncl) {

        transactions
            .stream()
            .distinct()
            .filter(t -> t.isForAccount(virtualAccount))
            .filter(t -> !t.getDate().isBefore(lowDateIncl))
            .filter(t -> !highDateIncl.isAfter(t.getDate()))
            .forEach(t -> {
                final BigDecimal effectiveBalanceChange = EFFECTIVE_AMOUNT_FUNCTION.apply(t);
                final BigDecimal budgetedBalanceChange = BUDGETED_AMOUNT_FUNCTION.apply(t,
                    virtualAccount.isPrebudgetedAccount());
                if (virtualAccount == t.getCreditedAccount()) {
                    balance.subtract(effectiveBalanceChange, budgetedBalanceChange);
                }
                if (virtualAccount == t.getDebitedAccount()) {
                    balance.add(effectiveBalanceChange, budgetedBalanceChange);
                }
            });
    }
}
