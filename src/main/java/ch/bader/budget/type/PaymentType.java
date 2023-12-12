package ch.bader.budget.type;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentType implements ValueEnum<Integer> {

    DEPOSIT(1, "Deposit"), STANDING_ORDER(2, "Standing Order"), EBILL(3, "e-bill");

    private final Integer value;

    private final String name;

    PaymentType(int value, String name) {
        this.value = value;
        this.name = name;
    }


    public static PaymentType forValue(Integer value) {
        return Arrays.stream(PaymentType.values()).filter(p -> p.getValue().equals(value)).findFirst().orElseThrow();
    }

}
