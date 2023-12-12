package ch.bader.budget.core.balance;

import ch.bader.budget.adapter.repository.YearMonthBalanceRepository;
import ch.bader.budget.boundary.time.MonthGenerator;
import ch.bader.budget.core.function.BudgetedAmountFunction;
import ch.bader.budget.core.function.EffectiveAmountFunction;
import ch.bader.budget.domain.Account;
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
import java.util.Optional;

@ApplicationScoped
public class AccountBalanceService {

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
                List.of(latestBalanceForVirtualAccount)))
            .orElseGet(() -> getBalanceWithoutYearMonthBalance(virtualAccount, yearMonth, transactions));
    }

    private Balance getBalanceWithoutYearMonthBalance(final VirtualAccount virtualAccount,
                                                      final YearMonth yearMonth,
                                                      final List<Transaction> transactions) {
        final Balance balance = new Balance(virtualAccount.getInitialBalance());

        addTransactionsToBalance(balance, transactions, virtualAccount, yearMonth.atEndOfMonth());

        return balance;
    }

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


    private <T extends Account> Balance getBalanceWithYearMontBalance(final T account,
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
                account,
                latestBalanceForVirtualAccounts.get(0).getYearMonth().plusMonths(1).atDay(1),
                yearMonth.atEndOfMonth());
        }
        return balance;
    }


    private <T extends Account> Balance calculateBalanceForAlienAndPrebudgeted(final T account,
                                                                               final YearMonth yearMonth,
                                                                               final List<Transaction> transactions) {
        final Balance balance = new Balance();

        addTransactionsToBalance(balance, transactions, account, yearMonth.atDay(1), yearMonth.atEndOfMonth());

        return balance;
    }


    private <T extends Account> void addTransactionsToBalance(final Balance balance,
                                                              final List<Transaction> transactionsForBalance,
                                                              final T account,
                                                              final LocalDate highDate) {

        addTransactionsToBalance(balance, transactionsForBalance, account, monthGenerator.getStartDate(), highDate);
    }

    private <T extends Account> void addTransactionsToBalance(final Balance balance,
                                                              final List<Transaction> transactions,
                                                              final T account,
                                                              final LocalDate lowDateIncl,
                                                              final LocalDate highDateIncl) {

        transactions
            .stream()
            .distinct()
            .filter(account::isRelevantForTransaction)
            .filter(t -> !t.getDate().isBefore(lowDateIncl))
            .filter(t -> !t.getDate().isAfter(highDateIncl))
            .forEach(t -> {
                final BigDecimal effectiveBalanceChange = EFFECTIVE_AMOUNT_FUNCTION.apply(t);
                final BigDecimal budgetedBalanceChange = BUDGETED_AMOUNT_FUNCTION.apply(t,
                    account.isPrebudgetedAccount());
                if (account.isCreditedAccount(t)) {
                    balance.subtract(effectiveBalanceChange, budgetedBalanceChange);
                }
                if (account.isDebitedAccount(t)) {
                    balance.add(effectiveBalanceChange, budgetedBalanceChange);
                }
            });
    }
}
