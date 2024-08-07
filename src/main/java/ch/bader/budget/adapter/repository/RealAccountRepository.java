package ch.bader.budget.adapter.repository;

import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.type.AccountType;

import java.util.List;

public interface RealAccountRepository {
    RealAccount getAccountById(String id);

    List<RealAccount> getAll();

    RealAccount addRealAccount(RealAccount account);

    RealAccount updateRealAccount(RealAccount account);

    List<RealAccount> getAllByAccountType(List<AccountType> accountTypes);
}
