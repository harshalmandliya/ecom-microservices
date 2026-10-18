package com.ecommerce.notification;

import com.ecommerce.notification.payload.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
public class OrderEventConsumer {

//    @RabbitListener(queues = "${rabbitmq.queue.name}")
//    public void handleOrderEvent(OrderCreatedEvent event){
//        System.out.println("Received Order Event" + event);
//    }

    @Bean
    public Consumer<OrderCreatedEvent> orderCreated() {
       return event -> {
            log.info("Order Created Event: {}", event.getOrderId());
           log.info("Order Created Event: {}", event.getUserId());
       };
    }


}
