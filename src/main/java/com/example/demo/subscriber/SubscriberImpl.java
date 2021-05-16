package com.example.demo.subscriber;

import com.example.demo.model.Price;
import lombok.Getter;
import lombok.ToString;

import java.util.EnumSet;

@Getter
@ToString
public class SubscriberImpl implements Subscriber {

    private int id;
    private EnumSet<Instrument> instruments = EnumSet.noneOf(Instrument.class);

    public SubscriberImpl(int id) {
        this.id = id;
    }

    @Override
    // I assume that individual Subscriber has to ask for a specific instrument to be informed about its prices
    public void onMessage(Price price) {
        if (instruments.contains(price.getInstrument())) {
            System.out.println("Subscriber no: " + id + " has been informed about the new price of " + price.getInstrument());
        }
    }

    public SubscriberImpl addInstrument(Instrument instrument) {
        instruments.add(instrument);
        return this;
    }
}
