package ch.bader.budget.type;

import java.util.Arrays;

public enum AccountType implements ValueEnum<Integer> {

    CHECKING(1, "Checking"), SAVING(2, "Saving"), CREDIT(3, "Credit"), ALIEN(4, "Alien"),
    PREBUDGETED(5, "Prebudgeted");

    private static final AccountType[] overviewTypes = {CHECKING, SAVING, CREDIT, PREBUDGETED};

    private static final AccountType[] PrebudgetedTypes = {PREBUDGETED};

    private static final AccountType[] alienTypes = {ALIEN};

    private final Integer value;

    private final String name;

    AccountType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    public static AccountType forValue(Integer value) {
        return Arrays.stream(AccountType.values()).filter(p -> p.getValue().equals(value)).findFirst().orElseThrow();
    }

    public boolean isPrebudgetedAccount() {
        return Arrays.asList(PrebudgetedTypes).contains(this);
    }

    public boolean isAlienAccount() {
        return Arrays.asList(alienTypes).contains(this);
    }

    public boolean isOverviewAccount() {
        return Arrays.asList(overviewTypes).contains(this);
    }

}
