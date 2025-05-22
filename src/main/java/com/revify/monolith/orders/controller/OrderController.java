package com.revify.monolith.orders.controller;

import com.revify.monolith.commons.models.orders.OrderCreationDTO;
import com.revify.monolith.commons.models.orders.OrderDTO;
import com.revify.monolith.commons.models.orders.OrderStatusUpdateRequest;
import com.revify.monolith.orders.models.Order;
import com.revify.monolith.orders.service.OrderService;
import com.revify.monolith.orders.util.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders(@RequestParam(name = "offset", defaultValue = "0") Integer offset,
                                                       @RequestParam(name = "limit", defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(orderService.getUserOrders(offset, limit).stream().map(OrderDTO::from).toList());
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllCourierOrders(@RequestParam(name = "offset", defaultValue = "0") Integer offset,
                                                       @RequestParam(name = "limit", defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(orderService.getCourierOrders(offset, limit).stream().map(OrderDTO::from).toList());
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderCreationDTO orderCreationDTO) {
        log.debug("Caught request to create order.");

        Order order = orderService.createOrder(orderCreationDTO);
        if (order == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(OrderMapper.from(order));
    }

    @PatchMapping("{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<OrderDTO> updateOrderStatus(@RequestBody OrderStatusUpdateRequest request) {
        log.debug("Caught request to update order status. Order ID: {}", request.orderId());

        if (ObjectId.isValid(request.orderId())) {
            orderService.updateOrderStatus(request);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("{orderId}")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String orderId) {
        log.debug("Caught request to get order by id. Order ID: {}", orderId);

        if (ObjectId.isValid(orderId)) {
            Order orderById = orderService.findOrderById(new ObjectId(orderId));
            return ResponseEntity.ok(OrderMapper.from(orderById));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete order - by client holding item or courier working for that order,
     * after close obv select the minimum price from auction if is, otherwise make auction enabled again
     */
    @DeleteMapping("{orderId}")
    public ResponseEntity<OrderDTO> deleteOrderById(@PathVariable String orderId) {
        log.debug("Caught request to delete order by id. Order ID: {}", orderId);
        if (ObjectId.isValid(orderId)) {
            Order order = orderService.deleteOrder(new ObjectId(orderId));
            return ResponseEntity.ok(OrderMapper.from(order));
        }
        return ResponseEntity.notFound().build();
    }
}