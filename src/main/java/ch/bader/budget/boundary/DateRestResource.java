package ch.bader.budget.boundary;

import ch.bader.budget.boundary.dto.mapper.LocalDateMapper;
import ch.bader.budget.boundary.time.MonthGenerator;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.ZonedDateTime;
import java.util.List;

@Path("/budget/date/month/list")
@Produces(MediaType.APPLICATION_JSON)
public class DateRestResource {


    @Inject
    MonthGenerator monthGenerator;

    @Inject
    LocalDateMapper localDateMapper;

    public DateRestResource(final MonthGenerator monthGenerator) {
        this.monthGenerator = monthGenerator;
    }

    @GET
    public List<ZonedDateTime> getAllMonths() {
        return monthGenerator.getAllMonths().stream().map(localDateMapper::fromDomain).toList();
    }
}
