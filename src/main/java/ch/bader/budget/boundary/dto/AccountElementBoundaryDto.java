package ch.bader.budget.boundary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class AccountElementBoundaryDto {

    private RealAccountBoundaryDto realAccount;
    private List<VirtualAccountBoundaryDto> virtualAccounts;
}
