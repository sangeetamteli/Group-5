package com.example.empowher.service;

import com.example.empowher.model.CartItem;
import com.example.empowher.model.DTO.CartItemDTO;
import com.example.empowher.model.Product;
import com.example.empowher.repository.CartRepository;
import com.example.empowher.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public void addToCart(Long userId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (quantity <= 0) throw new RuntimeException("Invalid quantity");
        if (quantity > product.getAvailableQuantity()) throw new RuntimeException("Quantity exceeds stock");

        CartItem existingItem = cartRepository.findByUserIdAndProductId(userId, productId);
        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            if (newQuantity > product.getAvailableQuantity()) throw new RuntimeException("Not enough stock");
            existingItem.setQuantity(newQuantity);
            cartRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(userId, productId, quantity);
            cartRepository.save(newItem);
        }
    }

    public void removeFromCart(Long userId, Long productId) {
        CartItem existingItem = cartRepository.findByUserIdAndProductId(userId, productId);
        if (existingItem != null) cartRepository.delete(existingItem);
    }

    public void clearCart(Long userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        if (!items.isEmpty()) cartRepository.deleteAll(items);
    }

    public List<CartItemDTO> getCartItemsWithDetails(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);

        return cartItems.stream()
                .map(item -> productRepository.findById(item.getProductId())
                        .map(product -> new CartItemDTO(
                                product.getProductId(),
                                product.getTitle(),
                                product.getPrice().doubleValue(),
                                item.getQuantity()
                        ))
                        .orElse(null))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    public void updateCartItems(Long userId, List<CartItemDTO> items) {
        for (CartItemDTO dto : items) {
            CartItem existingItem = cartRepository.findByUserIdAndProductId(userId, dto.getProductId());
            if (existingItem != null) {
                existingItem.setQuantity(dto.getQuantity());
                cartRepository.save(existingItem);
            }
        }
    }
}
