package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.orderservice.entity.Enum.OrderStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString(exclude = {"orderLines"})
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderLine> orderLines = new ArrayList<>();

    private String userId;

    public void addOrderLine(OrderLine orderLine) {
        orderLine.setOrder(this);
        this.orderLines.add(orderLine);
    }


    public void removeOrderLine(OrderLine orderLine) {
        this.orderLines.remove(orderLine);
        orderLine.setOrder(null);
    }


    public Double getTotalAmount() {
        return this.orderLines.stream()
                .mapToDouble(OrderLine::getLineTotal)
                .sum();
    }

}
