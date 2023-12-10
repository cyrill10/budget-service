package ch.bader.budget.adapter.repository.mongo;

import ch.bader.budget.adapter.entity.ClosingProcessAdapterDbo;
import ch.bader.budget.adapter.entity.mapper.ClosingProcessAdapterDboMapper;
import ch.bader.budget.adapter.entity.mapper.ClosingProcessStatusAdapterDboMapper;
import ch.bader.budget.adapter.repository.ClosingProcessRepository;
import ch.bader.budget.domain.ClosingProcess;
import ch.bader.budget.type.ClosingProcessStatus;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.YearMonth;
import java.util.Optional;

@ApplicationScoped
public class ClosingProcessRepositoryImpl implements ClosingProcessRepository, PanacheMongoRepository<ClosingProcessAdapterDbo> {

    @Inject
    ClosingProcessAdapterDboMapper closingProcessAdapterDboMapper;

    @Inject
    ClosingProcessStatusAdapterDboMapper closingProcessStatusAdapterDboMapper;

    @Override
    public ClosingProcess getClosingProcessByYearMonth(final YearMonth yearMonth) {
        final ClosingProcessAdapterDbo result = getClosingProcessAdapterDboByYearMonth(yearMonth);
        return closingProcessAdapterDboMapper.mapToDomain(result);
    }

    @Override
    public ClosingProcess closeTransferStatus(final YearMonth yearMonth) {
        final ClosingProcessAdapterDbo dbo = getClosingProcessAdapterDboByYearMonth(yearMonth);
        dbo.setTransferStatus(closingProcessStatusAdapterDboMapper.mapToDbo(ClosingProcessStatus.DONE));
        update(dbo);
        return closingProcessAdapterDboMapper.mapToDomain(dbo);
    }

    @Override
    public ClosingProcess closeFileUploadStatus(final YearMonth yearMonth) {
        final ClosingProcessAdapterDbo dbo = getClosingProcessAdapterDboByYearMonth(yearMonth);
        dbo.setUploadStatus(closingProcessStatusAdapterDboMapper.mapToDbo(ClosingProcessStatus.DONE));
        update(dbo);
        return closingProcessAdapterDboMapper.mapToDomain(dbo);
    }

    @Override
    public ClosingProcessAdapterDbo getClosingProcessAdapterDboByYearMonth(final YearMonth yearMonth) {
        final Optional<ClosingProcessAdapterDbo> dboOptional = find(ClosingProcessAdapterDbo.Fields.yearMonth,
            yearMonth.toString()).firstResultOptional();
        return dboOptional.orElseGet(() -> {
            final ClosingProcess newClosingProcess = ClosingProcess.builder()
                                                                   .yearMonth(yearMonth)
                                                                   .manualEntryStatus(ClosingProcessStatus.NEW)
                                                                   .uploadStatus(ClosingProcessStatus.NEW)
                                                                   .transferStatus(ClosingProcessStatus.NEW)
                                                                   .build();
            final ClosingProcessAdapterDbo dbo = closingProcessAdapterDboMapper.mapToDbo(newClosingProcess);
            persist(dbo);
            return dbo;
        });
    }

    @Override
    public void updateClosingProcess(final ClosingProcess closingProcess) {
        update(closingProcessAdapterDboMapper.mapToDbo(closingProcess));
    }
}
