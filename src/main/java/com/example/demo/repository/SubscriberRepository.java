package com.example.demo.repository;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.subscriber.Subscriber;
import com.example.demo.subscriber.SubscriberImpl;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static com.example.demo.subscriber.Instrument.*;

@Repository
public class SubscriberRepository {

    private List<Subscriber> subscribers = Arrays.asList(
            new SubscriberImpl(1).addInstrument(EUR_USD).addInstrument(EUR_JPY),
            new SubscriberImpl(2).addInstrument(GBP_USD).addInstrument(EUR_JPY),
            new SubscriberImpl(3).addInstrument(GBP_USD).addInstrument(EUR_JPY).addInstrument(EUR_USD)
    );

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public Subscriber getSubscriber(int id) {
        return subscribers.stream().filter(subscriber -> subscriber.getId() == id).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No Subscriber with id: " + id));
    }
}
