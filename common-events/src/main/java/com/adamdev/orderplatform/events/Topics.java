package com.adamdev.orderplatform.events;

public final class Topics {

    private Topics() {
    }

    public static final String ORDER_CREATED = "order-created";
    public static final String STOCK_RESERVED = "stock-reserved";
    public static final String STOCK_FAILED = "stock-failed";
    public static final String STOCK_RELEASE = "stock-release";
    public static final String PAYMENT_COMPLETED = "payment-completed";
    public static final String PAYMENT_FAILED = "payment-failed";
    public static final String ORDER_FINAL_EVENTS = "order-final-events";
}