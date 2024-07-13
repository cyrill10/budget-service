package ch.bader.budget.boundary;

import ch.bader.budget.boundary.dto.AccountStatisticsBoundaryDto;
import ch.bader.budget.boundary.dto.AccountStatisticsRequestBoundaryDto;
import ch.bader.budget.boundary.dto.mapper.AccountStatisticsBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.AccountStatisticsRequestBoundaryDtoMapper;
import ch.bader.budget.core.service.AccountStatisticsService;
import ch.bader.budget.domain.statistics.AccountStatistics;
import ch.bader.budget.domain.statistics.AccountStatisticsRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/budget/stats")
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsRestResource {

    @Inject
    AccountStatisticsBoundaryDtoMapper accountStatisticsBoundaryDtoMapper;

    @Inject
    AccountStatisticsRequestBoundaryDtoMapper accountStatisticsRequestBoundaryDtoMapper;

    @Inject
    AccountStatisticsService accountStatisticsService;

    @GET
    public AccountStatisticsBoundaryDto getStatistics(final AccountStatisticsRequestBoundaryDto accountStatisticsRequestBoundaryDto) {
        final AccountStatisticsRequest accountStatisticsRequest = accountStatisticsRequestBoundaryDtoMapper.mapFromBoundaryDto(
            accountStatisticsRequestBoundaryDto);
        final AccountStatistics result = accountStatisticsService.getAccountStatistics(accountStatisticsRequest);
        return accountStatisticsBoundaryDtoMapper.mapToBoundaryDto(result);
    }
}
