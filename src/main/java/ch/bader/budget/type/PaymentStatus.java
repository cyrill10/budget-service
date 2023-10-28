package ch.bader.budget.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentStatus implements ValueEnum<Integer> {

    OPEN(1, "open"), SETUP(2, "set up"), PAID(3, "paid");

    private final String name;

    private final Integer value;

    PaymentStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PaymentStatus forValue(Integer value) {
        return Arrays.stream(PaymentStatus.values()).filter(p -> p.getValue().equals(value)).findFirst().orElseThrow();
    }

}
