package ch.bader.budget.boundary;

import ch.bader.budget.boundary.dto.AccountStatisticsBoundaryDto;
import ch.bader.budget.boundary.dto.AccountStatisticsRequestBoundaryDto;
import ch.bader.budget.boundary.dto.InsightsBoundaryDto;
import ch.bader.budget.boundary.dto.InsightsRequestBoundaryDto;
import ch.bader.budget.boundary.dto.mapper.AccountStatisticsBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.AccountStatisticsRequestBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.InsightsBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.InsightsRequestBoundaryDtoMapper;
import ch.bader.budget.core.service.AccountStatisticsService;
import ch.bader.budget.core.service.insights.CalculateInsightsService;
import ch.bader.budget.domain.statistics.AccountStatistics;
import ch.bader.budget.domain.statistics.AccountStatisticsRequest;
import ch.bader.budget.domain.statistics.Insights;
import ch.bader.budget.domain.statistics.InsightsRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/budget/insights")
@Produces(MediaType.APPLICATION_JSON)
public class InsightsRestResource {

    @Inject
    AccountStatisticsBoundaryDtoMapper accountStatisticsBoundaryDtoMapper;

    @Inject
    AccountStatisticsRequestBoundaryDtoMapper accountStatisticsRequestBoundaryDtoMapper;

    @Inject
    InsightsRequestBoundaryDtoMapper insightsRequestBoundaryDtoMapper;

    @Inject
    InsightsBoundaryDtoMapper insightsBoundaryDtoMapper;

    @Inject
    AccountStatisticsService accountStatisticsService;

    @Inject
    CalculateInsightsService calculateInsightsService;

    @POST
    @Path("/account")
    public AccountStatisticsBoundaryDto getAccountStatistics(final AccountStatisticsRequestBoundaryDto accountStatisticsRequestBoundaryDto) {
        final AccountStatisticsRequest accountStatisticsRequest = accountStatisticsRequestBoundaryDtoMapper.mapFromBoundaryDto(
            accountStatisticsRequestBoundaryDto);
        final AccountStatistics result = accountStatisticsService.getAccountStatistics(accountStatisticsRequest);
        return accountStatisticsBoundaryDtoMapper.mapToBoundaryDto(result);
    }

    @POST
    public InsightsBoundaryDto getInsights(final InsightsRequestBoundaryDto insightsRequestBoundaryDto) {
        final InsightsRequest insightsRequest = insightsRequestBoundaryDtoMapper.mapFromBoundaryDto(
            insightsRequestBoundaryDto);
        final Insights result = calculateInsightsService.getInsights(insightsRequest);
        return insightsBoundaryDtoMapper.mapToBoundaryDto(result);
    }
}
