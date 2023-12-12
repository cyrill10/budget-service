package ch.bader.budget.boundary;

import ch.bader.budget.core.balance.YearMonthBalanceCalculator;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.YearMonth;

@Path("/budget/yearMonthBalance/")
@Produces(MediaType.APPLICATION_JSON)
public class YearMonthBalanceRestResource {

    @Inject
    YearMonthBalanceCalculator yearMonthBalanceCalculator;

    @POST
    public void recalculateYearMonthBalance(@RestQuery final Integer year, @RestQuery final Integer month) {
        yearMonthBalanceCalculator.recalculateYearMonthBalance(YearMonth.of(year, month).atDay(1));
    }
}
