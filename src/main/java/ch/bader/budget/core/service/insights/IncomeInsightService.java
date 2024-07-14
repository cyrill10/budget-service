package ch.bader.budget.core.service.insights;

import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.statistics.Insights;
import ch.bader.budget.domain.statistics.InsightsRequest;
import ch.bader.budget.type.AccountType;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@ApplicationScoped
public class IncomeInsightService extends AbstractInsightService {

    public Insights getIncomeInsights(final InsightsRequest insightsRequest) {
        final List<VirtualAccount> incomeVirtualAccounts = getInsightVirtualAccounts(insightsRequest.getAccountIds(),
            List.of(AccountType.CHECKING, AccountType.SAVING));
        final List<Transaction> transactions = getInsightTransactions(insightsRequest, incomeVirtualAccounts);

        final Map<VirtualAccount, List<Transaction>> incomeTransactionsByIncomeReason = transactions
            .stream()
            .filter(transaction -> incomeVirtualAccounts.contains(transaction.getDebitedAccount()))
            .filter(transaction -> !incomeVirtualAccounts.contains(transaction.getCreditedAccount()))
            .collect(groupingBy(Transaction::getCreditedAccount));

        return Insights.builder().insights(transformTransactionMap(incomeTransactionsByIncomeReason)).build();
    }
}
