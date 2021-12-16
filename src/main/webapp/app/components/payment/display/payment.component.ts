import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, Validators } from '@angular/forms';
import { CartService } from 'app/components/services/cart.service';

import { PaymentService } from 'app/components/services/payment.service';
import { getProductQuantity, getTotalCartPrice, ICart } from 'app/entities/cart/cart.model';
import { HttpResponse } from '@angular/common/http';
import { IOrder, Order } from '../../../entities/order/order.model';
import * as dayjs from 'dayjs';
import { DATE_FORMAT, DATE_TIME_FORMAT, TIME_FORMAT } from '../../../config/input.constants';
import { IPromotionalCode } from '../../../entities/promotional-code/promotional-code.model';
import { ReductionType } from '../../../entities/enumerations/reduction-type.model';
import { IProductOrder, ProductOrder } from '../../../entities/product-order/product-order.model';
import { PromotionService } from '../../services/promotion.service';
import { Router } from '@angular/router';

@Component({
  selector: 'jhi-payment',
  templateUrl: './payment.component.html',
})
export class PaymentComponent implements OnInit {
  cart?: ICart | null;
  promoCode?: IPromotionalCode | null;
  totalPrice = 0;
  totalSaved = 0;
  promoCodeSavings = 0;
  codeUsed = '';
  isLoadingCart = true;
  isLoadingPromoCode = false;
  isSaving = false;

  editForm = this.fb.group({
    receptionDate: [null, [Validators.required]],
    receptionTime: [null, [Validators.required]],
    promoCode: [null],
    cbName: [null, [Validators.required, Validators.pattern("^[a-zA-Z -']+")]],
    cbNumber: [null, [Validators.required, Validators.pattern('^[0-9]+')]],
    cbExpirationDate: [null, [Validators.required]],
    cbCVC: [null, [Validators.required, Validators.pattern('^[0-9]+')]],
  });

  constructor(
    protected paymentService: PaymentService,
    protected cartService: CartService,
    protected modalService: NgbModal,
    public promotionService: PromotionService,
    public router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loadCart();
    const order: IOrder = new Order();
    const today = dayjs().startOf('day').add(1, 'day').add(12, 'hour');
    order.paymentDate = today;
    order.receptionDate = today;
    this.updateForm(order);
  }

  loadCart(): void {
    this.isLoadingCart = true;

    this.cartService.queryOneCart().subscribe(
      (res: HttpResponse<ICart>) => {
        this.isLoadingCart = false;
        this.cartService.cart = res.body ?? null;
        this.cart = res.body ?? null;
        this.calcTotal();
      },
      () => {
        this.isLoadingCart = false;
      }
    );
  }

  calcTotal(): void {
    if (this.promotionService.getPromotions() != null) {
      const results: number[] = getTotalCartPrice(this.cart, this.promotionService);
      this.totalSaved = results[1] + this.promoCodeSavings;
      this.totalPrice = results[0] - this.promoCodeSavings;
    } else {
      this.promotionService.promotionsObs.subscribe(() => {
        const results: number[] = getTotalCartPrice(this.cart, this.promotionService);
        this.totalSaved = results[1] + this.promoCodeSavings;
        this.totalPrice = results[0] - this.promoCodeSavings;
      });
    }
    if (this.totalPrice < 0) {
      this.totalPrice = 0;
    }
  }

  calcTotalSaved(): void {
    if (this.promoCode == null || this.cart == null) {
      this.promoCodeSavings = 0;
      return;
    }
    let total = 0;
    if (this.promoCode.unit === ReductionType.FIX) {
      this.promoCode.products!.forEach(p => {
        total += this.promoCode!.value! * getProductQuantity(this.cart, p.id!);
      });
    } else if (this.promoCode.unit === ReductionType.PERCENTAGE) {
      const percentage: number = this.promoCode.value! / 100;
      this.promoCode.products!.forEach(p => {
        total += p.price! * percentage * getProductQuantity(this.cart, p.id!);
      });
    }
    this.promoCodeSavings = total;
    this.calcTotal();
  }

  generateOrder(): void {
    this.isSaving = true;
    const order = this.createFromForm();
    this.paymentService.create(order).subscribe(
      () => {
        this.isSaving = false;
        this.cartService.calcTotal();
        this.router.navigate(['/orders']);
      },
      () => {
        this.isSaving = false;
      }
    );
  }

  previousState(): void {
    window.history.back();
  }

  applyPromoCode(): void {
    this.paymentService.findPromoCode(this.editForm.get(['promoCode'])!.value, true).subscribe(
      (res: HttpResponse<IPromotionalCode>) => {
        this.isLoadingPromoCode = false;
        this.promoCode = res.body ?? null;
        this.calcTotalSaved();
      },
      () => {
        this.isLoadingPromoCode = false;
        // TODO Afficher une alerte indiquant que le tag est invalide.
      }
    );
  }

  protected updateForm(order: IOrder): void {
    this.editForm.patchValue({
      receptionDate: order.receptionDate ? order.receptionDate.format(DATE_FORMAT) : null,
      receptionTime: order.receptionDate ? order.receptionDate.format(TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): IOrder {
    return {
      ...new Order(),
      receptionDate: this.editForm.get(['receptionDate'])!.value
        ? dayjs(
            `${String(this.editForm.get(['receptionDate'])!.value)}T${String(this.editForm.get(['receptionTime'])!.value)}`,
            DATE_TIME_FORMAT
          )
        : undefined,
      totalPrice: +(+this.totalPrice).toFixed(2),
      promotionalCode: this.promoCode,
      lines: this.generateOrderLinesFromCart(),
    };
  }

  private generateOrderLinesFromCart(): IProductOrder[] | null {
    if (this.cart == null) {
      return null;
    }

    const orderLines: IProductOrder[] = [];
    for (const line of this.cart.lines!) {
      orderLines.push(
        new ProductOrder(NaN, line.quantity, line.product!.price! * line.quantity!, null, null, null, null, line.product, null)
      );
    }

    return orderLines;
  }
}
