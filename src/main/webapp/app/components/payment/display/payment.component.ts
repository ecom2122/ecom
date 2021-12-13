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

@Component({
  selector: 'jhi-payment',
  templateUrl: './payment.component.html',
})
export class PaymentComponent implements OnInit {
  cart?: ICart | null;
  promoCode?: IPromotionalCode | null;
  totalPrice = 0;
  totalSaved = 0;
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
      this.totalPrice = getTotalCartPrice(this.cart, this.promotionService);
    } else {
      this.promotionService.promotionsObs.subscribe(promotions => {
        this.totalPrice = getTotalCartPrice(this.cart, this.promotionService);
      });
    }
  }

  calcTotalSaved(): void {
    if (this.promoCode == null || this.cart == null) {
      this.totalSaved = 0;
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
    this.totalSaved = total;
  }

  generateOrder(): void {
    this.isSaving = true;
    const order = this.createFromForm();
    console.log('Generated order', order);
    this.paymentService.create(order).subscribe(
      res => {
        this.isSaving = false;
        const savedOrder: IOrder | null = res.body ?? null;
        console.log('Received order', savedOrder);
        // TODO Rediriger vers la page "Historique des commandes".
        window.history.go(-2);
      },
      err => {
        console.log('Error :', err);
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
        console.log(this.promoCode);
        this.calcTotalSaved();
      },
      err => {
        console.log('Error :', err);
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
      totalPrice: +(+this.totalPrice - +this.totalSaved).toFixed(2),
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
      orderLines.push(new ProductOrder(NaN, line.quantity, line.product!.price! * line.quantity!, line.product, null));
    }

    return orderLines;
  }
}
