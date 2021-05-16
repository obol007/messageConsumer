package com.example.demo.converter;

import com.example.demo.model.Price;
import com.example.demo.subscriber.Instrument;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class MessageConverter {

    private final static String pattern = "dd-MM-yyyy HH:mm:ss:SSS";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

    public static Price convertToPrice(String message) {
        String[] msgElements = message.split(",");
        try {
            int id = Integer.parseInt(msgElements[0]);
            Instrument instrument = Instrument.fromValue(msgElements[1].trim());
            BigDecimal bid = convertToBigDecimal(msgElements[2].trim());
            BigDecimal ask = convertToBigDecimal(msgElements[3]);
            String timestamp = msgElements[4];
            return Price.builder()
                    .id(id)
                    .instrument(instrument)
                    .bid(bid)
                    .ask(ask)
                    .timestamp(LocalDateTime.from(formatter.parse(timestamp)))
                    .toPrice();
        } catch (Exception e) {
            log.warn("Invalid message format");
            return null;
        }
    }

    private static BigDecimal convertToBigDecimal(String number) {
        final int DECIMAL = 1;
        int scale = 0;
        String[] integerDotDecimal = number.split("\\.");
        if (integerDotDecimal.length > 1) {
            scale = integerDotDecimal[DECIMAL].length();
        }
        BigDecimal decimal = new BigDecimal(number);
        return decimal.setScale(scale, RoundingMode.HALF_UP);
    }


}
