package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.orderservice.entity.Enum.OrderStatus;
import org.example.orderservice.model.Product;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private OrderStatus status;
    private int quantity;
    private Long productId;
    @Transient
    private Product product;

}
