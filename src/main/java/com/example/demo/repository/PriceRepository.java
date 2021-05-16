package com.example.demo.repository;

import com.example.demo.model.Price;
import com.example.demo.subscriber.Instrument;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Getter
@Setter
@RequiredArgsConstructor
public class PriceRepository {

    @Autowired
    private final SubscriberRepository subscriberRepository;

    private Price eurUsd;
    private Price eurJpy;
    private Price gbpUsd;

    public List<Price> getPrices() {
        List<Price> prices = new ArrayList<>(3);
        prices.add(getEurJpy());
        prices.add(getEurUsd());
        prices.add(getGbpUsd());
        while (prices.remove(null)) ;
        return prices;
    }

    public List<Price> getPricesBySubscriberId(int id) {
        Set<Instrument> subscriberInstruments = subscriberRepository.getSubscriber(id).getInstruments();
        return getPrices().stream()
                .filter(message -> subscriberInstruments.contains(message.getInstrument())).collect(Collectors.toList());
    }

    public void updatePrices(List<Price> prices) {
        prices.forEach(this::updatePrice);
    }

    private synchronized void updatePrice(Price price) {
        switch (price.getInstrument()) {
            case EUR_JPY:
                if (eurJpy == null) {
                    setEurJpy(price);
                } else if
                (isNewPrice(price, this.eurJpy.getTimestamp())) {
                    setEurJpy(price);
                }
                break;
            case EUR_USD:
                if (eurUsd == null) {
                    setEurUsd(price);
                } else if (isNewPrice(price, this.eurUsd.getTimestamp())) {
                    setEurUsd(price);
                }
                break;
            default:
                if (gbpUsd == null) {
                    setGbpUsd(price);
                } else if (isNewPrice(price, this.gbpUsd.getTimestamp())) {
                    setGbpUsd(price);
                }
                break;
        }
    }

    private boolean isNewPrice(Price price, LocalDateTime oldPriceTimestamp) {
        return price.getTimestamp().isAfter(oldPriceTimestamp);
    }

}
