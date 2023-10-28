package ch.bader.budget.boundary;


import ch.bader.budget.boundary.dto.ClosingProcessBoundaryDto;
import ch.bader.budget.boundary.dto.SaveScannedTransactionBoundaryDto;
import ch.bader.budget.boundary.dto.ScannedTransactionBoundaryDto;
import ch.bader.budget.boundary.dto.TransferDetailBoundaryDto;
import ch.bader.budget.boundary.dto.mapper.ClosingProcessBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.ScannedTransactionBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.TransferDetailBoundaryDtoMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

@Path("/budget/closingProcess")
@Produces(MediaType.APPLICATION_JSON)
public class ClosingProcessRestResource {

    @Inject
    final ClosingProcessService closingProcessService;

    @Inject
    final ClosingProcessBoundaryDtoMapper closingProcessBoundaryDtoMapper;

    @Inject
    final ScannedTransactionBoundaryDtoMapper scannedTransactionBoundaryDtoMapper;

    @Inject
    final TransferDetailBoundaryDtoMapper transferDetailBoundaryDtoMapper;


    @GET
    public ClosingProcessBoundaryDto getClosingProcess(@RestQuery Integer year, @RestQuery Integer month) {
        return ClosingProcessBoundaryDto.builder().build();
    }

    @POST
    @Path("closeFileUpload")
    public ClosingProcessBoundaryDto closeFileUpload(@RestQuery Integer year, @RestQuery Integer month) {
        return ClosingProcessBoundaryDto.builder().build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public List<ScannedTransactionBoundaryDto> uploadFile(
        @RestQuery Integer year,
        @RestQuery Integer month,
        @RestForm FileUpload file) {
        return List.of(ScannedTransactionBoundaryDto.builder().build());

    }

    @GET
    @Path("/transactions")
    public List<ScannedTransactionBoundaryDto> getTransactions(
        @RestQuery Integer year,
        @RestQuery Integer month) {
        return List.of(ScannedTransactionBoundaryDto.builder().build());
    }

    @POST
    @Path("/transactions")
    public void saveScannedTransactions(SaveScannedTransactionBoundaryDto dto) {
    }

    @GET
    @Path("/transfer/details")
    public List<TransferDetailBoundaryDto> getTransferDetails(@RestQuery Integer year,
                                                              @RestQuery Integer month) {
        return List.of(TransferDetailBoundaryDto.builder().build());
    }

    @POST
    @Path("/transfer/close")
    public ClosingProcessBoundaryDto closeTransfer(@RestQuery Integer year, @RestQuery Integer month) {
        return ClosingProcessBoundaryDto.builder().build();
    }
}
