package ch.bader.budget.boundary;

import ch.bader.budget.boundary.dto.AccountElementBoundaryDto;
import ch.bader.budget.boundary.dto.RealAccountBoundaryDto;
import ch.bader.budget.boundary.dto.ValueEnumBoundaryDto;
import ch.bader.budget.boundary.dto.mapper.AccountTypeBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.RealAccountBoundaryDtoMapper;
import ch.bader.budget.boundary.dto.mapper.VirtualAccountBoundaryDtoMapper;
import ch.bader.budget.core.service.RealAccountService;
import ch.bader.budget.core.service.VirtualAccountService;
import ch.bader.budget.domain.RealAccount;
import ch.bader.budget.domain.VirtualAccount;
import ch.bader.budget.type.AccountType;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Path("/budget/realAccount/")
@Produces(MediaType.APPLICATION_JSON)
public class RealAccountRestResource {

    @Inject
    RealAccountService realAccountService;

    @Inject
    VirtualAccountService virtualAccountService;

    @Inject
    RealAccountBoundaryDtoMapper realAccountBoundaryDtoMapper;

    @Inject
    VirtualAccountBoundaryDtoMapper virtualAccountBoundaryDtoMapper;

    @Inject
    AccountTypeBoundaryDtoMapper accountTypeBoundaryDtoMapper;

    @POST
    @ResponseStatus(201)
    @Path("/add")
    public RealAccountBoundaryDto addNewAccount(final RealAccountBoundaryDto accountDto) {
        RealAccount account = realAccountBoundaryDtoMapper.mapToDomain(accountDto);
        account = realAccountService.addRealAccount(account);
        return realAccountBoundaryDtoMapper.mapToDto(account);

    }

    @GET
    public RealAccountBoundaryDto getAccount(@RestQuery final String id) {
        final RealAccount result = realAccountService.getAccountById(id);
        return realAccountBoundaryDtoMapper.mapToDto(result);
    }

    @GET
    @Path("/list")
    public List<AccountElementBoundaryDto> getAllAccounts() {
        final Map<RealAccount, List<VirtualAccount>> map = virtualAccountService.getAccountMap();
        return map
            .entrySet()
            .stream()
            .map(entry -> new AccountElementBoundaryDto(realAccountBoundaryDtoMapper.mapToDto(entry.getKey()),
                entry.getValue().stream().map(virtualAccountBoundaryDtoMapper::mapToDto).toList()))
            .toList();
    }


    @PUT
    @Path("/update")
    public RealAccountBoundaryDto updateAccount(final RealAccountBoundaryDto dto) {
        RealAccount account = realAccountBoundaryDtoMapper.mapToDomain(dto);
        account = realAccountService.updateRealAccount(account);
        return realAccountBoundaryDtoMapper.mapToDto(account);
    }

    @GET
    @Path("/type/list")
    public List<ValueEnumBoundaryDto> getAllAccountTypes() {
        return Arrays.stream(AccountType.values()).map(accountTypeBoundaryDtoMapper::mapToDto).toList();
    }
}
