package ch.bader.budget.core.service;

import ch.bader.budget.adapter.repository.VirtualAccountRepository;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.VirtualAccount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class VirtualAccountService {

    @Inject
    VirtualAccountRepository virtualAccountRepository;

    public VirtualAccount addVirtualAccount(final VirtualAccount virtualAccount) {
        return virtualAccountRepository.addVirtualAccount(virtualAccount);
    }

    public VirtualAccount updateVirtualAccount(final VirtualAccount virtualAccount) {
        return virtualAccountRepository.updateVirtualAccount(virtualAccount);
    }

    public VirtualAccount getAccountById(final String id) {
        return virtualAccountRepository.getAccountById(id);
    }

    public List<VirtualAccount> getAllVirtualAccounts() {
        return virtualAccountRepository.getAllVirtualAccountsWithTheirUnderlyingAccount();
    }

    public Map<RealAccount, List<VirtualAccount>> getAccountMap() {
        return virtualAccountRepository.getAccountMap();
    }
}
