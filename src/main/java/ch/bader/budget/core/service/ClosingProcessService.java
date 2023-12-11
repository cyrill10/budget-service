package ch.bader.budget.core.service;

import ch.bader.budget.adapter.repository.ClosingProcessRepository;
import ch.bader.budget.adapter.repository.ScannedTransactionRepository;
import ch.bader.budget.adapter.repository.TransactionRepository;
import ch.bader.budget.adapter.repository.VirtualAccountRepository;
import ch.bader.budget.boundary.dto.SaveScannedTransactionBoundaryDto;
import ch.bader.budget.core.balance.AccountBalanceService;
import ch.bader.budget.core.process.closing.ScannedTransactionCsvBean;
import ch.bader.budget.domain.Balance;
import ch.bader.budget.domain.ClosingProcess;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.ScannedTransaction;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.TransferDetails;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.type.AccountType;
import ch.bader.budget.type.ClosingProcessStatus;
import ch.bader.budget.type.PaymentStatus;
import ch.bader.budget.type.PaymentType;
import ch.bader.budget.type.TransactionIndication;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClosingProcessService {

    @Inject
    ClosingProcessRepository closingProcessRepository;

    @Inject
    ScannedTransactionRepository scannedTransactionRepository;

    @Inject
    VirtualAccountRepository virtualAccountRepository;

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    AccountBalanceService accountBalanceService;

    public ClosingProcess getClosingProcess(final YearMonth yearMonth) {
        return closingProcessRepository.getClosingProcessByYearMonth(yearMonth);
    }

    public ClosingProcess closeFileUpload(final YearMonth yearMonth) {
        return closingProcessRepository.closeFileUploadStatus(yearMonth);
    }

    public List<ScannedTransaction> uploadFile(final YearMonth yearMonth, final FileUpload file) throws IOException {
        final ClosingProcess closingProcess = closingProcessRepository.getClosingProcessByYearMonth(yearMonth);
        if (closingProcess.getUploadStatus().equals(ClosingProcessStatus.NEW)) {
            final BufferedReader reader = Files.newBufferedReader(file.filePath());
            final List<ScannedTransaction> scannedTransactions = new CsvToBeanBuilder<ScannedTransactionCsvBean>(reader)
                .withType(ScannedTransactionCsvBean.class)
                .build()
                .stream()
                .filter(stb -> !stb.getDescription().contains("IHRE ZAHLUNG – BESTEN DANK"))
                .map(stb -> stb.mapTopScannedTransaction(closingProcess))
                .sorted()
                .collect(Collectors.toList());

            closingProcess.setUploadStatus(ClosingProcessStatus.STARTED);
            closingProcessRepository.updateClosingProcess(closingProcess);
            return scannedTransactionRepository.saveAll(scannedTransactions);
        }
        return List.of();
    }

    public List<ScannedTransaction> getTransactions(final YearMonth yearMonth) {
        return scannedTransactionRepository.findAllByYearMonth(yearMonth);
    }

    public void saveScannedTransactions(final SaveScannedTransactionBoundaryDto dto) {
        final List<ScannedTransaction> scannedTransactions = scannedTransactionRepository
            .findAllById(dto.getTransactionIds())
            .stream()
            .filter(s -> !s.getTransactionCreated())
            .toList();

        if (CollectionUtils.isNotEmpty(scannedTransactions)) {

            final YearMonth yearMonth = scannedTransactions.get(0).getYearMonth();

            final LocalDate transactionDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 10);

            final VirtualAccount creditedAccount = virtualAccountRepository.getAccountById(dto.getCreditedAccountId());
            final VirtualAccount debitedAccount = virtualAccountRepository.getAccountById(dto.getDebitedAccountId());
            VirtualAccount throughAccount = null;

            if (dto.getThroughAccountId() != null) {
                throughAccount = virtualAccountRepository.getAccountById(dto.getThroughAccountId());
            }

            final List<Transaction> transactions = createTransactions(scannedTransactions,
                transactionDate,
                creditedAccount,
                debitedAccount,
                throughAccount);

            scannedTransactionRepository.updateAll(scannedTransactions);
            transactionRepository.saveTransactions(transactions);

        }
    }

    public List<TransferDetails> getTransferDetails(final YearMonth yearMonth) {
        final Map<RealAccount, List<VirtualAccount>> accountMap = virtualAccountRepository.getAccountMap();
        final List<Transaction> allTransactions = transactionRepository.getAllTransactionsUntil(yearMonth);
        return accountMap
            .entrySet()
            .stream()
            .filter(e -> AccountType.SAVING.equals(e.getKey().getAccountType()))
            .map(e -> mapToTransferDetail(e.getKey(), e.getValue(), yearMonth, allTransactions))
            .toList();
    }

    private TransferDetails mapToTransferDetail(final RealAccount realAccount,
                                                final List<VirtualAccount> virtualAccounts,
                                                final YearMonth yearMonth,
                                                final List<Transaction> allTransactions) {
        final Balance balanceLastMonth = accountBalanceService.getBalanceAtYearMonth(realAccount,
            virtualAccounts,
            yearMonth.minusMonths(1),
            allTransactions);
        final Balance balanceThisMonth = accountBalanceService.getBalanceAtYearMonth(realAccount,
            virtualAccounts,
            yearMonth,
            allTransactions);
        return TransferDetails
            .builder()
            .accountName(realAccount.getName())
            .transferAmount(balanceThisMonth.getEffective().subtract(balanceLastMonth.getEffective()))
            .build();
    }

    public ClosingProcess closeTransfer(final YearMonth yearMonth) {
        return closingProcessRepository.closeTransferStatus(yearMonth);
    }


    private List<Transaction> createTransactions(final List<ScannedTransaction> scannedTransactions,
                                                 final LocalDate transactionDate,
                                                 final VirtualAccount creditedAccount,
                                                 final VirtualAccount debitedAccount,
                                                 final VirtualAccount throughAccount) {
        final List<Transaction> transactions;
        if (throughAccount != null) {
            transactions = createTransactionsWithThroughAccount(scannedTransactions,
                creditedAccount,
                debitedAccount,
                throughAccount,
                transactionDate);
        } else {
            transactions = createTransactionsWithoutThroughAccount(scannedTransactions,
                creditedAccount,
                debitedAccount,
                transactionDate);
        }
        return transactions;
    }

    private List<Transaction> createTransactionsWithoutThroughAccount(final List<ScannedTransaction> scannedTransactions,
                                                                      final VirtualAccount creditedAccount,
                                                                      final VirtualAccount debitedAccount,
                                                                      final LocalDate date) {
        return scannedTransactions
            .stream()
            .sorted()
            .map(ScannedTransaction::createTransaction)
            .map(sc -> createTransaction(sc, creditedAccount, debitedAccount, date))
            .toList();


    }

    private List<Transaction> createTransactionsWithThroughAccount(final List<ScannedTransaction> scannedTransactions,
                                                                   final VirtualAccount creditedAccount,
                                                                   final VirtualAccount debitedAccount,
                                                                   final VirtualAccount throughAccount,
                                                                   final LocalDate date) {

        return scannedTransactions
            .stream()
            .sorted()
            .map(ScannedTransaction::createTransaction)
            .map(sc -> createThroughTransactions(sc, creditedAccount, debitedAccount, throughAccount, date))
            .flatMap(List::stream)
            .toList();
    }

    private Transaction createTransaction(final ScannedTransaction scannedTransaction,
                                          final VirtualAccount creditedAccount,
                                          final VirtualAccount debitedAccount,
                                          final LocalDate date) {
        return Transaction
            .builder()
            .date(date)
            .description(scannedTransaction.getDescription())
            .effectiveAmount(scannedTransaction.getAmount())
            .indication(TransactionIndication.EXPECTED)
            .paymentStatus(PaymentStatus.PAID)
            .paymentType(PaymentType.DEPOSIT)
            .creditedAccount(creditedAccount)
            .debitedAccount(debitedAccount)
            .budgetedAmount(BigDecimal.ZERO)
            .creationDate(LocalDateTime.now())
            .build();
    }

    private List<Transaction> createThroughTransactions(final ScannedTransaction scannedTransaction,
                                                        final VirtualAccount creditedAccount,
                                                        final VirtualAccount debitedAccount,
                                                        final VirtualAccount throughAccount,
                                                        final LocalDate date) {
        final Transaction t1 = createTransaction(scannedTransaction, creditedAccount, throughAccount, date);
        final Transaction t2 = createTransaction(scannedTransaction, throughAccount, debitedAccount, date);

        return List.of(t1, t2);
    }
}
