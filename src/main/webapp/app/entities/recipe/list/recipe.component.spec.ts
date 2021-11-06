import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { RecipeService } from '../service/recipe.service';

import { RecipeComponent } from './recipe.component';

describe('Recipe Management Component', () => {
  let comp: RecipeComponent;
  let fixture: ComponentFixture<RecipeComponent>;
  let service: RecipeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [RecipeComponent],
    })
      .overrideTemplate(RecipeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RecipeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RecipeService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.recipes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
