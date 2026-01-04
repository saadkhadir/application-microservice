package org.example.orderservice.service;

import jakarta.transaction.Transactional;
import org.example.orderservice.entity.Order;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.web.ProductClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderServiceImpl(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    @Override
    public List<Order> findAll() {
                List<Order> orders = orderRepository.findAll();
                orders.forEach(order -> {
                    if(order.getProductId()!=null) {
                        order.setProduct(productClient.getProductById(order.getProductId()));
                    }
                });
                return orders;
    }

    @Override
    public Order findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        if(order.getProductId()!=null) {
            order.setProduct(productClient.getProductById(order.getProductId()));
        }
        return order;
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void update(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        if(order.getDate() != null)
            existingOrder.setDate(order.getDate());
        if(order.getStatus() != null)
            existingOrder.setStatus(order.getStatus());
        if(order.getQuantity() != 0)
            existingOrder.setQuantity(order.getQuantity());
        if(order.getProductId() != null)
            existingOrder.setProductId(order.getProductId());

    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
