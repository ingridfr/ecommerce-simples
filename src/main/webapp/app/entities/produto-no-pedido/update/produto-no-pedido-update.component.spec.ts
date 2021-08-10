jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ProdutoNoPedidoService } from '../service/produto-no-pedido.service';
import { IProdutoNoPedido, ProdutoNoPedido } from '../produto-no-pedido.model';
import { IProduto } from 'app/entities/produto/produto.model';
import { ProdutoService } from 'app/entities/produto/service/produto.service';
import { IPedido } from 'app/entities/pedido/pedido.model';
import { PedidoService } from 'app/entities/pedido/service/pedido.service';

import { ProdutoNoPedidoUpdateComponent } from './produto-no-pedido-update.component';

describe('Component Tests', () => {
  describe('ProdutoNoPedido Management Update Component', () => {
    let comp: ProdutoNoPedidoUpdateComponent;
    let fixture: ComponentFixture<ProdutoNoPedidoUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let produtoNoPedidoService: ProdutoNoPedidoService;
    let produtoService: ProdutoService;
    let pedidoService: PedidoService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ProdutoNoPedidoUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ProdutoNoPedidoUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ProdutoNoPedidoUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      produtoNoPedidoService = TestBed.inject(ProdutoNoPedidoService);
      produtoService = TestBed.inject(ProdutoService);
      pedidoService = TestBed.inject(PedidoService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Produto query and add missing value', () => {
        const produtoNoPedido: IProdutoNoPedido = { id: 456 };
        const produto: IProduto = { id: 42281 };
        produtoNoPedido.produto = produto;

        const produtoCollection: IProduto[] = [{ id: 86135 }];
        jest.spyOn(produtoService, 'query').mockReturnValue(of(new HttpResponse({ body: produtoCollection })));
        const additionalProdutos = [produto];
        const expectedCollection: IProduto[] = [...additionalProdutos, ...produtoCollection];
        jest.spyOn(produtoService, 'addProdutoToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ produtoNoPedido });
        comp.ngOnInit();

        expect(produtoService.query).toHaveBeenCalled();
        expect(produtoService.addProdutoToCollectionIfMissing).toHaveBeenCalledWith(produtoCollection, ...additionalProdutos);
        expect(comp.produtosSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Pedido query and add missing value', () => {
        const produtoNoPedido: IProdutoNoPedido = { id: 456 };
        const produto: IPedido = { id: 55593 };
        produtoNoPedido.produto = produto;

        const pedidoCollection: IPedido[] = [{ id: 48875 }];
        jest.spyOn(pedidoService, 'query').mockReturnValue(of(new HttpResponse({ body: pedidoCollection })));
        const additionalPedidos = [produto];
        const expectedCollection: IPedido[] = [...additionalPedidos, ...pedidoCollection];
        jest.spyOn(pedidoService, 'addPedidoToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ produtoNoPedido });
        comp.ngOnInit();

        expect(pedidoService.query).toHaveBeenCalled();
        expect(pedidoService.addPedidoToCollectionIfMissing).toHaveBeenCalledWith(pedidoCollection, ...additionalPedidos);
        expect(comp.pedidosSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const produtoNoPedido: IProdutoNoPedido = { id: 456 };
        const produto: IProduto = { id: 87441 };
        produtoNoPedido.produto = produto;
        const produto: IPedido = { id: 48227 };
        produtoNoPedido.produto = produto;

        activatedRoute.data = of({ produtoNoPedido });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(produtoNoPedido));
        expect(comp.produtosSharedCollection).toContain(produto);
        expect(comp.pedidosSharedCollection).toContain(produto);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ProdutoNoPedido>>();
        const produtoNoPedido = { id: 123 };
        jest.spyOn(produtoNoPedidoService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ produtoNoPedido });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: produtoNoPedido }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(produtoNoPedidoService.update).toHaveBeenCalledWith(produtoNoPedido);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ProdutoNoPedido>>();
        const produtoNoPedido = new ProdutoNoPedido();
        jest.spyOn(produtoNoPedidoService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ produtoNoPedido });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: produtoNoPedido }));
        saveSubject.complete();

        // THEN
        expect(produtoNoPedidoService.create).toHaveBeenCalledWith(produtoNoPedido);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<ProdutoNoPedido>>();
        const produtoNoPedido = { id: 123 };
        jest.spyOn(produtoNoPedidoService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ produtoNoPedido });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(produtoNoPedidoService.update).toHaveBeenCalledWith(produtoNoPedido);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackProdutoById', () => {
        it('Should return tracked Produto primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackProdutoById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackPedidoById', () => {
        it('Should return tracked Pedido primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackPedidoById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
