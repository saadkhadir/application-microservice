package org.example.orderservice.web;

import org.example.orderservice.entity.Enum.OrderStatus;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderLine;
import org.example.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }


    @PostMapping
    public ResponseEntity<String> save(@RequestBody Order order, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        order.setUserId(userId);
        orderService.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Order order) {
        orderService.update(id, order);
        return ResponseEntity.ok("Order updated successfully");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.ok("Order deleted successfully");
    }

    @PatchMapping("/{orderId}/add-order-line")
    public ResponseEntity<String> addOrderLine(@PathVariable Long orderId, @RequestBody OrderLine orderLine) {
        orderService.addOrderLine(orderId, orderLine);
        return ResponseEntity.ok("OrderLine added successfully");
    }

    @DeleteMapping("/{orderId}/remove-order-line/{orderLineId}")
    public ResponseEntity<String> removeOrderLine(@PathVariable Long orderId, @PathVariable Long orderLineId) {
        orderService.removeOrderLine(orderId, orderLineId);
        return ResponseEntity.ok("OrderLine removed successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> findByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.findByUserId(userId));
    }

    @GetMapping("myOrders")
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        List<Order> orders = orderService.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        Order order = orderService.findById(orderId);
        order.setStatus(Enum.valueOf(OrderStatus.class, status));
        orderService.update(orderId, order);
        return ResponseEntity.ok("Order status updated successfully");
    }
}
