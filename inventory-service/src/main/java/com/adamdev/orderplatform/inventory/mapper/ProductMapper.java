package com.adamdev.orderplatform.inventory.mapper;

import com.adamdev.orderplatform.inventory.dto.ProductResponse;
import com.adamdev.orderplatform.inventory.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getStock(),
                product.getPriceCents()
        );
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .toList();
    }
}