package com.example.empowher.model.DTO;

import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private String deliveryAddress;
    private Double totalAmount;
    private String paymentMethod;
    private String status;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private String productName;
        private int quantity;
        private Double unitPrice;
        private Double totalPrice;
    }
}


