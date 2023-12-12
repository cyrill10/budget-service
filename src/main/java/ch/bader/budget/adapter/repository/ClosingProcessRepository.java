package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.entity.ClosingProcessAdapterDbo;
import ch.bader.budget.domain.ClosingProcess;

import java.time.YearMonth;

public interface ClosingProcessRepository {

    ClosingProcess getClosingProcessByYearMonth(YearMonth yearMonth);

    ClosingProcess closeTransferStatus(YearMonth yearMonth);

    ClosingProcess closeFileUploadStatus(YearMonth yearMonth);

    ClosingProcessAdapterDbo getClosingProcessAdapterDboByYearMonth(YearMonth yearMonth);

    void updateClosingProcess(ClosingProcess closingProcess);
}
