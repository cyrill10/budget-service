package ch.bader.budget.boundary.time;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MonthGenerator {

    @ConfigProperty(name = "budget.startdate")
    String startDate;

    public LocalDate getStartDate() {
        return LocalDate.parse(startDate);
    }

    public List<LocalDate> getAllMonths() {
        final LocalDate today = LocalDate.now();
        final LocalDate todayInAYear = today.plusYears(1).withDayOfMonth(1);
        return getAllMonths(todayInAYear);
    }

    public List<LocalDate> getAllMonths(final LocalDate endDate) {
        final List<LocalDate> allMonths = new ArrayList<>();
        LocalDate startDate = getStartDate();
        while (startDate.isBefore(endDate)) {
            allMonths.add(startDate);
            startDate = startDate.plusMonths(1);
        }
        return allMonths;
    }
}
