package com.example.demo.publisher;

import com.example.demo.repository.PriceRepository;
import com.example.demo.repository.SubscriberRepository;
import com.example.demo.subscriber.Subscriber;
import com.example.demo.subscriber.SubscriberImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.example.demo.subscriber.Instrument.EUR_USD;
import static com.example.demo.subscriber.Instrument.GBP_USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class MessagePublisherTest {

    @Mock
    SubscriberRepository subscriberRepository;
    @Autowired
    PriceRepository priceRepository;
    MessagePublisher publisher;

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        publisher = new MessagePublisher(subscriberRepository, priceRepository);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @ParameterizedTest
    @MethodSource("newPrices")
    void shouldPrintToConsoleWhenSubscriberHasInstrumentSubscription(String message, String expectedOutput) {
        //given
        Subscriber subscriber1 = new SubscriberImpl(1).addInstrument(EUR_USD).addInstrument(GBP_USD);
        Subscriber subscriber2 = new SubscriberImpl(2).addInstrument(GBP_USD);
        when(subscriberRepository.getSubscribers()).thenReturn(List.of(subscriber1, subscriber2));
        //when
        publisher.sendMessages(Collections.singletonList(message));
        //then
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    @Test
    void shouldAssignPriceWithTheNewestDate() {
        //given
        String message1 = "101, EUR/JPY, 2.0000,2.1000,01-05-2021 13:01:01:001";
        String message2 = "102, EUR/JPY, 1.0000,2.1000,01-05-2021 12:01:01:001";
        //when
        publisher.sendMessages(List.of(message1));
        publisher.sendMessages(List.of(message2));
        //then
        String expectedTimestamp = "2021-05-01T13:01:01.001";
        String actualTimestamp = priceRepository.getEurJpy().getTimestamp().toString();
        assertEquals(expectedTimestamp, actualTimestamp);
    }

    @Test
    void shouldAddMarginToThePrices() {
        //given
        String message = "101, EUR/JPY, 10000,10000,01-06-2021 13:01:01:001";
        //when
        publisher.sendMessages(List.of(message));
        //then
        String expectedBid = "9990";
        String expectedAsk = "10010";
        System.out.println(priceRepository.getEurJpy());
        String actualBid = priceRepository.getEurJpy().getBid().toString();
        String actualAsk = priceRepository.getEurJpy().getAsk().toString();
        assertEquals(expectedBid, actualBid);
        assertEquals(expectedAsk, actualAsk);
    }

    private static Stream<Arguments> newPrices() {
        return Stream.of(
                Arguments.of("100, EUR/USD, 1.0000,1.1000,01-05-2021 12:01:01:001", "Subscriber no: 1 has been informed about the new price of EUR/USD"),
                Arguments.of("101, EUR/JPY, 2.0000,2.1000,01-05-2021 13:01:01:001", ""),
                Arguments.of("102, EUR/JPY, 1.0000,2.1000,01-05-2021 12:01:01:001", ""),
                Arguments.of("103, GBP/USD, 1.0000,2.1000,01-05-2021 12:01:01:001",
                        "Subscriber no: 1 has been informed about the new price of GBP/USD" +
                                "\nSubscriber no: 2 has been informed about the new price of GBP/USD")
        );
    }


}