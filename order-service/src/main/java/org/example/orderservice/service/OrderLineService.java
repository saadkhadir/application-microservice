package org.example.orderservice.service;

import org.example.orderservice.entity.OrderLine;

import java.util.List;

public interface OrderLineService {
    List<OrderLine> findAll();
    OrderLine findById(Long id);
    List<OrderLine> findByOrderId(Long orderId);
    void save(OrderLine orderLine);
    void update(Long id, OrderLine orderLine);
    void deleteById(Long id);
}
