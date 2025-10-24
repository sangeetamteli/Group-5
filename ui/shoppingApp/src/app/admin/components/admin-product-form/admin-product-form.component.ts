import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Product } from 'src/app/model/product';

@Component({
  selector: 'app-admin-product-form',
  templateUrl: './admin-product-form.component.html'
})
export class AdminProductFormComponent implements OnInit {
  @Input() isEdit: boolean = false;
  @Input() productData: Product | null = null;
  @Output() save = new EventEmitter<Product>();
  @Output() cancel = new EventEmitter<void>();

  productForm!: FormGroup;

  categories = [
    { id: 1, name: 'Electronics' },
    { id: 2, name: 'Clothing' },
    { id: 3, name: 'Books' },
    { id: 4, name: 'Home Appliances' },
    { id: 5, name: 'Accessories' },
    { id: 6, name: 'Furniture' },
    { id: 7, name: 'Groceries' },
    { id: 8, name: 'Sportswear' },
    { id: 9, name: 'Toys' }
  ];

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    this.productForm = this.fb.group({
      title: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(1)]],
      availableQuantity: [1, [Validators.required, Validators.min(0)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      categoryId: ['', Validators.required],
      isActive: [true],
      imageUrl: ['', Validators.required]
    });

    // Prefill form if editing
    if (this.productData) {
      this.productForm.patchValue(this.productData);
    }
  }

  isInvalid(controlName: string): boolean {
    const control = this.productForm.get(controlName);
    return !!(control && control.invalid && (control.touched || control.dirty));
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      const product: Product = this.productForm.value;

      // Preserve product ID if editing
      if (this.productData?.productId) {
        product.productId = this.productData.productId;
      }

      this.save.emit(product);

      if (!this.isEdit) {
        this.productForm.reset({
          title: '',
          price: 0,
          availableQuantity: 1,
          description: '',
          categoryId: '',
          isActive: true,
          imageUrl: ''
        });
      }
    } else {
      this.productForm.markAllAsTouched();
    }
  }

  onCancel(): void {
    this.cancel.emit();
  }
}
