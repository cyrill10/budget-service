package ch.bader.budget.core.balance;

import ch.bader.budget.adapter.repository.YearMonthBalanceRepository;
import ch.bader.budget.boundary.time.MonthGenerator;
import ch.bader.budget.core.balance.function.BudgetedAmountFunction;
import ch.bader.budget.core.balance.function.EffectiveAmountFunction;
import ch.bader.budget.domain.Balance;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.YearMonthBalance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@ApplicationScoped
public class RealAccountBalanceService {

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
    public Balance getBalanceAtYearMonth(final RealAccount realAccount,
                                         final List<VirtualAccount> virtualAccounts,
                                         final YearMonth yearMonth,
                                         final List<Transaction> allTransactions) {

        if (realAccount.isAlienAccount() || realAccount.isPrebudgetedAccount()) {
            return calculateBalanceForAlienAndPrebudgeted(realAccount, yearMonth, allTransactions);
        }
        return calculateBalanceForNormalAccount(realAccount, virtualAccounts, yearMonth, allTransactions);
    }

    private Balance calculateBalanceForNormalAccount(final RealAccount realAccount,
                                                     final List<VirtualAccount> virtualAccounts,
                                                     final YearMonth yearMonth,
                                                     final List<Transaction> transactions) {
        final List<YearMonthBalance> latestYearMonthBalancesForVirtualAccounts = yearMonthBalanceRepository.getLatestYearMonthBalanceForVirtualAccounts(
            virtualAccounts,
            yearMonth);


        if (CollectionUtils.isNotEmpty(latestYearMonthBalancesForVirtualAccounts)) {
            return getBalanceWithYearMontBalance(realAccount,
                yearMonth,
                transactions,
                latestYearMonthBalancesForVirtualAccounts);
        }
        return getBalanceWithoutYearMonthBalance(realAccount, virtualAccounts, yearMonth, transactions);
    }

    private Balance getBalanceWithoutYearMonthBalance(final RealAccount realAccount,
                                                      final List<VirtualAccount> virtualAccounts,
                                                      final YearMonth yearMonth,
                                                      final List<Transaction> transactions) {
        final Balance balance = virtualAccounts
            .stream()
            .map(VirtualAccount::getInitialBalance)
            .reduce(new Balance(), (balance1, balance2) -> {
                balance1.add(balance2);
                return balance1;
            });

        addTransactionsToBalance(balance, transactions, realAccount, yearMonth.atEndOfMonth());

        return balance;
    }

    private Balance getBalanceWithYearMontBalance(final RealAccount realAccount,
                                                  final YearMonth yearMonth,
                                                  final List<Transaction> transactions,
                                                  final List<YearMonthBalance> latestBalanceForVirtualAccounts) {

        final Balance balance = latestBalanceForVirtualAccounts
            .stream()
            .map(YearMonthBalance::getBalance)
            .reduce(new Balance(), (newBalance, bigDecimal) -> {
                newBalance.add(bigDecimal, bigDecimal);
                return newBalance;
            }, Balance::add);

        if (!yearMonth.equals(latestBalanceForVirtualAccounts.get(0).getYearMonth())) {
            addTransactionsToBalance(balance,
                transactions,
                realAccount,
                latestBalanceForVirtualAccounts.get(0).getYearMonth().atDay(1),
                yearMonth.atEndOfMonth());
        }
        return balance;
    }

    private Balance calculateBalanceForAlienAndPrebudgeted(final RealAccount realAccount,
                                                           final YearMonth yearMonth,
                                                           final List<Transaction> transactions) {
        final Balance balance = new Balance();

        addTransactionsToBalance(balance, transactions, realAccount, yearMonth.atDay(1), yearMonth.atEndOfMonth());

        return balance;
    }

    private void addTransactionsToBalance(final Balance balance,
                                          final List<Transaction> transactionsForBalance,
                                          final RealAccount realAccount,
                                          final LocalDate highDate) {

        addTransactionsToBalance(balance, transactionsForBalance, realAccount, monthGenerator.getStartDate(), highDate);
    }

    private void addTransactionsToBalance(final Balance balance,
                                          final List<Transaction> transactions,
                                          final RealAccount realAccount,
                                          final LocalDate lowDateIncl,
                                          final LocalDate highDateIncl) {

        transactions
            .stream()
            .distinct()
            .filter(realAccount::isRelevantForTransaction)
            .filter(t -> !t.getDate().isBefore(lowDateIncl))
            .filter(t -> !t.getDate().isAfter(highDateIncl))
            .forEach(t -> {
                final BigDecimal effectiveBalanceChange = EFFECTIVE_AMOUNT_FUNCTION.apply(t);
                final BigDecimal budgetedBalanceChange = BUDGETED_AMOUNT_FUNCTION.apply(t,
                    realAccount.isPrebudgetedAccount());
                if (realAccount.isCreditedAccount(t)) {
                    balance.subtract(effectiveBalanceChange, budgetedBalanceChange);
                }
                if (realAccount.isDebitedAccount(t)) {
                    balance.add(effectiveBalanceChange, budgetedBalanceChange);
                }
            });
    }
}
