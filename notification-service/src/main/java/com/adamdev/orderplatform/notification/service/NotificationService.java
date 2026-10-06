package com.adamdev.orderplatform.notification.service;

import com.adamdev.orderplatform.notification.dto.NotificationRequest;
import com.adamdev.orderplatform.notification.dto.NotificationResponse;
import com.adamdev.orderplatform.notification.mapper.NotificationMapper;
import com.adamdev.orderplatform.notification.model.Notification;
import com.adamdev.orderplatform.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        Notification notification = new Notification(
                request.orderId(),
                request.customerId(),
                request.type(),
                request.message()
        );

        Notification saved = notificationRepository.save(notification);

        log.info("Notification sent to {} for order {}: {}",
                saved.getCustomerId(),
                saved.getOrderId(),
                saved.getMessage()
        );

        return notificationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listAll() {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listByOrder(UUID orderId) {
        List<Notification> notifications =
                notificationRepository.findByOrderIdOrderBySentAtDesc(orderId);
        return notifications.stream()
                .map(notificationMapper::toResponse)
                .toList();
    }
}