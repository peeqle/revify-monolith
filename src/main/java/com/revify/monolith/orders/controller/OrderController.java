package com.revify.monolith.orders.controller;

import com.revify.monolith.commons.models.orders.OrderCreationDTO;
import com.revify.monolith.commons.models.orders.OrderDTO;
import com.revify.monolith.commons.models.orders.OrderStatusUpdateRequest;
import com.revify.monolith.orders.service.OrderReadService;
import com.revify.monolith.orders.service.OrderWriteService;
import com.revify.monolith.orders.util.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.revify.monolith.orders.util.OrderRequestValidation.validateOrderId;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class OrderController {

    private final OrderWriteService orderWriteService;

    private final OrderReadService orderReadService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDTO> createOrder(@RequestBody OrderCreationDTO orderCreationDTO) {
        log.debug("Caught request to create order.");

        return orderWriteService.createOrder(orderCreationDTO)
                .mapNotNull(OrderMapper::from);
    }

    @PatchMapping("{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<OrderDTO> updateOrderStatus(@RequestBody OrderStatusUpdateRequest request) {
        log.debug("Caught request to update order status. Order ID: {}", request.orderId());

        return validateOrderId(request.orderId())
                .then(orderWriteService.updateOrderStatus(request)
                        .mapNotNull(OrderMapper::from))
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to update order status: " + e.getMessage(), e)));
    }

    @GetMapping("{orderId}")
    @ResponseStatus(HttpStatus.FOUND)
    public Mono<OrderDTO> getOrderById(@PathVariable String orderId) {
        log.debug("Caught request to get order by id. Order ID: {}", orderId);

        return validateOrderId(orderId)
                .then(orderReadService.findOrderById(orderId).mapNotNull(OrderMapper::from))
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed fetching order: " + e.getMessage(), e)));
    }

    /**
     * Delete order - by client holding item or courier working for that order,
     * after close obv select the minimum price from auction if is, otherwise make auction enabled again
     */
    @DeleteMapping("{orderId}")
    public Mono<OrderDTO> deleteOrderById(@PathVariable String orderId) {
        log.debug("Caught request to delete order by id. Order ID: {}", orderId);
        return validateOrderId(orderId)
                .flatMap(x -> orderWriteService.deleteOrder(x).mapNotNull(OrderMapper::from))
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed fetching order: %s"
                        .formatted(e.getMessage()), e)));
    }
}