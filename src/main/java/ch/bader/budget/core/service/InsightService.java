package ch.bader.budget.core.service;

import ch.bader.budget.adapter.repository.RealAccountRepository;
import ch.bader.budget.adapter.repository.VirtualAccountRepository;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.statistics.InsightElement;
import ch.bader.budget.domain.statistics.Insights;
import ch.bader.budget.domain.statistics.InsightsRequest;
import ch.bader.budget.type.AccountType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@ApplicationScoped
public class InsightService {

    @Inject
    VirtualAccountRepository virtualAccountRepository;

    @Inject
    RealAccountRepository realAccountRepository;

    @Inject
    TransactionService transactionService;

    public Insights getInsights(final InsightsRequest insightsRequest) {
        final List<YearMonth> relevantMonths = getRelevantMonths(insightsRequest);
        return switch (insightsRequest.getInsightType()) {
            case INCOME -> getIncomeInsights(insightsRequest, relevantMonths);
            case SPENDING -> null;
            case SAVING -> null;
        };
    }

    private List<YearMonth> getRelevantMonths(final InsightsRequest insightsRequest) {
        if (CollectionUtils.isEmpty(insightsRequest.getMonths()) && CollectionUtils.isEmpty(insightsRequest.getYears())) {
            return List.of(YearMonth.now());
        }
        final List<Month> months = CollectionUtils.isEmpty(insightsRequest.getMonths()) ? List.of(Month.values()) : insightsRequest
            .getMonths()
            .stream()
            .map(Month::of)
            .toList();

        return insightsRequest
            .getYears()
            .stream()
            .map(year -> months.stream().map(month -> YearMonth.of(year, month)).toList())
            .flatMap(Collection::stream)
            .toList();
    }

    private Insights getIncomeInsights(final InsightsRequest insightsRequest, final List<YearMonth> relevantMonths) {
        final List<VirtualAccount> incomeVirtualAccount = getIncomeVirtualAccount(insightsRequest.getAccountIds());
        final List<Transaction> transactions = relevantMonths
            .stream()
            .map(yearMonth -> transactionService.getAllTransactionsForMonthAndVirtualAccounts(yearMonth,
                incomeVirtualAccount))
            .flatMap(Collection::stream)
            .toList();

        final Map<VirtualAccount, List<Transaction>> incomeTransactionsByIncomeReason = transactions
            .stream()
            .filter(transaction -> incomeVirtualAccount.contains(transaction.getDebitedAccount()))
            .filter(transaction -> !incomeVirtualAccount.contains(transaction.getCreditedAccount()))
            .collect(groupingBy(Transaction::getCreditedAccount));

        final List<InsightElement> insightElements = incomeTransactionsByIncomeReason
            .entrySet()
            .stream()
            .map(entry -> InsightElement
                .builder()
                .name(entry.getKey().getName())
                .amount(entry
                    .getValue()
                    .stream()
                    .map(Transaction::getEffectiveAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build())
            .toList();
        return Insights.builder().insights(insightElements).build();
    }

    private List<VirtualAccount> getIncomeVirtualAccount(final List<String> accountIds) {
        final List<RealAccount> relevantRealAccounts = realAccountRepository.getAllByAccountType(List.of(AccountType.CHECKING,
            AccountType.SAVING));
        return virtualAccountRepository
            .getAccountsByRealAccounts(relevantRealAccounts)
            .stream()
            .filter(virtualAccount -> CollectionUtils.isEmpty(accountIds) || accountIds.contains(virtualAccount.getId()))
            .toList();
    }
}
