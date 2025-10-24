package com.example.empowher.service;

import com.example.empowher.model.*;
import com.example.empowher.model.DTO.OrderDTO;
import com.example.empowher.repository.OrderRepository;
import com.example.empowher.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order placeOrder(OrderDTO orderDTO) {
        if (orderDTO.getDeliveryAddress() == null || orderDTO.getDeliveryAddress().isEmpty())
            throw new IllegalArgumentException("Delivery address is required");

        if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty())
            throw new IllegalArgumentException("Cart is empty. Cannot place order.");

        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setTotalAmount(BigDecimal.valueOf(orderDTO.getTotalAmount()));
        order.setStatus("PENDING");

        List<OrderProduct> items = orderDTO.getItems().stream().map(item -> {
            OrderProduct op = new OrderProduct();
            op.setProductId(item.getProductId());
            op.setQuantity(item.getQuantity());
            op.setUnitPrice(BigDecimal.valueOf(item.getUnitPrice()));
            op.setTotalPrice(BigDecimal.valueOf(item.getTotalPrice()));
            op.setOrder(order);
            return op;
        }).collect(Collectors.toList());

        order.setOrderProducts(items);
        return orderRepository.save(order);
    }

    public List<OrderDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .sorted(Comparator.comparing(Order::getOrderId).reversed()) // newest first
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Order::getOrderId).reversed()) // newest first
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean updateOrderStatus(Long orderId, String status) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(status);
                    orderRepository.save(order);
                    return true;
                })
                .orElse(false);
    }

    public OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setTotalAmount(order.getTotalAmount().doubleValue());
        dto.setStatus(order.getStatus());

        List<OrderDTO.OrderItemDTO> itemDTOs = order.getOrderProducts().stream().map(op -> {
            OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
            item.setProductId(op.getProductId());
            item.setQuantity(op.getQuantity());
            item.setUnitPrice(op.getUnitPrice().doubleValue());
            item.setTotalPrice(op.getTotalPrice().doubleValue());

            productRepository.findById(op.getProductId())
                    .ifPresent(p -> item.setProductName(p.getTitle()));

            if (item.getProductName() == null) item.setProductName("Unknown Product");
            return item;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}

