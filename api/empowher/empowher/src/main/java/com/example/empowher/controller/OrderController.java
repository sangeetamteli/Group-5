package com.example.empowher.controller;

import com.example.empowher.model.DTO.OrderDTO;
import com.example.empowher.model.Order;
import com.example.empowher.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody OrderDTO orderDTO) {
        try {
            Order placedOrder = orderService.placeOrder(orderDTO);
            OrderDTO responseDTO = orderService.convertToDTO(placedOrder);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    public static class StatusUpdateRequest {
        public String status;
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long orderId,
            @RequestBody StatusUpdateRequest request) {
        boolean updated = orderService.updateOrderStatus(orderId, request.status);
        return updated
                ? ResponseEntity.ok("Order status updated successfully.")
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("OrderController active ✅");
    }
}
