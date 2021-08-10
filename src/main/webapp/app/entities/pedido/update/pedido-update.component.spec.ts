jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PedidoService } from '../service/pedido.service';
import { IPedido, Pedido } from '../pedido.model';
import { IUsuario } from 'app/entities/usuario/usuario.model';
import { UsuarioService } from 'app/entities/usuario/service/usuario.service';

import { PedidoUpdateComponent } from './pedido-update.component';

describe('Component Tests', () => {
  describe('Pedido Management Update Component', () => {
    let comp: PedidoUpdateComponent;
    let fixture: ComponentFixture<PedidoUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let pedidoService: PedidoService;
    let usuarioService: UsuarioService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PedidoUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PedidoUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PedidoUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      pedidoService = TestBed.inject(PedidoService);
      usuarioService = TestBed.inject(UsuarioService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Usuario query and add missing value', () => {
        const pedido: IPedido = { id: 456 };
        const usuario: IUsuario = { id: 3476 };
        pedido.usuario = usuario;
        const endereco: IUsuario = { id: 62789 };
        pedido.endereco = endereco;

        const usuarioCollection: IUsuario[] = [{ id: 70258 }];
        jest.spyOn(usuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: usuarioCollection })));
        const additionalUsuarios = [usuario, endereco];
        const expectedCollection: IUsuario[] = [...additionalUsuarios, ...usuarioCollection];
        jest.spyOn(usuarioService, 'addUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ pedido });
        comp.ngOnInit();

        expect(usuarioService.query).toHaveBeenCalled();
        expect(usuarioService.addUsuarioToCollectionIfMissing).toHaveBeenCalledWith(usuarioCollection, ...additionalUsuarios);
        expect(comp.usuariosSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const pedido: IPedido = { id: 456 };
        const usuario: IUsuario = { id: 97974 };
        pedido.usuario = usuario;
        const endereco: IUsuario = { id: 89601 };
        pedido.endereco = endereco;

        activatedRoute.data = of({ pedido });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(pedido));
        expect(comp.usuariosSharedCollection).toContain(usuario);
        expect(comp.usuariosSharedCollection).toContain(endereco);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Pedido>>();
        const pedido = { id: 123 };
        jest.spyOn(pedidoService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ pedido });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: pedido }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(pedidoService.update).toHaveBeenCalledWith(pedido);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Pedido>>();
        const pedido = new Pedido();
        jest.spyOn(pedidoService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ pedido });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: pedido }));
        saveSubject.complete();

        // THEN
        expect(pedidoService.create).toHaveBeenCalledWith(pedido);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Pedido>>();
        const pedido = { id: 123 };
        jest.spyOn(pedidoService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ pedido });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(pedidoService.update).toHaveBeenCalledWith(pedido);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUsuarioById', () => {
        it('Should return tracked Usuario primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUsuarioById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
