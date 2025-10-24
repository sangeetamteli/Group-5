package com.example.empowher.repository;

import com.example.empowher.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTitle(String title);
    List<Product> findByCategoryIdAndPriceBetween(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByTitleContainingIgnoreCase(String title);
    List<Product> findByCategoryId(Integer categoryId);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}