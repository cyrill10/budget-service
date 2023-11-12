package ch.bader.budget.boundary;


import ch.bader.budget.boundary.dto.TransactionBoundaryDto;
import ch.bader.budget.boundary.dto.TransactionElementBoundaryDto;
import ch.bader.budget.boundary.dto.mapper.TransactionBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.TransactionElementBoundaryDtoMapper;
import ch.bader.budget.core.service.TransactionService;
import ch.bader.budget.domain.Transaction;
import ch.bader.budget.domain.TransactionListElement;
import ch.bader.budget.type.PaymentStatus;
import ch.bader.budget.type.PaymentType;
import ch.bader.budget.type.TransactionIndication;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Path("/budget/transaction/")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionRestResource {

    @Inject
    TransactionService transactionService;

    @Inject
    TransactionBoundaryDtoMapper transactionBoundaryDtoMapper;

    @Inject
    TransactionElementBoundaryDtoMapper transactionElementBoundaryDtoMapper;


    @POST
    @Path("/add")
    public TransactionBoundaryDto createTransaction(TransactionBoundaryDto dto) {
        if (dto.getCreationDate() == null) {
            dto.setCreationDate(LocalDateTime.now());
        }
        Transaction transaction = transactionBoundaryDtoMapper.mapToDomain(dto);
        transaction = transactionService.updateTransaction(transaction);
        return transactionBoundaryDtoMapper.mapToDto(transaction);
    }

    @PUT
    @Path("/update")
    public TransactionBoundaryDto updateTransaction(TransactionBoundaryDto dto) {
        Transaction transaction = transactionBoundaryDtoMapper.mapToDomain(dto);
        transaction = transactionService.updateTransaction(transaction);
        return transactionBoundaryDtoMapper.mapToDto(transaction);
    }

    @DELETE
    @Path("/delete")
    public void deleteTransaction(@RestQuery String transactionId) {
        transactionService.deleteTransaction(transactionId);
    }

    @POST
    @Path("/dublicate")
    public void duplicateTransaction(TransactionBoundaryDto dto) {
        Transaction transaction = transactionBoundaryDtoMapper.mapToDomain(dto);
        transactionService.duplicateTransaction(transaction);
    }

    @GET
    public RestResponse<TransactionBoundaryDto> getTransactionById(@RestQuery String id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            return RestResponse.ok(transactionBoundaryDtoMapper.mapToDto(transaction));
        } catch (NoSuchElementException e) {
            return RestResponse.status(RestResponse.Status.NOT_FOUND);
        }

    }

    @GET
    @Path("/list")
    public List<TransactionBoundaryDto> getAllTransactions(
        @RestQuery("date") long dateLong) {
        LocalDate date = Instant.ofEpochMilli(dateLong)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .withDayOfMonth(1);
        List<Transaction> transactions = transactionService.getAllTransactions(date);
        return transactions.stream()
                           .map(transactionBoundaryDtoMapper::mapToDto)
                           .collect(Collectors.toList());
    }

    @GET
    @Path("/listByMonthAndVirtualAccount")
    public List<TransactionElementBoundaryDto> getAllTransactionsForMonthAndVirtualAccount(
        @RestQuery("date") long dateLong,
        @RestQuery String accountId) {
        LocalDate date = Instant.ofEpochMilli(dateLong)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .withDayOfMonth(1);
        List<TransactionListElement> transactionListElements =
            transactionService.getAllTransactionsForMonthAndVirtualAccount(
                date,
                accountId);

        return transactionListElements.stream()
                                      .map(transactionElementBoundaryDtoMapper::mapToDto)
                                      .collect(Collectors.toList());
    }

    @GET
    @Path("/listByMonthAndRealAccount")
    public List<TransactionElementBoundaryDto> getAllTransactionsForMonthAndRealAccount(
        @RestQuery("date") long dateLong,
        @RestQuery String accountId) {
        LocalDate date = Instant.ofEpochMilli(dateLong)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .withDayOfMonth(1);
        return transactionService.getAllTransactionsForMonthAndRealAccount(date, accountId)
                                 .stream()
                                 .map(transactionElementBoundaryDtoMapper::mapToDto)
                                 .collect(
                                     Collectors.toList());
    }

    @GET
    @Path("/type/list")
    public List<PaymentType> getAllPaymentTypes() {
        return Arrays.asList(PaymentType.values());
    }

    @GET
    @Path("/indication/list")
    public List<TransactionIndication> getAllIndicationTypes() {
        return Arrays.asList(TransactionIndication.values());
    }

    @GET
    @Path("/status/list")
    public List<PaymentStatus> getAllStatusTypes() {
        return Arrays.asList(PaymentStatus.values());
    }

}
