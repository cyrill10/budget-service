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
public class SavingsInsightService extends AbstractInsightService {

    public Insights getSavingsInsight(final InsightsRequest insightsRequest) {
        final List<VirtualAccount> savingsVirtualAccounts = getInsightVirtualAccounts(insightsRequest.getAccountIds(),
            List.of(AccountType.SAVING));
        final List<Transaction> transactions = getInsightTransactions(insightsRequest, savingsVirtualAccounts);

        final Map<VirtualAccount, List<Transaction>> savingsTransactionsBySavingsReason = transactions
            .stream()
            .filter(transaction -> savingsVirtualAccounts.contains(transaction.getDebitedAccount()))
            .filter(transaction -> !savingsVirtualAccounts.contains(transaction.getCreditedAccount()))
            .collect(groupingBy(Transaction::getDebitedAccount));

        return Insights.builder().insights(transformTransactionMap(savingsTransactionsBySavingsReason)).build();
    }
}
