package org.example.orderservice.web;

import org.example.orderservice.entity.OrderLine;
import org.example.orderservice.service.OrderLineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-lines")
public class OrderLineController {

    private final OrderLineService orderLineService;

    public OrderLineController(OrderLineService orderLineService) {
        this.orderLineService = orderLineService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderLine>> findAll() {
        return ResponseEntity.ok(orderLineService.findAll());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderLine> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderLineService.findById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderLine>> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderLineService.findByOrderId(orderId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<String> save(@RequestBody OrderLine orderLine) {
        orderLineService.save(orderLine);
        return ResponseEntity.status(HttpStatus.CREATED).body("OrderLine created successfully");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody OrderLine orderLine) {
        orderLineService.update(id, orderLine);
        return ResponseEntity.ok("OrderLine updated successfully");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        orderLineService.deleteById(id);
        return ResponseEntity.ok("OrderLine deleted successfully");
    }
}

