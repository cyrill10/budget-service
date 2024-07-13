package ch.bader.budget.core.service;

import ch.bader.budget.adapter.repository.RealAccountRepository;
import ch.bader.budget.adapter.repository.TransactionRepository;
import ch.bader.budget.adapter.repository.VirtualAccountRepository;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.statistics.AccountStatistics;
import ch.bader.budget.domain.statistics.AccountStatisticsRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class AccountStatisticsService {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    VirtualAccountRepository virtualAccountRepository;

    @Inject
    RealAccountRepository realAccountRepository;

    public AccountStatistics getAccountStatistics(final AccountStatisticsRequest accountStatisticsRequest) {

        if (accountStatisticsRequest.getVirtualAccountId() != null) {
            return getVirtualAccountStatistics(accountStatisticsRequest);
        }
        return getRealAccountStatistics(accountStatisticsRequest);
    }

    public AccountStatistics getRealAccountStatistics(final AccountStatisticsRequest accountStatisticsRequest) {

        final RealAccount realAccount = realAccountRepository.getAccountById(accountStatisticsRequest.getVirtualAccountId());

        final LocalDate lastOfMonthBefore = accountStatisticsRequest.getFrom().atDay(1).minusDays(1);
        final LocalDate firstOfNextMonth = accountStatisticsRequest.getTo().atEndOfMonth().plusDays(1);

        final List<String> virtualAccounts = virtualAccountRepository
            .getAccountsByRealAccount(realAccount)
            .stream()
            .map(VirtualAccount::getId)
            .toList();

        final List<Transaction> transactions = transactionRepository.findAllByDateBetweenAndVirtualAccountId(
            lastOfMonthBefore,
            firstOfNextMonth,
            virtualAccounts);

        final BigDecimal ingoingAmount = calculateIngoingRealAccount(transactions, virtualAccounts);
        final BigDecimal outgoingAmount = calculateOutgoingRealAccount(transactions, virtualAccounts);

        return AccountStatistics
            .builder()
            .id(realAccount.getId())
            .name(realAccount.getName())
            .accountType(realAccount.getAccountType())
            .balanceIngoing(ingoingAmount)
            .balanceOutgoing(outgoingAmount)
            .build();
    }

    public AccountStatistics getVirtualAccountStatistics(final AccountStatisticsRequest accountStatisticsRequest) {

        final VirtualAccount virtualAccount = virtualAccountRepository.getAccountById(accountStatisticsRequest.getVirtualAccountId());

        final LocalDate lastOfMonthBefore = accountStatisticsRequest.getFrom().atDay(1).minusDays(1);
        final LocalDate firstOfNextMonth = accountStatisticsRequest.getTo().atEndOfMonth().plusDays(1);
        final List<Transaction> transactions = transactionRepository.findAllByDateBetweenAndVirtualAccountId(
            lastOfMonthBefore,
            firstOfNextMonth,
            List.of(accountStatisticsRequest.getVirtualAccountId()));

        final BigDecimal ingoingAmount = calculateIngoingVirtualAccount(transactions, virtualAccount);
        final BigDecimal outgoingAmount = calculateOutgoingVirtualAccount(transactions, virtualAccount);

        return AccountStatistics
            .builder()
            .id(virtualAccount.getId())
            .name(virtualAccount.getName())
            .accountType(virtualAccount.getUnderlyingAccount().getAccountType())
            .balanceIngoing(ingoingAmount)
            .balanceOutgoing(outgoingAmount)
            .build();
    }

    private BigDecimal calculateOutgoingVirtualAccount(final List<Transaction> transactions,
                                                       final VirtualAccount virtualAccount) {
        return sumUpEffective(transactions
            .stream()
            .filter(transaction -> transaction.getCreditedAccount().equals(virtualAccount)));
    }

    private BigDecimal calculateIngoingVirtualAccount(final List<Transaction> transactions,
                                                      final VirtualAccount virtualAccount) {
        return sumUpEffective(transactions
            .stream()
            .filter(transaction -> transaction.getDebitedAccount().equals(virtualAccount)));
    }

    private BigDecimal calculateIngoingRealAccount(final List<Transaction> transactions,
                                                   final List<String> virtualAccounts) {
        return sumUpEffective(transactions
            .stream()
            .filter(transaction -> virtualAccounts.contains(transaction.getDebitedAccount().getId())));
    }

    private BigDecimal calculateOutgoingRealAccount(final List<Transaction> transactions,
                                                    final List<String> virtualAccounts) {
        return sumUpEffective(transactions
            .stream()
            .filter(transaction -> virtualAccounts.contains(transaction.getCreditedAccount().getId())));
    }


    private BigDecimal sumUpEffective(final Stream<Transaction> transactionStream) {
        return transactionStream.map(Transaction::getEffectiveAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
