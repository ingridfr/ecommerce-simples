jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { EnderecoService } from '../service/endereco.service';
import { IEndereco, Endereco } from '../endereco.model';
import { IUsuario } from 'app/entities/usuario/usuario.model';
import { UsuarioService } from 'app/entities/usuario/service/usuario.service';

import { EnderecoUpdateComponent } from './endereco-update.component';

describe('Component Tests', () => {
  describe('Endereco Management Update Component', () => {
    let comp: EnderecoUpdateComponent;
    let fixture: ComponentFixture<EnderecoUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let enderecoService: EnderecoService;
    let usuarioService: UsuarioService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [EnderecoUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(EnderecoUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(EnderecoUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      enderecoService = TestBed.inject(EnderecoService);
      usuarioService = TestBed.inject(UsuarioService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Usuario query and add missing value', () => {
        const endereco: IEndereco = { id: 456 };
        const usuario: IUsuario = { id: 5803 };
        endereco.usuario = usuario;

        const usuarioCollection: IUsuario[] = [{ id: 31094 }];
        jest.spyOn(usuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: usuarioCollection })));
        const additionalUsuarios = [usuario];
        const expectedCollection: IUsuario[] = [...additionalUsuarios, ...usuarioCollection];
        jest.spyOn(usuarioService, 'addUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ endereco });
        comp.ngOnInit();

        expect(usuarioService.query).toHaveBeenCalled();
        expect(usuarioService.addUsuarioToCollectionIfMissing).toHaveBeenCalledWith(usuarioCollection, ...additionalUsuarios);
        expect(comp.usuariosSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const endereco: IEndereco = { id: 456 };
        const usuario: IUsuario = { id: 69298 };
        endereco.usuario = usuario;

        activatedRoute.data = of({ endereco });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(endereco));
        expect(comp.usuariosSharedCollection).toContain(usuario);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Endereco>>();
        const endereco = { id: 123 };
        jest.spyOn(enderecoService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ endereco });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: endereco }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(enderecoService.update).toHaveBeenCalledWith(endereco);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Endereco>>();
        const endereco = new Endereco();
        jest.spyOn(enderecoService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ endereco });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: endereco }));
        saveSubject.complete();

        // THEN
        expect(enderecoService.create).toHaveBeenCalledWith(endereco);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Endereco>>();
        const endereco = { id: 123 };
        jest.spyOn(enderecoService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ endereco });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(enderecoService.update).toHaveBeenCalledWith(endereco);
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
