package com.example.demo.subscriber;

import com.example.demo.model.Price;

import java.util.EnumSet;

public interface Subscriber {

    void onMessage(Price price);

    int getId();

    EnumSet<Instrument> getInstruments();
}
