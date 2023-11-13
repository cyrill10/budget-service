package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.entity.TransactionAdapterDbo;
import ch.bader.budget.adapter.entity.mapper.TransactionAdapterDboMapper;
import ch.bader.budget.domain.Transaction;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class TransactionRepository implements PanacheMongoRepositoryBase<TransactionAdapterDbo, String> {

    @Inject
    TransactionAdapterDboMapper transactionAdapterDboMapper;

    public List<Transaction> findAllByDateBetween(final LocalDate fromExclusive,
                                                  final LocalDate toExclusive) {
        final Document query =
            new Document("date",
                new Document("$gt", fromExclusive).append("$lt", toExclusive));
        return stream(query).map(transactionAdapterDboMapper::mapToDomain).toList();
    }


    public List<Transaction> findAllByDateBetweenAndVirtualAccountId(final LocalDate fromExclusive,
                                                                     final LocalDate toExclusive,
                                                                     final List<String> accountId) {
        final Document query =
            new Document("$and",
                List.of(
                    new Document("date",
                        new Document("$gt", fromExclusive).append("$lt", toExclusive)
                    ),
                    new Document("$or",
                        List.of(
                            new Document("creditedAccountId",
                                new Document("$in", accountId)
                            ),
                            new Document("debitedAccountId",
                                new Document("$in", accountId)
                            )
                        )
                    )
                )
            );
        return stream(query).map(transactionAdapterDboMapper::mapToDomain).toList();
    }
}
