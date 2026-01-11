package org.example.orderservice.service;

import jakarta.transaction.Transactional;
import org.example.orderservice.entity.OrderLine;
import org.example.orderservice.repository.OrderLineRepository;
import org.example.orderservice.web.ProductClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderLineServiceImpl implements OrderLineService {

    private final OrderLineRepository orderLineRepository;
    private final ProductClient productClient;

    public OrderLineServiceImpl(OrderLineRepository orderLineRepository, ProductClient productClient) {
        this.orderLineRepository = orderLineRepository;
        this.productClient = productClient;
    }

    @Override
    public List<OrderLine> findAll() {
        List<OrderLine> orderLines = orderLineRepository.findAll();
        orderLines.forEach(orderLine -> {
            if (orderLine.getProductId() != null) {
                orderLine.setProduct(productClient.getProductById(orderLine.getProductId()));
            }
        });
        return orderLines;
    }

    @Override
    public OrderLine findById(Long id) {
        OrderLine orderLine = orderLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderLine not found with id: " + id));
        if (orderLine.getProductId() != null) {
            orderLine.setProduct(productClient.getProductById(orderLine.getProductId()));
        }
        return orderLine;
    }

    @Override
    public List<OrderLine> findByOrderId(Long orderId) {
        List<OrderLine> orderLines = orderLineRepository.findByOrderId(orderId);
        orderLines.forEach(orderLine -> {
            if (orderLine.getProductId() != null) {
                orderLine.setProduct(productClient.getProductById(orderLine.getProductId()));
            }
        });
        return orderLines;
    }

    @Override
    public void save(OrderLine orderLine) {
        orderLineRepository.save(orderLine);
    }


    @Transactional
    @Override
    public void update(Long id, OrderLine orderLine) {
        OrderLine existingOrderLine = orderLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderLine not found with id: " + id));
        if (orderLine.getQuantity() > 0)
            existingOrderLine.setQuantity(orderLine.getQuantity());
        if (orderLine.getUnitPrice() != null && orderLine.getUnitPrice() > 0)
            existingOrderLine.setUnitPrice(orderLine.getUnitPrice());
        if (orderLine.getProductId() != null)
            existingOrderLine.setProductId(orderLine.getProductId());
    }


    @Override
    public void deleteById(Long id) {
        orderLineRepository.deleteById(id);
    }
}

