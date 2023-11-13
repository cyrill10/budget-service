package ch.bader.budget.boundary;

import ch.bader.budget.boundary.dto.VirtualAccountBoundaryDto;
import ch.bader.budget.boundary.dto.mapper.VirtualAccountBoundaryDtoMapper;
import ch.bader.budget.core.service.VirtualAccountService;
import ch.bader.budget.domain.VirtualAccount;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.math.BigDecimal;
import java.util.List;

@Path("/budget/virtualAccount/")
@Produces(MediaType.APPLICATION_JSON)
public class VirtualAccountRestResource {

    @Inject
    VirtualAccountService virtualAccountService;

    @Inject
    VirtualAccountBoundaryDtoMapper virtualAccountBoundaryDtoMapper;


    @POST
    @Path("/add")
    public VirtualAccountBoundaryDto addNewAccount(final VirtualAccountBoundaryDto dto) {
        VirtualAccount virtualAccount = virtualAccountBoundaryDtoMapper.mapToDomain(dto);
        virtualAccount.setBalance(BigDecimal.ZERO);
        virtualAccount.setIsDeleted(Boolean.FALSE);
        virtualAccount = virtualAccountService.updateVirtualAccount(virtualAccount);
        return virtualAccountBoundaryDtoMapper.mapToDto(virtualAccount);
    }

    @PUT
    @Path("/update")
    public VirtualAccountBoundaryDto updateAccount(final VirtualAccountBoundaryDto dto) {
        VirtualAccount virtualAccount = virtualAccountBoundaryDtoMapper.mapToDomain(dto);
        virtualAccount = virtualAccountService.updateVirtualAccount(virtualAccount);
        return virtualAccountBoundaryDtoMapper.mapToDto(virtualAccount);
    }

    @GET
    @Path("/")
    public VirtualAccountBoundaryDto getAccountById(final String id) {
        final VirtualAccount virtualAccount = virtualAccountService.getAccountById(id);
        return virtualAccountBoundaryDtoMapper.mapToDto(virtualAccount);
    }

    @GET
    @Path("/list")
    public List<VirtualAccountBoundaryDto> getAllAccounts() {
        final List<VirtualAccount> accounts = virtualAccountService.getAllVirtualAccounts();
        return accounts.stream().map(virtualAccountBoundaryDtoMapper::mapToDto).toList();
    }
}
