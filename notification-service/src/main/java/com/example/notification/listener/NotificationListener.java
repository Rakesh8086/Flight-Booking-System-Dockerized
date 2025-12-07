package com.example.notification.listener;

import com.example.notification.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    // triggered whenever message arrives in queue
    @RabbitListener(queues = RabbitMQConfig.BOOKING_QUEUE)
    public void handleBookingNotification(String bookingMessage) {
        log.info("--- Notification Service Received Message ---");
        log.info("Received booking details: {}", bookingMessage);
        log.info("Preparing to send confirmation email for booking.");
        log.info("Email content : {}", bookingMessage);
        log.info("Confirmation email sent");
    }
}
