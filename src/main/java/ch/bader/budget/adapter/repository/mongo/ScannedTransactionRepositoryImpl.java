package ch.bader.budget.adapter.repository.mongo;

import ch.bader.budget.adapter.entity.ScannedTransactionAdapterDbo;
import ch.bader.budget.adapter.entity.mapper.ScannedTransactionAdapterDboMapper;
import ch.bader.budget.adapter.repository.ScannedTransactionRepository;
import ch.bader.budget.domain.ScannedTransaction;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.time.YearMonth;
import java.util.List;

@ApplicationScoped
public class ScannedTransactionRepositoryImpl implements ScannedTransactionRepository, PanacheMongoRepository<ScannedTransactionAdapterDbo> {

    @Inject
    ScannedTransactionAdapterDboMapper scannedTransactionAdapterDboMapper;

    @Override
    public List<ScannedTransaction> findAllById(final List<String> ids) {
        final List<ObjectId> objectIds = ids.stream().map(ObjectId::new).toList();
        return stream("_id in ?1", objectIds)
            .map(scannedTransactionAdapterDboMapper::mapToDomain)
            .toList();
    }

    @Override
    public List<ScannedTransaction> findAllByYearMonth(final YearMonth yearMonth) {
        return list("yearMonth", yearMonth.toString())
            .stream()
            .map(scannedTransactionAdapterDboMapper::mapToDomain)
            .toList();
    }

    @Override
    public List<ScannedTransaction> saveAll(final List<ScannedTransaction> scannedTransactions) {
        final List<ScannedTransactionAdapterDbo> dbos = scannedTransactions
            .stream()
            .map(scannedTransactionAdapterDboMapper::mapToDbo).toList();
        persist(dbos);
        return dbos.stream().map(scannedTransactionAdapterDboMapper::mapToDomain).toList();
    }

    @Override
    public List<ScannedTransaction> updateAll(final List<ScannedTransaction> scannedTransactions) {
        final List<ScannedTransactionAdapterDbo> dbos = scannedTransactions
            .stream()
            .map(scannedTransactionAdapterDboMapper::mapToDbo).toList();
        update(dbos);
        return dbos.stream().map(scannedTransactionAdapterDboMapper::mapToDomain).toList();
    }
}
