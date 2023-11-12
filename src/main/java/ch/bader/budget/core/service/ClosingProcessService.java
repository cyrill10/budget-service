package ch.bader.budget.core.service;

import ch.bader.budget.boundary.dto.SaveScannedTransactionBoundaryDto;
import ch.bader.budget.domain.ClosingProcess;
import ch.bader.budget.domain.ScannedTransaction;
import ch.bader.budget.domain.TransferDetails;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.time.YearMonth;
import java.util.List;

@ApplicationScoped
public class ClosingProcessService {
    public ClosingProcess getClosingProcess(YearMonth yearMonth) {
        return null;
    }

    public ClosingProcess closeFileUpload(YearMonth yearMonth) {
        return null;
    }

    public List<ScannedTransaction> uploadFile(YearMonth yearMonth, FileUpload file) {
        return null;
    }

    public List<ScannedTransaction> getTransactions(YearMonth yearMonth) {
        return null;
    }

    public void saveScannedTransactions(SaveScannedTransactionBoundaryDto dto) {
    }

    public List<TransferDetails> getTransferDetails(YearMonth yearMonth) {
        return null;
    }

    public ClosingProcess closeTransfer(YearMonth yearMonth) {
        return null;
    }
}
