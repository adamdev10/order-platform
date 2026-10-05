package com.adamdev.orderplatform.inventory.controller;

import com.adamdev.orderplatform.inventory.dto.ProductResponse;
import com.adamdev.orderplatform.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final InventoryService inventoryService;

    public ProductController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<ProductResponse> listAll() {
        return inventoryService.listProducts();
    }
}