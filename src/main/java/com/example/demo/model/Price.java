package com.example.demo.model;

import com.example.demo.subscriber.Instrument;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@ToString
@Builder(buildMethodName = "toPrice")
@Getter
public class Price {

    private int id;
    private Instrument instrument;
    private BigDecimal bid;
    private BigDecimal ask;
    private LocalDateTime timestamp;

    /**
     * Applies bid and ask commission to prices.
     *
     * @param bidCommission percentage of the commission subtracted from the bid
     * @param askCommission percentage of the commission added to the ask
     */
    public Price applyCommission(double bidCommission, double askCommission) {
        bidCommission /= 100;
        askCommission /= 100;
        int scale = this.bid.scale();
        this.bid = bid.multiply(new BigDecimal(String.valueOf(1 - bidCommission))).setScale(scale, RoundingMode.HALF_UP);
        this.ask = ask.multiply(new BigDecimal(String.valueOf(1 + askCommission))).setScale(scale, RoundingMode.HALF_UP);
        return this;
    }
}
