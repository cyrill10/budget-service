package ch.bader.budget.adapter.repository;

import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.VirtualAccount;

import java.util.List;
import java.util.Map;

public interface VirtualAccountRepository {

    VirtualAccount getAccountById(String id);

    VirtualAccount addVirtualAccount(VirtualAccount virtualAccount);

    VirtualAccount updateVirtualAccount(VirtualAccount virtualAccount);

    Map<String, VirtualAccount> getAllVirtualAccountsWithTheirUnderlyingAccountAsMap();

    List<VirtualAccount> getAllVirtualAccountsWithTheirUnderlyingAccount();

    Map<RealAccount, List<VirtualAccount>> getAccountMap();

    List<VirtualAccount> getAccountsByRealAccount(RealAccount realAccount);
}

