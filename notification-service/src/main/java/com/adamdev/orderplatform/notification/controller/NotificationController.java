package com.adamdev.orderplatform.notification.controller;

import com.adamdev.orderplatform.notification.dto.NotificationRequest;
import com.adamdev.orderplatform.notification.dto.NotificationResponse;
import com.adamdev.orderplatform.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public List<NotificationResponse> list(@RequestParam(required = false) UUID orderId) {
        if (orderId == null) {
            return notificationService.listAll();
        }
        return notificationService.listByOrder(orderId);
    }
}