package ch.bader.budget.adapter.repository.mongo;

import ch.bader.budget.adapter.entity.VirtualAccountAdapterDbo;
import ch.bader.budget.adapter.entity.mapper.VirtualAccountAdapterDboMapper;
import ch.bader.budget.adapter.repository.RealAccountRepository;
import ch.bader.budget.adapter.repository.VirtualAccountRepository;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.VirtualAccount;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class VirtualAccountRepositoryImpl implements VirtualAccountRepository, PanacheMongoRepository<VirtualAccountAdapterDbo> {

    @Inject
    VirtualAccountAdapterDboMapper virtualAccountAdapterDboMapper;

    @Inject
    RealAccountRepository realAccountRepository;

    @Override
    public VirtualAccount getAccountById(final String id) {
        final VirtualAccountAdapterDbo dbo = findById(new ObjectId(id));
        final VirtualAccount virtualAccount = virtualAccountAdapterDboMapper.mapToDomain(dbo);
        return addRealAccountToVirtualAccount(virtualAccount, dbo.getUnderlyingAccountId());

    }

    @Override
    public VirtualAccount addVirtualAccount(final VirtualAccount virtualAccount) {
        final VirtualAccountAdapterDbo dbo = virtualAccountAdapterDboMapper.mapToDbo(virtualAccount);
        persist(dbo);
        final VirtualAccount virtualAccountSaved = virtualAccountAdapterDboMapper.mapToDomain(dbo);
        virtualAccountSaved.setUnderlyingAccount(virtualAccount.getUnderlyingAccount());
        return virtualAccountSaved;
    }

    @Override
    public VirtualAccount updateVirtualAccount(final VirtualAccount virtualAccount) {
        final VirtualAccountAdapterDbo dbo = virtualAccountAdapterDboMapper.mapToDbo(virtualAccount);
        update(dbo);
        final VirtualAccount virtualAccountSaved = virtualAccountAdapterDboMapper.mapToDomain(dbo);
        virtualAccountSaved.setUnderlyingAccount(virtualAccount.getUnderlyingAccount());
        return virtualAccountSaved;
    }

    @Override
    public Map<String, VirtualAccount> getAllVirtualAccountsWithTheirUnderlyingAccountAsMap() {
        final List<VirtualAccountAdapterDbo> virtualAccountDbos = listAll();
        final Map<String, RealAccount> realAccounts = realAccountRepository
            .getAll()
            .stream()
            .collect(toMap(RealAccount::getId, Function.identity()));

        return virtualAccountDbos.stream().map(dbo -> {
            final VirtualAccount va = virtualAccountAdapterDboMapper.mapToDomain(dbo);
            va.setUnderlyingAccount(realAccounts.get(dbo.getUnderlyingAccountId()));
            return va;
        }).sorted().collect(toMap(VirtualAccount::getId, Function.identity(), (u, v) -> u, LinkedHashMap::new));
    }

    @Override
    public List<VirtualAccount> getAllVirtualAccountsWithTheirUnderlyingAccount() {
        return getAllVirtualAccountsWithTheirUnderlyingAccountAsMap().values().stream().toList();
    }

    @Override
    public Map<RealAccount, List<VirtualAccount>> getAccountMap() {
        final List<RealAccount> realAccounts = realAccountRepository.getAll();

        final Map<String, List<VirtualAccountAdapterDbo>> virtualAccountByReallAccountId = listAll()
            .stream()
            .collect(groupingBy(VirtualAccountAdapterDbo::getUnderlyingAccountId));

        return realAccounts
            .stream()
            .collect(toMap(Function.identity(), r -> virtualAccountByReallAccountId.get(r.getId()).stream().map(dbo -> {
                final VirtualAccount v = virtualAccountAdapterDboMapper.mapToDomain(dbo);
                v.setUnderlyingAccount(r);
                return v;
            }).sorted().toList(), (u, v) -> u, LinkedHashMap::new));
    }

    @Override
    public List<VirtualAccount> getAccountsByRealAccount(final RealAccount realAccount) {
        return stream("underlyingAccountId = ?1", realAccount.getId()).map(v -> {
            final VirtualAccount virtualAccount = virtualAccountAdapterDboMapper.mapToDomain(v);
            virtualAccount.setUnderlyingAccount(realAccount);
            return virtualAccount;
        }).toList();
    }

    @Override
    public List<VirtualAccount> getAccountsByRealAccounts(final List<RealAccount> realAccounts) {
        return getAccountMap()
            .entrySet()
            .stream()
            .filter(entry -> realAccounts.contains(entry.getKey()))
            .map(Map.Entry::getValue)
            .flatMap(Collection::stream)
            .toList();
    }

    private VirtualAccount addRealAccountToVirtualAccount(final VirtualAccount virtualAccount,
                                                          final String realAccountId) {
        virtualAccount.setUnderlyingAccount(realAccountRepository.getAccountById(realAccountId));
        return virtualAccount;
    }
}
