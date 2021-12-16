import { Component, Input } from '@angular/core';
import { IProduct } from '../../../entities/product/product.model';
import { AccountService } from '../../../core/auth/account.service';
import { Router } from '@angular/router';
import { WeightUnit } from '../../../entities/enumerations/weight-unit.model';
import { getPriceWeightStr } from '../../products/products.module';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { PromotionService } from '../../services/promotion.service';

@Component({
  selector: 'jhi-product-card',
  templateUrl: './product-card.component.html',
})
export class ProductCardComponent {
  @Input() product: IProduct | null = null;

  constructor(
    protected productService: ProductService,
    public accountService: AccountService,
    private router: Router,
    public cartService: CartService,
    public promotionService: PromotionService
  ) {}

  isFavorites(product: IProduct): boolean {
    return this.productService.isFavorites(product);
  }

  addToFavorite(product: IProduct): void {
    // If no connected redirection to login page.
    if (!this.accountService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    } else if (product.id !== undefined) {
      this.productService.addToFavorites(product.id);
    }
  }

  getIntegerOfPrice(price?: number): string {
    const b = price!.toString().split('.');
    return b[0];
  }

  getIntegerOfPricePromo(promo: any, price?: number): any {
    let res: any = '';
    const subPromo = Number(promo.substr(1, promo.length - 2));

    if (promo.substr(promo.length - 1) === '%') {
      res = price! - (price! * subPromo) / 100;
    } else {
      res = price! - subPromo;
    }

    res = res.toFixed(2).toString();
    const b = res.split('.');
    return b[0];
  }

  getDecimalsOfPricePromo(promo: any, price?: number): any {
    let res: any = '';
    const subPromo = Number(promo.substr(1, promo.length - 2));

    if (promo.substr(promo.length - 1) === '%') {
      res = price! - (price! * subPromo) / 100;
    } else {
      res = price! - subPromo;
    }

    res = res.toFixed(2).toString();
    const b = res.split('.');
    return b[1];
  }

  getDecimalsOfPrice(price?: number): string {
    const b = price!.toFixed(2).toString().split('.');
    return b[1];
  }

  getPriceWeightStrCard(product: IProduct): string {
    return getPriceWeightStr(product.price!, product.weight!, product.weightUnit!);
  }

  getPriceWeightStrCardPromo(product: IProduct, promo: any): string {
    const res = Number(getPriceWeightStr(product.price!, product.weight!, product.weightUnit!).replace(',', '.'));
    const subPromo = Number(promo.substr(1, promo.length - 2));
    if (promo.substr(promo.length - 1) === '%') {
      return (res - (res * subPromo) / 100).toFixed(2).toString().replace('.', ',');
    } else {
      return (res - subPromo).toFixed(2).toString().replace('.', ',');
    }
  }

  getStringWeight(product?: IProduct): string {
    if (product!.weightUnit === WeightUnit.ML || product!.weightUnit === WeightUnit.L) {
      return 'L';
    } else if (product!.weightUnit === WeightUnit.G || product!.weightUnit === WeightUnit.KG) {
      return 'kg';
    } else {
      return 'u';
    }
  }

  isPresent(productId?: number): boolean {
    return this.cartService.productsMap.has(<number>productId);
  }

  addToCart(product: IProduct): void {
    // If no connected redirection to login page.
    if (!this.accountService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }
    this.cartService.addToCart(product);
  }

  quantityProduct(item: IProduct): number {
    const lineProduct = this.cartService.getProductCart(item);
    return lineProduct?.quantity ?? 0;
  }

  updateQuantityProduct(item: IProduct, quantity: number): void {
    this.cartService.updateQuantityProduct(item, quantity);
  }

  updateQuantityProductByText(item: IProduct, event: any): void {
    if (event.target.value != null && event.target.value !== '') {
      if (!isNaN(Number(event.target.value))) {
        const quantity: number = +event.target.value;
        this.updateQuantityProduct(item, quantity);
      }
    }
  }
}
