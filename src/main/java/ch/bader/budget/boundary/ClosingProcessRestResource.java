package ch.bader.budget.boundary;


import ch.bader.budget.boundary.dto.ClosingProcessBoundaryDto;
import ch.bader.budget.boundary.dto.SaveScannedTransactionBoundaryDto;
import ch.bader.budget.boundary.dto.ScannedTransactionBoundaryDto;
import ch.bader.budget.boundary.dto.TransferDetailBoundaryDto;
import ch.bader.budget.boundary.dto.mapper.ClosingProcessBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.ScannedTransactionBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.TransferDetailBoundaryDtoMapper;
import ch.bader.budget.core.service.ClosingProcessService;
import ch.bader.budget.domain.ClosingProcess;
import ch.bader.budget.domain.ScannedTransaction;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

@Path("/budget/closingProcess")
@Produces(MediaType.APPLICATION_JSON)
public class ClosingProcessRestResource {

    @Inject
    ClosingProcessService closingProcessService;

    @Inject
    ClosingProcessBoundaryDtoMapper closingProcessBoundaryDtoMapper;

    @Inject
    ScannedTransactionBoundaryDtoMapper scannedTransactionBoundaryDtoMapper;

    @Inject
    TransferDetailBoundaryDtoMapper transferDetailBoundaryDtoMapper;


    @GET
    public ClosingProcessBoundaryDto getClosingProcess(@RestQuery final Integer year, @RestQuery final Integer month) {
        final YearMonth yearMonth = YearMonth.of(year, month + 1);
        final ClosingProcess closingProcess = closingProcessService.getClosingProcess(yearMonth);
        return closingProcessBoundaryDtoMapper.mapToDto(closingProcess);

    }

    @POST
    @Path("closeFileUpload")
    public ClosingProcessBoundaryDto closeFileUpload(@RestQuery final Integer year, @RestQuery final Integer month) {
        final YearMonth yearMonth = YearMonth.of(year, month + 1);
        final ClosingProcess closingProcess = closingProcessService.closeFileUpload(yearMonth);
        return closingProcessBoundaryDtoMapper.mapToDto(closingProcess);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public RestResponse<List<ScannedTransactionBoundaryDto>> uploadFile(
        @RestQuery final Integer year,
        @RestQuery final Integer month,
        @RestForm final FileUpload file) throws IOException {
        final YearMonth yearMonth = YearMonth.of(year, month + 1);
        final List<ScannedTransaction> scannedTransactions = closingProcessService.uploadFile(yearMonth, file);
        if (scannedTransactions != null) {
            return RestResponse.ok(scannedTransactions
                .stream()
                .map(scannedTransactionBoundaryDtoMapper::mapToDto)
                .toList());
        }
        return RestResponse.status(RestResponse.Status.EXPECTATION_FAILED);
    }

    @GET
    @Path("/transactions")
    public List<ScannedTransactionBoundaryDto> getTransactions(
        @RestQuery final Integer year,
        @RestQuery final Integer month) {
        final YearMonth yearMonth = YearMonth.of(year, month + 1);
        return closingProcessService
            .getTransactions(yearMonth)
            .stream()
            .map(scannedTransactionBoundaryDtoMapper::mapToDto).toList();
    }

    @POST
    @ResponseStatus(200)
    @Path("/transactions")
    public void saveScannedTransactions(final SaveScannedTransactionBoundaryDto dto) {
        closingProcessService.saveScannedTransactions(dto);
    }

    @GET
    @Path("/transfer/details")
    public List<TransferDetailBoundaryDto> getTransferDetails(@RestQuery final Integer year,
                                                              @RestQuery final Integer month) {
        final YearMonth yearMonth = YearMonth.of(year, month + 1);
        return closingProcessService
            .getTransferDetails(yearMonth)
            .stream()
            .map(transferDetailBoundaryDtoMapper::mapToDto)
            .toList();

    }

    @POST
    @ResponseStatus(200)
    @Path("/transfer/close")
    public ClosingProcessBoundaryDto closeTransfer(@RestQuery final Integer year, @RestQuery final Integer month) {
        final YearMonth yearMonth = YearMonth.of(year, month + 1);
        final ClosingProcess closingProcess = closingProcessService.closeTransfer(yearMonth);
        return closingProcessBoundaryDtoMapper.mapToDto(closingProcess);
    }
}
