import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProductService } from '../service/product.service';
import { CartService } from '../service/cart.service';
import { Product } from '../model/product';
import { NotificationService } from '../notification.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  cartCount = 0;

  categories = [
    { id: 1, name: 'Electronics' },
    { id: 2, name: 'Clothing' },
    { id: 3, name: 'Books' },
    { id: 4, name: 'Home appliances' },
    { id: 5, name: 'Accessories' },
    { id: 6, name: 'Furniture' },
    { id: 7, name: 'Groceries' },
    { id: 8, name: 'Sportswear' },
    { id: 9, name: 'Toys' }
  ];

  selectedCategory: number = 0;
  minPrice: number | null = null;
  maxPrice: number | null = null;
  searchTitle: string = '';

  selectedUser = JSON.parse(localStorage.getItem('user') || '{}');

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private router: Router,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.productService.loadProducts();
    this.productService.products$.subscribe(products => {
      this.products = products;
      this.filteredProducts = [...products];
    });

    if (this.selectedUser?.id) {
      this.cartService.cartItems$.subscribe(items => {
        this.cartCount = items.length;
      });
      this.cartService.refreshCart(this.selectedUser.id);
    }
  }

  addToCart(product: Product): void {
    if (!this.selectedUser?.id || !product.productId) return;

    this.cartService.addToCart(this.selectedUser.id, product.productId, 1).subscribe({
      next: () => {
        this.cartService.refreshCart(this.selectedUser.id);
        this.notificationService.notify({
          message: `${product.title} added to cart!`,
          type: 'success',
          header: 'Cart'
        });
      },
      error: err => {
        console.error(err);
        this.notificationService.notify({
          message: `Failed to add ${product.title} to cart.`,
          type: 'error',
          header: 'Cart'
        });
      }
    });
  }

  viewDetailsEvent(productId: number | undefined): void {
    if (productId === undefined) return;
    this.router.navigate(['/product', productId]);
  }

  applyFilter(): void {
    this.filteredProducts = this.products.filter(product => {
      const matchesCategory = this.selectedCategory ? product.categoryId === this.selectedCategory : true;
      const matchesMin = this.minPrice != null ? product.price >= this.minPrice : true;
      const matchesMax = this.maxPrice != null ? product.price <= this.maxPrice : true;
      const matchesTitle = this.searchTitle ? product.title.toLowerCase().includes(this.searchTitle.toLowerCase()) : true;
      return matchesCategory && matchesMin && matchesMax && matchesTitle;
    });
  }

  resetFilters(): void {
    this.searchTitle = '';
    this.selectedCategory = 0;
    this.minPrice = null;
    this.maxPrice = null;
    this.filteredProducts = [...this.products];
  }
}
