package com.example.booking.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfig {
    public static final String EXCHANGE = "flight_booking_exchange";
    public static final String ROUTING_KEY = "booking_key";

    // this one is for the producer to know where to send the notification
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }
}
