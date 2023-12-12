package ch.bader.budget.adapter.repository.mongo;

import ch.bader.budget.adapter.entity.YearMonthBalanceAdapterDbo;
import ch.bader.budget.adapter.entity.mapper.YearMonthBalanceAdapterDboMapper;
import ch.bader.budget.adapter.repository.YearMonthBalanceRepository;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.YearMonthBalance;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class YearMonthBalanceRepositoryImpl implements YearMonthBalanceRepository, PanacheMongoRepository<YearMonthBalanceAdapterDbo> {

    @Inject
    YearMonthBalanceAdapterDboMapper yearMonthBalanceAdapterDboMapper;

    @Override
    public void deleteAllYearMonthBalances() {
        deleteAll();
    }

    @Override
    public void saveAll(final List<YearMonthBalance> yearMonthBalances) {
        persist(yearMonthBalances.stream().map(yearMonthBalanceAdapterDboMapper::mapToDbo));
    }

    @Override
    public Optional<YearMonthBalance> getLatestYearMonthBalanceForVirtualAccount(final VirtualAccount virtualAccount,
                                                                                 final YearMonth yearMonth) {
        return stream("virtualAccountId = ?1 and yearMonth <= ?2",
            virtualAccount.getId(),
            yearMonth.toString(),
            Sort.by("yearMonth").descending()).findFirst().map(yearMonthBalanceAdapterDboMapper::mapToDomain);
    }

    @Override
    public List<YearMonthBalance> getLatestYearMonthBalanceForVirtualAccounts(final List<VirtualAccount> virtualAccounts,
                                                                              final YearMonth yearMonth) {
        final List<YearMonthBalance> candidates = stream("virtualAccountId in ?1 and yearMonth <= ?2",
            virtualAccounts.stream().map(VirtualAccount::getId).toList(),
            yearMonth.toString(),
            Sort.by("yearMonth").descending()).map(yearMonthBalanceAdapterDboMapper::mapToDomain).toList();

        if (CollectionUtils.isNotEmpty(candidates)) {
            final YearMonth newestYearMonth = candidates.get(0).getYearMonth();

            final List<YearMonthBalance> candidatesWithSameYearMonth = candidates
                .stream()
                .filter(c -> newestYearMonth.equals(c.getYearMonth()))
                .toList();

            if (candidatesWithSameYearMonth.size() == virtualAccounts.size()) {
                return candidatesWithSameYearMonth;
            }
        }
        return List.of();
    }
}
