jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ProdutoService } from '../service/produto.service';
import { IProduto, Produto } from '../produto.model';

import { ProdutoUpdateComponent } from './produto-update.component';

describe('Component Tests', () => {
  describe('Produto Management Update Component', () => {
    let comp: ProdutoUpdateComponent;
    let fixture: ComponentFixture<ProdutoUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let produtoService: ProdutoService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ProdutoUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ProdutoUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ProdutoUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      produtoService = TestBed.inject(ProdutoService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const produto: IProduto = { id: 456 };

        activatedRoute.data = of({ produto });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(produto));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Produto>>();
        const produto = { id: 123 };
        jest.spyOn(produtoService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ produto });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: produto }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(produtoService.update).toHaveBeenCalledWith(produto);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Produto>>();
        const produto = new Produto();
        jest.spyOn(produtoService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ produto });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: produto }));
        saveSubject.complete();

        // THEN
        expect(produtoService.create).toHaveBeenCalledWith(produto);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Produto>>();
        const produto = { id: 123 };
        jest.spyOn(produtoService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ produto });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(produtoService.update).toHaveBeenCalledWith(produto);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
