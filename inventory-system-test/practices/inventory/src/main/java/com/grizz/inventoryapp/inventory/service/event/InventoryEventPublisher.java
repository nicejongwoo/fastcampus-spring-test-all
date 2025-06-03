package com.grizz.inventoryapp.inventory.service.event;

import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.support.KafkaHeaders;

public interface InventoryEventPublisher {
    String MESSAGE_KEY = KafkaHeaders.KEY;
    void publish(@NotNull InventoryEvent event);
}
