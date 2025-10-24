package com.example.empowher.controller;

import com.example.empowher.model.Product;
import com.example.empowher.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add-product")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            Product addedProduct = productService.addProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add product");
        }
    }

    @PutMapping("/update-product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Optional<Product> existing = productService.findProductById(id);
        if (existing.isPresent()) {
            product.setProductId(id);
            Product updated = productService.updateProduct(product);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }

    @GetMapping("/get-product/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.findProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Optional<Product> existing = productService.findProductById(id);
        if (existing.isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }

    @GetMapping("/get-products")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) BigDecimal min_price,
            @RequestParam(required = false) BigDecimal max_price) {

        List<Product> products;
        if (title != null && !title.isBlank()) {
            products = productService.searchProductsByTitle(title);
        } else if (categoryId != null || min_price != null || max_price != null) {
            products = productService.filterByCategoryIdAndPriceBetween(categoryId, min_price, max_price);
        } else {
            products = productService.findAll();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping("/decrease-stock/{id}")
    public ResponseEntity<?> decreaseStock(@PathVariable Long id, @RequestParam int quantity) {
        Optional<Product> optionalProduct = productService.findProductById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getAvailableQuantity() >= quantity) {
                product.setAvailableQuantity(product.getAvailableQuantity() - quantity);
                productService.updateProduct(product);
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient stock");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }
}
