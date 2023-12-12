package ch.bader.budget.adapter.repository.mongo;

import ch.bader.budget.adapter.entity.TransactionAdapterDbo;
import ch.bader.budget.adapter.entity.mapper.TransactionAdapterDboMapper;
import ch.bader.budget.adapter.repository.TransactionRepository;
import ch.bader.budget.adapter.repository.VirtualAccountRepository;
import ch.bader.budget.boundary.time.MonthGenerator;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.VirtualAccount;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class TransactionRepositoryImpl implements TransactionRepository, PanacheMongoRepository<TransactionAdapterDbo> {

    @Inject
    TransactionAdapterDboMapper transactionAdapterDboMapper;

    @Inject
    VirtualAccountRepository virtualAccountRepository;

    @Inject
    MonthGenerator monthGenerator;

    @Override
    public void saveTransactions(final List<Transaction> transactionList) {
        final List<TransactionAdapterDbo> newEntites = transactionList
            .stream()
            .map(transactionAdapterDboMapper::mapToDbo)
            .collect(Collectors.toList());
        persist(newEntites);
    }

    @Override
    public List<Transaction> findAllByDateBetween(final LocalDate fromExclusive, final LocalDate toExclusive) {
        final Document query = new Document("date",
            new Document("$gt", fromExclusive.toString()).append("$lt", toExclusive.toString()));
        return transformAndAddAccounts(stream(query));
    }

    @Override
    public List<Transaction> findAllByDateBetweenAndVirtualAccountId(final LocalDate fromExclusive,
                                                                     final LocalDate toExclusive,
                                                                     final List<String> accountIds) {
        final Document query = new Document("$and",
            List.of(new Document("date",
                    new Document("$gt", fromExclusive.toString()).append("$lt", toExclusive.toString())),
                new Document("$or",
                    List.of(new Document("creditedAccountId", new Document("$in", accountIds)),
                        new Document("debitedAccountId", new Document("$in", accountIds))))));
        return transformAndAddAccounts(stream(query));
    }

    @Override
    public Transaction addTransaction(final Transaction transaction) {
        final TransactionAdapterDbo transactionDbo = transactionAdapterDboMapper.mapToDbo(transaction);
        persist(transactionDbo);
        final Transaction result = transactionAdapterDboMapper.mapToDomain(transactionDbo);
        result.setCreditedAccount(transaction.getCreditedAccount());
        result.setDebitedAccount(transaction.getDebitedAccount());
        return result;
    }

    @Override
    public Transaction updateTransaction(final Transaction transaction) {
        final TransactionAdapterDbo transactionDbo = transactionAdapterDboMapper.mapToDbo(transaction);
        update(transactionDbo);
        final Transaction result = transactionAdapterDboMapper.mapToDomain(transactionDbo);
        result.setCreditedAccount(transaction.getCreditedAccount());
        result.setDebitedAccount(transaction.getDebitedAccount());
        return result;
    }

    @Override
    public void deleteTransaction(final String transactionId) {
        deleteById(new ObjectId(transactionId));
    }

    @Override
    public Transaction getTransactionById(final String id) {
        final Optional<TransactionAdapterDbo> optionalDbo = findByIdOptional(new ObjectId(id));
        return optionalDbo.map(dbo -> {
            final VirtualAccount creditedAccount = virtualAccountRepository.getAccountById(dbo.getCreditedAccountId());
            final VirtualAccount debitedAccount = virtualAccountRepository.getAccountById(dbo.getDebitedAccountId());
            final Transaction transaction = transactionAdapterDboMapper.mapToDomain(dbo);
            transaction.setCreditedAccount(creditedAccount);
            transaction.setDebitedAccount(debitedAccount);
            return transaction;
        }).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Transaction> getAllTransactionsForMonth(final YearMonth month) {
        return findAllByDateBetween(month.minusMonths(1).atEndOfMonth(), month.plusMonths(1).atDay(1));
    }

    @Override
    public List<Transaction> getAllTransactionsForVirtualAccountsUntilDate(final List<VirtualAccount> virtualAccounts,
                                                                           final LocalDate unitlExclusive) {
        return findAllByDateBetweenAndVirtualAccountId(monthGenerator.getStartDate().minusDays(1),
            unitlExclusive,
            virtualAccounts.stream().map(VirtualAccount::getId).toList());
    }

    @Override
    public List<Transaction> getAllTransactionsForVirtualAccountUntilDate(final String virtualAccountId,
                                                                          final LocalDate unitlExclusive) {
        return findAllByDateBetweenAndVirtualAccountId(monthGenerator.getStartDate().minusDays(1),
            unitlExclusive,
            List.of(virtualAccountId));
    }

    @Override
    public List<Transaction> getAllTransactionsUntil(final YearMonth month) {
        return findAllByDateBetween(monthGenerator.getStartDate().minusDays(1), month.plusMonths(1).atDay(1));
    }

    private List<Transaction> transformAndAddAccounts(final Stream<TransactionAdapterDbo> dbos) {
        final Map<String, VirtualAccount> virtualAccounts = virtualAccountRepository.getAllVirtualAccountsWithTheirUnderlyingAccountAsMap();

        return dbos.map(dbo -> transformTransaction(dbo, virtualAccounts)).sorted().toList();

    }

    private Transaction transformTransaction(final TransactionAdapterDbo dbo,
                                             final Map<String, VirtualAccount> virtualAccounts) {
        final Transaction transaction = transactionAdapterDboMapper.mapToDomain(dbo);
        transaction.setCreditedAccount(virtualAccounts.get(dbo.getCreditedAccountId()));
        transaction.setDebitedAccount(virtualAccounts.get(dbo.getDebitedAccountId()));
        return transaction;
    }
}
