package ch.bader.budget.adapter.repository.mongo;

import ch.bader.budget.adapter.entity.RealAccountAdapterDbo;
import ch.bader.budget.adapter.entity.mapper.RealAccountAdapterDboMapper;
import ch.bader.budget.adapter.repository.RealAccountRepository;
import ch.bader.budget.domain.RealAccount;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class RealAccountRepositoryImpl implements RealAccountRepository, PanacheMongoRepository<RealAccountAdapterDbo> {

    @Inject
    RealAccountAdapterDboMapper realAccountAdapterDboMapper;


    @Override
    public RealAccount getAccountById(final String id) {
        return realAccountAdapterDboMapper.mapToDomain(findById(new ObjectId(id)));
    }

    @Override
    public List<RealAccount> getAll() {
        return streamAll().map(realAccountAdapterDboMapper::mapToDomain).sorted().toList();
    }

    @Override
    public RealAccount addRealAccount(final RealAccount account) {
        final RealAccountAdapterDbo dbo = realAccountAdapterDboMapper.mapToDbo(account);
        persist(dbo);
        return realAccountAdapterDboMapper.mapToDomain(dbo);
    }

    @Override
    public RealAccount updateRealAccount(final RealAccount account) {
        final RealAccountAdapterDbo dbo = realAccountAdapterDboMapper.mapToDbo(account);
        update(dbo);
        return realAccountAdapterDboMapper.mapToDomain(dbo);
    }
}
