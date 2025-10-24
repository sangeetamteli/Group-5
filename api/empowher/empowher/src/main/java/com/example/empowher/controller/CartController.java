package com.example.empowher.controller;

import com.example.empowher.model.DTO.AddToCartRequest;
import com.example.empowher.model.DTO.CartItemDTO;
import com.example.empowher.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/view")
    public ResponseEntity<List<CartItemDTO>> viewCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartItemsWithDetails(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<List<CartItemDTO>> addToCart(@RequestBody AddToCartRequest request) {
        cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cartService.getCartItemsWithDetails(request.getUserId()));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<List<CartItemDTO>> removeFromCart(@RequestParam Long userId,
                                                            @RequestParam Long productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(cartService.getCartItemsWithDetails(userId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<List<CartItemDTO>> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(cartService.getCartItemsWithDetails(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<List<CartItemDTO>> updateCart(@RequestParam Long userId,
                                                        @RequestBody List<CartItemDTO> items) {
        cartService.updateCartItems(userId, items);
        return ResponseEntity.ok(cartService.getCartItemsWithDetails(userId));
    }
}
