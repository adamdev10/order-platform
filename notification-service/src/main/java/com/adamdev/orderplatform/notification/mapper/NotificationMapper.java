package com.adamdev.orderplatform.notification.mapper;

import com.adamdev.orderplatform.notification.dto.NotificationResponse;
import com.adamdev.orderplatform.notification.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getOrderId(),
                notification.getCustomerId(),
                notification.getType(),
                notification.getMessage(),
                notification.getSentAt()
        );
    }
}