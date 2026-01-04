package org.example.orderservice.service;


import org.example.orderservice.entity.Order;

import java.util.List;

public interface OrderService {
    public List<Order> findAll();
    public Order findById(Long id);
    public void save(Order order);
    public void update(Long id, Order order);
    public void deleteById(Long id);
}
