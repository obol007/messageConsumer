package com.example.demo.publisher;

import com.example.demo.converter.MessageConverter;
import com.example.demo.model.Price;
import com.example.demo.repository.PriceRepository;
import com.example.demo.repository.SubscriberRepository;
import com.example.demo.subscriber.Subscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessagePublisher {

    @Autowired
    private final SubscriberRepository subscriberRepository;
    @Autowired
    private final PriceRepository priceRepository;


    public void sendMessages(List<String> messages) {
        List<Subscriber> subscribers = subscriberRepository.getSubscribers();
        List<Price> newPrices =
                convertMessagesToPrices(messages);
        priceRepository.updatePrices(newPrices);
        newPrices.forEach(price -> subscribers.forEach(subscriber -> subscriber.onMessage(price)));
    }

    private List<Price> convertMessagesToPrices(List<String> messages) {
        return messages
                .stream()
                .map(MessageConverter::convertToPrice)
                .filter(Objects::nonNull)
                .map(message -> message.applyCommission(0.1, 0.1))
                .collect(Collectors.toList());
    }
}


