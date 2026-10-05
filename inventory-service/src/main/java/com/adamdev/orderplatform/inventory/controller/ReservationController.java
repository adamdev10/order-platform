package com.adamdev.orderplatform.inventory.controller;

import com.adamdev.orderplatform.inventory.dto.ReservationRequest;
import com.adamdev.orderplatform.inventory.dto.ReservationResponse;
import com.adamdev.orderplatform.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/reservations")
public class ReservationController {

    private final InventoryService inventoryService;

    public ReservationController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reserve(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = inventoryService.reserve(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}