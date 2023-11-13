package ch.bader.budget.adapter.repository;

import ch.bader.budget.adapter.entity.ClosingProcessAdapterDbo;
import ch.bader.budget.adapter.entity.mapper.ClosingProcessAdapterDboMapper;
import ch.bader.budget.domain.ClosingProcess;
import ch.bader.budget.type.ClosingProcessStatus;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.YearMonth;
import java.util.Optional;

@ApplicationScoped
public class ClosingProcessRepository implements PanacheMongoRepository<ClosingProcessAdapterDbo> {

    @Inject
    ClosingProcessAdapterDboMapper closingProcessAdapterDboMapper;

    public ClosingProcess getClosingProcessByYearMonth(final YearMonth yearMonth) {
        final Optional<ClosingProcessAdapterDbo> dboOptional = find("yearMonth",
            yearMonth.toString()).firstResultOptional();
        final ClosingProcessAdapterDbo result = dboOptional.orElseGet(() -> {
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
        return closingProcessAdapterDboMapper.mapToDomain(result);
    }
}
