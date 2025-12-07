package com.example.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String BOOKING_QUEUE = "booking_notification_queue";
    public static final String EXCHANGE = "flight_booking_exchange";
    public static final String ROUTING_KEY = "booking_key";

    @Bean
    public Queue queue() {
        return new Queue(BOOKING_QUEUE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}