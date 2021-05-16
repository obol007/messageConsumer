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

    private Price EUR_USD;
    private Price EUR_JPY;
    private Price GBP_USD;

    public List<Price> getPrices() {
        List<Price> prices = new ArrayList<>(3);
        prices.add(getEUR_JPY());
        prices.add(getEUR_USD());
        prices.add(getGBP_USD());
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
                if (EUR_JPY == null) {
                    setEUR_JPY(price);
                } else if
                (isNewPrice(price, this.EUR_JPY.getTimestamp())) {
                    setEUR_JPY(price);
                }
                break;
            case EUR_USD:
                if (EUR_USD == null) {
                    setEUR_USD(price);
                } else if (isNewPrice(price, this.EUR_USD.getTimestamp())) {
                    setEUR_USD(price);
                }
                break;
            default:
                if (GBP_USD == null) {
                    setGBP_USD(price);
                } else if (isNewPrice(price, this.GBP_USD.getTimestamp())) {
                    setGBP_USD(price);
                }
                break;
        }
    }

    private boolean isNewPrice(Price price, LocalDateTime oldPriceTimestamp) {
        return price.getTimestamp().isAfter(oldPriceTimestamp);
    }

}
