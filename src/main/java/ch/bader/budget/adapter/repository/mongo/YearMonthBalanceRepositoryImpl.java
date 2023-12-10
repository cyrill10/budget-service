package ch.bader.budget.adapter.repository.mongo;

import ch.bader.budget.adapter.entity.YearMonthBalanceAdapterDbo;
import ch.bader.budget.adapter.entity.mapper.YearMonthBalanceAdapterDboMapper;
import ch.bader.budget.adapter.repository.YearMonthBalanceRepository;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.YearMonthBalance;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
        return Optional.empty();
    }

    @Override
    public List<YearMonthBalance> getLatestYearMonthBalanceForVirtualAccounts(final List<VirtualAccount> virtualAccounts,
                                                                              final YearMonth yearMonth) {
        return null;
    }
}
