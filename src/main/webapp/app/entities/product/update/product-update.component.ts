import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProduct, Product } from '../product.model';
import { ProductService } from '../service/product.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { ITag } from 'app/entities/tag/tag.model';
import { TagService } from 'app/entities/tag/service/tag.service';
import { WeightUnit } from 'app/entities/enumerations/weight-unit.model';
import { FileUpload } from '../../../components/firebase/file-upload.model';
import { FileUploadService } from '../../../components/services/file-upload.service';

@Component({
  selector: 'jhi-product-update',
  templateUrl: './product-update.component.html',
})
export class ProductUpdateComponent implements OnInit {
  isSaving = false;
  weightUnitValues = Object.keys(WeightUnit);

  categoriesSharedCollection: ICategory[] = [];
  tagsSharedCollection: ITag[] = [];

  selectedFiles?: FileList;
  currentFileUpload?: FileUpload;
  percentage = 0;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    description: [],
    quantity: [null, [Validators.required]],
    version: [null, [Validators.required]],
    origin: [],
    brand: [],
    imagePath: [],
    price: [null, [Validators.required]],
    weight: [],
    weightUnit: [],
    category: [],
    relatedCategories: [],
    tags: [],
  });

  constructor(
    protected productService: ProductService,
    protected categoryService: CategoryService,
    protected tagService: TagService,
    protected uploadService: FileUploadService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ product }) => {
      if (product.id == null) {
        product.version = 1;
      }
      this.updateForm(product);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const product = this.createFromForm();
    if (product.id !== undefined) {
      this.subscribeToSaveResponse(this.productService.update(product));
    } else {
      this.subscribeToSaveResponse(this.productService.create(product));
    }
  }

  trackCategoryById(index: number, item: ICategory): number {
    return item.id!;
  }

  trackTagById(index: number, item: ITag): number {
    return item.id!;
  }

  getSelectedCategory(option: ICategory, selectedVals?: ICategory[]): ICategory {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  getSelectedTag(option: ITag, selectedVals?: ITag[]): ITag {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
  }

  upload(): void {
    if (this.selectedFiles) {
      const file: File | null = this.selectedFiles.item(0);
      this.selectedFiles = undefined;

      if (file) {
        this.currentFileUpload = new FileUpload(file, 'products', this.editForm.get('id')?.value);
        const observables = this.uploadService.pushFileToStorage(this.currentFileUpload);
        observables[0].subscribe(percentage => {
          this.percentage = Math.round(percentage ? percentage : 0);
        });
        observables[1].subscribe(filePath =>
          this.editForm.patchValue({
            imagePath: filePath,
          })
        );
      }
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProduct>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(product: IProduct): void {
    this.editForm.patchValue({
      id: product.id,
      name: product.name,
      description: product.description,
      quantity: product.quantity,
      version: product.version,
      origin: product.origin,
      brand: product.brand,
      imagePath: product.imagePath,
      price: product.price,
      weight: product.weight,
      weightUnit: product.weightUnit,
      category: product.category,
      relatedCategories: product.relatedCategories,
      tags: product.tags,
    });

    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing(
      this.categoriesSharedCollection,
      product.category,
      ...(product.relatedCategories ?? [])
    );
    this.tagsSharedCollection = this.tagService.addTagToCollectionIfMissing(this.tagsSharedCollection, ...(product.tags ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing(
            categories,
            this.editForm.get('category')!.value,
            ...(this.editForm.get('relatedCategories')!.value ?? [])
          )
        )
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));

    this.tagService
      .query()
      .pipe(map((res: HttpResponse<ITag[]>) => res.body ?? []))
      .pipe(map((tags: ITag[]) => this.tagService.addTagToCollectionIfMissing(tags, ...(this.editForm.get('tags')!.value ?? []))))
      .subscribe((tags: ITag[]) => (this.tagsSharedCollection = tags));
  }

  protected createFromForm(): IProduct {
    return {
      ...new Product(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      quantity: this.editForm.get(['quantity'])!.value,
      version: this.editForm.get(['version'])!.value,
      origin: this.editForm.get(['origin'])!.value,
      brand: this.editForm.get(['brand'])!.value,
      imagePath: this.editForm.get(['imagePath'])!.value,
      price: this.editForm.get(['price'])!.value,
      weight: this.editForm.get(['weight'])!.value,
      weightUnit: this.editForm.get(['weightUnit'])!.value,
      category: this.editForm.get(['category'])!.value,
      relatedCategories: this.editForm.get(['relatedCategories'])!.value,
      tags: this.editForm.get(['tags'])!.value,
    };
  }
}
