package ch.bader.budget.boundary;

import ch.bader.budget.boundary.dto.OverviewElementBoundaryDto;
import ch.bader.budget.boundary.dto.mapper.OverviewElementBoundaryDtoMapper;
import ch.bader.budget.core.service.OverviewService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Path("/budget/overview/list")
@Produces(MediaType.APPLICATION_JSON)
public class OverviewRestResource {

    @Inject
    OverviewElementBoundaryDtoMapper overviewElementBoundaryDtoMapper;

    @Inject
    OverviewService overviewService;

    @GET
    public List<OverviewElementBoundaryDto> getAllTransactions(@RestQuery final long dateLong) {
        final YearMonth month = YearMonth.from(Instant
            .ofEpochMilli(dateLong)
            .atZone(ZoneId.systemDefault())
            .toLocalDate());
        return overviewService
            .getAllTransactions(month)
            .stream()
            .map(overviewElementBoundaryDtoMapper::mapToDto)
            .toList();
    }
}
