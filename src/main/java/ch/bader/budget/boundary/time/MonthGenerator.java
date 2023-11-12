package ch.bader.budget.boundary.time;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MonthGenerator {

    private final String startdate = "2022-12-01";

    public LocalDate getStartDate() {
        return LocalDate.parse(startdate);
    }

    public List<LocalDate> getallMonths() {
        LocalDate today = LocalDate.now();
        LocalDate todayInAYear = today.plusYears(1).withDayOfMonth(1);

        List<LocalDate> allMonths = new ArrayList<>();
        LocalDate startDate = getStartDate();
        while (startDate.isBefore(todayInAYear)) {
            allMonths.add(startDate);
            startDate = startDate.plusMonths(1);
        }
        return allMonths;
    }
}
