package com.adamdev.orderplatform.inventory.service;

import com.adamdev.orderplatform.inventory.dto.ProductResponse;
import com.adamdev.orderplatform.inventory.dto.ReservationItemRequest;
import com.adamdev.orderplatform.inventory.dto.ReservationRequest;
import com.adamdev.orderplatform.inventory.dto.ReservationResponse;
import com.adamdev.orderplatform.inventory.exception.InsufficientStockException;
import com.adamdev.orderplatform.inventory.exception.ProductNotFoundException;
import com.adamdev.orderplatform.inventory.mapper.ProductMapper;
import com.adamdev.orderplatform.inventory.mapper.ReservationMapper;
import com.adamdev.orderplatform.inventory.model.Product;
import com.adamdev.orderplatform.inventory.model.Reservation;
import com.adamdev.orderplatform.inventory.model.ReservationItem;
import com.adamdev.orderplatform.inventory.repository.ProductRepository;
import com.adamdev.orderplatform.inventory.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final ProductMapper productMapper;
    private final ReservationMapper reservationMapper;

    public InventoryService(
            ProductRepository productRepository,
            ReservationRepository reservationRepository,
            ProductMapper productMapper,
            ReservationMapper reservationMapper
    ) {
        this.productRepository = productRepository;
        this.reservationRepository = reservationRepository;
        this.productMapper = productMapper;
        this.reservationMapper = reservationMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toResponseList(products);
    }

    @Transactional
    public ReservationResponse reserve(ReservationRequest request) {
        Reservation reservation = new Reservation(request.orderId());

        for (ReservationItemRequest itemRequest: request.items()) {
            Product product = productRepository.findByProductId(itemRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.productId()));

            if (itemRequest.quantity() > product.getStock()) {
                throw new InsufficientStockException(
                        product.getProductId(),
                        itemRequest.quantity(),
                        product.getStock()
                );
            }

            product.decreaseStock(itemRequest.quantity());

            ReservationItem item = new ReservationItem(
                    product.getProductId(),
                    itemRequest.quantity(),
                    product.getPriceCents()
            );
            reservation.addItem(item);
        }

        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.toResponse(saved);
    }
}