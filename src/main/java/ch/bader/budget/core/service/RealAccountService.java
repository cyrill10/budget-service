package ch.bader.budget.core.service;

import ch.bader.budget.adapter.repository.RealAccountRepository;
import ch.bader.budget.domain.RealAccount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RealAccountService {

    @Inject
    RealAccountRepository realAccountRepository;

    public RealAccount addRealAccount(final RealAccount account) {
        return realAccountRepository.addRealAccount(account);
    }

    public RealAccount getAccountById(final String id) {
        return realAccountRepository.getAccountById(id);
    }

    public RealAccount updateRealAccount(final RealAccount account) {
        return realAccountRepository.updateRealAccount(account);
    }
}
