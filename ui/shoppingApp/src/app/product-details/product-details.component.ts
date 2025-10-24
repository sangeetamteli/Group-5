import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../service/product.service';
import { CartService } from '../service/cart.service';
import { NotificationService } from '../notification.service';
import { Product } from '../model/product';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.scss']
})
export class ProductDetailsComponent implements OnInit {
  product!: Product;
  loading: boolean = false;
  error: string = '';

  selectedUser = JSON.parse(localStorage.getItem('user') || '{}');

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.error = 'Invalid product ID';
      return;
    }

    this.loading = true;
    this.productService.getProductById(id).subscribe({
      next: (res) => {
        this.product = res;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load product details';
        this.loading = false;
      }
    });
  }

  handleAddToCart(): void {
    if (!this.selectedUser?.id || !this.product?.productId) return;

    this.cartService.addToCart(this.selectedUser.id, this.product.productId, 1).subscribe({
      next: () => {
        this.notificationService.notify({
          message: `${this.product.title} added to cart!`,
          type: 'success',
          header: 'Cart'
        });
        this.cartService.refreshCart(this.selectedUser.id);
      },
      error: () => {
        this.notificationService.notify({
          message: `Failed to add ${this.product.title} to cart.`,
          type: 'error',
          header: 'Cart'
        });
      }
    });
  }
}
