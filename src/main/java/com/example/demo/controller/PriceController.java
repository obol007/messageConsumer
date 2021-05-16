package com.example.demo.controller;

import com.example.demo.model.Price;
import com.example.demo.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PriceController {

    @Autowired
    private final PriceRepository priceRepository;

    @GetMapping("/prices")
    public ResponseEntity<List<Price>> showPrices() {
        return ResponseEntity.ok(priceRepository.getPrices());
    }

    @GetMapping("/prices/{subscriberId}")
    public ResponseEntity<List<Price>> showPricesForSubscriber(@PathVariable("subscriberId") int id) {
        return ResponseEntity.ok(priceRepository.getPricesBySubscriberId(id));
    }
}
