package org.example.orderservice.web;

import org.example.orderservice.entity.Order;
import org.example.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping
    public void save(@RequestBody Order order) {
        orderService.save(order);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody Order order) {
        orderService.update(id, order);
    }
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        orderService.deleteById(id);
    }
}
