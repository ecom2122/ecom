import { Component, OnInit } from '@angular/core';
import { getTotalCartItems, getTotalCartPrice, ICart } from 'app/entities/cart/cart.model';
import { IProductCart } from 'app/entities/product-cart/product-cart.model';
import { HttpResponse } from '@angular/common/http';
import { CartService } from '../service/cart.service';
import { WeightUnit } from '../../../entities/enumerations/weight-unit.model';

@Component({
  selector: 'jhi-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
})
export class CartComponent implements OnInit {
  cart?: ICart | null;
  isLoading = false;
  total = '0';

  constructor(public cartService: CartService) {
    // do nothing.
  }

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.isLoading = true;

    this.cartService.queryOneCart().subscribe(
      (res: HttpResponse<ICart>) => {
        this.isLoading = false;
        this.cart = res.body ?? null;
        this.calcTotal();
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  calcTotal(): void {
    this.total = getTotalCartPrice(this.cart).toLocaleString();
    this.cartService.nbItems = getTotalCartItems(this.cart);
  }

  updateQuantityProductCart(item: IProductCart, quantity: number): void {
    if (item.id != null) {
      if (quantity > 0) {
        this.cartService.queryQuantityProductCart(item.id, quantity).subscribe(() => {
          // Reload component
          if (this.cart?.lines != null) {
            const indexProductCart = this.cart.lines.indexOf(item);
            this.cart.lines[indexProductCart].quantity = quantity;
            this.calcTotal();
          }
        });
      } else if (quantity === 0) {
        this.deleteLine(item);
      }
    }
  }

  updateQuantityProductCartByText(item: IProductCart, event: any): void {
    if (event.target.value != null && event.target.value !== '') {
      if (!isNaN(Number(event.target.value))) {
        const quantity: number = +event.target.value;
        this.updateQuantityProductCart(item, quantity);
      }
    }
  }

  deleteLine(productCart: IProductCart): void {
    if (productCart.id != null) {
      this.cartService.queryDeleteProductCart(productCart.id).subscribe(() => {
        // Reload component
        if (this.cart?.lines != null) {
          const indexProductCart = this.cart.lines.indexOf(productCart);
          // Splice is a method to delete starting from <index> a given <number of elements>.
          this.cart.lines.splice(indexProductCart, 1);
          this.calcTotal();
        }
      });
    }
  }

  trackId(index: number, item: IProductCart): number {
    return item.id!;
  }

  unitOfPrice(weightUnit: WeightUnit): string {
    switch (weightUnit) {
      case WeightUnit.KG:
        return 'kilo';
      case WeightUnit.G:
        return 'kilo';
      case WeightUnit.L:
        return 'litre';
      case WeightUnit.ML:
        return 'litre';
      case WeightUnit.U:
        return 'unité';

      default:
        return '';
    }
  }
}
