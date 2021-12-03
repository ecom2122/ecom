import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IProductCart } from 'app/entities/product-cart/product-cart.model';
import {ICart} from "../../../entities/cart/cart.model";

export type EntityResponseType = HttpResponse<IProductCart>;
export type EntityArrayResponseType = HttpResponse<IProductCart[]>;

@Injectable({ providedIn: 'root' })
export class ProductToCartService {
  //Attributes Cart
  public productsMap: Map<number, IProductCart> = new Map();
  cart?: ICart | null;

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cart/product');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(idProduct: number): Observable<EntityResponseType> {
    return this.http.post<IProductCart>(`${this.resourceUrl}/${idProduct}`, null, { observe: 'response' });
  }
}
