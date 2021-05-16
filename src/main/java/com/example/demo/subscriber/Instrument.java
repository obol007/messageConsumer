package com.example.demo.subscriber;

import java.util.InputMismatchException;

public enum Instrument {

    EUR_USD("EUR/USD"),
    EUR_JPY("EUR/JPY"),
    GBP_USD("GBP/USD");

    Instrument(String value) {
        this.value = value;
    }

    private final String value;

    public static Instrument fromValue(String instrument) {
        for (Instrument i : Instrument.values()) {
            if (i.toString().equals(instrument)) {
                return i;
            }
        }
        throw new InputMismatchException("Wrong instrument format");
    }

    public String toString() {
        return this.value;
    }
}
