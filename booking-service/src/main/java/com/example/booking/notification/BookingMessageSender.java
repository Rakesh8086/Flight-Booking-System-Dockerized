package com.example.booking.notification;

import com.example.booking.config.RabbitMQProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookingMessageSender {
    private static final Logger log = LoggerFactory.getLogger(BookingMessageSender.class);
    private final RabbitTemplate rabbitTemplate;

    public BookingMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendBookingConfirmation(String messagePayload) {
        log.info("Trying to send message to RabbitMQ: {}", messagePayload);
        rabbitTemplate.convertAndSend(
            RabbitMQProducerConfig.EXCHANGE, 
            RabbitMQProducerConfig.ROUTING_KEY, 
            messagePayload
        );
        
        log.info("Message sent to exchange: {}", RabbitMQProducerConfig.EXCHANGE);
    }
}