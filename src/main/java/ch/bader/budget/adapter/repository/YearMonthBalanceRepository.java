package ch.bader.budget.adapter.repository;

import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.domain.YearMonthBalance;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface YearMonthBalanceRepository {

    void deleteAllYearMonthBalances();

    void saveAll(List<YearMonthBalance> yearMonthBalances);

    Optional<YearMonthBalance> getLatestYearMonthBalanceForVirtualAccount(VirtualAccount virtualAccount,
                                                                          YearMonth yearMonth);

    /*
    When nicht alle Account einen eintrag zum letzten YearMonth haben wird eine leere liste geliefert
     */
    List<YearMonthBalance> getLatestYearMonthBalanceForVirtualAccounts(List<VirtualAccount> virtualAccounts,
                                                                       YearMonth yearMonth);
}
