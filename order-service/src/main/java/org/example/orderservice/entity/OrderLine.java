package org.example.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.example.orderservice.model.Product;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString(exclude = {"order"})
@Entity
@Table(name = "order_lines")
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long productId;

    @Transient
    private Product product;

    @Column(nullable = false)
    private int quantity;


    private Double unitPrice;


    public Double getLineTotal() {
        return unitPrice * quantity;
    }
}

