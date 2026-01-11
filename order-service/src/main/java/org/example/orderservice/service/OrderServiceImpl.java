package org.example.orderservice.service;

import jakarta.transaction.Transactional;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderLine;
import org.example.orderservice.repository.OrderLineRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.web.ProductClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final ProductClient productClient;

    public OrderServiceImpl(OrderRepository orderRepository, OrderLineRepository orderLineRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.productClient = productClient;
    }


    @Transactional
    @Override
    public Order addOrderLine(
            Long orderId,
            OrderLine orderLine) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        orderLine.setUnitPrice(productClient.getProductById(orderLine.getProductId()).getPrice());
        order.addOrderLine(orderLine);
        return order;
    }


    @Transactional
    @Override
    public Order removeOrderLine(
            Long orderId,
            Long orderLineId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        OrderLine orderLine = orderLineRepository.findById(orderLineId).orElseThrow(() -> new RuntimeException("OrderLine not found with id: " + orderLineId));
        order.removeOrderLine(orderLine);
        return order;
    }


    @Override
    public List<Order> findAll() {

        List<Order> orders = orderRepository.findAll();

        orders.forEach(order -> {
            List<OrderLine> orderLines = order.getOrderLines();
            orderLines.forEach(orderLine -> {
                if (orderLine.getProductId() != null) {
                    orderLine.setProduct(productClient.getProductById(orderLine.getProductId()));
                }
            });
        });
        return orders;
    }

    @Override
    public Order findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        List<OrderLine> orderLines = order.getOrderLines();
        orderLines.forEach(orderLine -> {
            if (orderLine.getProductId() != null) {
                orderLine.setProduct(productClient.getProductById(orderLine.getProductId()));
            }
        });
        return order;
    }

    @Transactional
    @Override
    public void save(Order order) {
        // Traiter les lignes de commande
        if (order.getOrderLines() != null) {
            for (OrderLine line : order.getOrderLines()) {
                // Enrichir avec le prix du produit
                if (line.getProductId() != null) {
                    line.setUnitPrice(
                            productClient.getProductById(line.getProductId()).getPrice()
                    );
                }
                //Établir la relation bidirectionnelle
                line.setOrder(order);
            }
        }

        // Sauvegarder
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void update(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        if (order.getDate() != null)
            existingOrder.setDate(order.getDate());
        if (order.getStatus() != null)
            existingOrder.setStatus(order.getStatus());
        if(order.getOrderLines() != null)
            order.getOrderLines().forEach(orderLine -> {
                existingOrder.addOrderLine(orderLine);
            });
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<Order> findByUserId(String id) {
        return orderRepository.findByUserId(id);
    }
}
