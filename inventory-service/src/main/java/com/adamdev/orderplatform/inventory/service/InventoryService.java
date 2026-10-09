package com.adamdev.orderplatform.inventory.service;

import com.adamdev.orderplatform.inventory.dto.ProductResponse;
import com.adamdev.orderplatform.inventory.dto.ReservationItemRequest;
import com.adamdev.orderplatform.inventory.dto.ReservationRequest;
import com.adamdev.orderplatform.inventory.dto.ReservationResponse;
import com.adamdev.orderplatform.inventory.exception.InsufficientStockException;
import com.adamdev.orderplatform.inventory.exception.ProductNotFoundException;
import com.adamdev.orderplatform.inventory.exception.ReservationNotFoundException;
import com.adamdev.orderplatform.inventory.mapper.ProductMapper;
import com.adamdev.orderplatform.inventory.mapper.ReservationMapper;
import com.adamdev.orderplatform.inventory.model.Product;
import com.adamdev.orderplatform.inventory.model.Reservation;
import com.adamdev.orderplatform.inventory.model.ReservationItem;
import com.adamdev.orderplatform.inventory.model.ReservationStatus;
import com.adamdev.orderplatform.inventory.repository.ProductRepository;
import com.adamdev.orderplatform.inventory.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.UUID;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final ProductMapper productMapper;
    private final ReservationMapper reservationMapper;

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

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


    @Transactional
    public void release(UUID orderId) {
        Reservation reservation = reservationRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ReservationNotFoundException(orderId));

        if (reservation.getStatus() == ReservationStatus.RELEASED) {
            log.info("Reservation for order {} already released, skipping", orderId);
            return;
        }

        for (ReservationItem item : reservation.getItems()) {
            Product product = productRepository.findByProductId(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));
            product.increaseStock(item.getQuantity());
        }

        reservation.release();
        log.info("Released stock for order {}", orderId);
    }
}