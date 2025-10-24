package com.example.empowher.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", schema = "empowher_db")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "available_quantity")
    private Integer availableQuantity;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "category_id")
    private Integer categoryId;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public Product() { }

    public Product(Long productId, String title, String description, Integer availableQuantity, BigDecimal price,
                   Integer categoryId, String imageUrl) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.availableQuantity = availableQuantity;
        this.price = price;
        this.categoryId = categoryId;
        this.imageUrl = imageUrl;
    }
}
