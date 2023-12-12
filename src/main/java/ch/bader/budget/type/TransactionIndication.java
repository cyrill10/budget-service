package ch.bader.budget.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransactionIndication implements ValueEnum<Integer> {

    EXPECTED(1, "E"), UNEXPECTED(2, "U");

    private final Integer value;

    private final String name;

    TransactionIndication(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static TransactionIndication forValue(Integer value) {
        return Arrays.stream(TransactionIndication.values()).filter(p -> p.getValue().equals(value)).findFirst()
                     .orElseThrow();
    }

}
