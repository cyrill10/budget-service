package ch.bader.budget.domain;

import ch.bader.budget.type.AccountType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RealAccount implements Comparable<RealAccount> {
    private String id;
    private String name;
    private AccountType accountType;

    public boolean isPrebudgetedAccount() {
        return getAccountType().isPrebudgetedAccount();
    }

    @Override
    public int compareTo(RealAccount o) {
        int compareType = getAccountType().getValue().compareTo(o.getAccountType().getValue());
        if (compareType == 0) {
            return getName().compareTo(o.getName());
        }
        return compareType;
    }

    public boolean isAlienAccount() {
        return getAccountType().isAlienAccount();
    }
}
