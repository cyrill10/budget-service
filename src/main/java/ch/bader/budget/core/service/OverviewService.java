package ch.bader.budget.core.service;

import ch.bader.budget.domain.OverviewElement;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class OverviewService {
    public List<OverviewElement> getAllTransactions(LocalDate localDate) {
        return null;
    }
}
