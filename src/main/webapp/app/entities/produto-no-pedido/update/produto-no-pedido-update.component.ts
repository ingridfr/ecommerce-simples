import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IProdutoNoPedido, ProdutoNoPedido } from '../produto-no-pedido.model';
import { ProdutoNoPedidoService } from '../service/produto-no-pedido.service';
import { IProduto } from 'app/entities/produto/produto.model';
import { ProdutoService } from 'app/entities/produto/service/produto.service';
import { IPedido } from 'app/entities/pedido/pedido.model';
import { PedidoService } from 'app/entities/pedido/service/pedido.service';

@Component({
  selector: 'jhi-produto-no-pedido-update',
  templateUrl: './produto-no-pedido-update.component.html',
})
export class ProdutoNoPedidoUpdateComponent implements OnInit {
  isSaving = false;

  produtosSharedCollection: IProduto[] = [];
  pedidosSharedCollection: IPedido[] = [];

  editForm = this.fb.group({
    id: [],
    quantidade: [],
    preco: [],
    criado: [],
    produto: [],
    produto: [],
  });

  constructor(
    protected produtoNoPedidoService: ProdutoNoPedidoService,
    protected produtoService: ProdutoService,
    protected pedidoService: PedidoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ produtoNoPedido }) => {
      if (produtoNoPedido.id === undefined) {
        const today = dayjs().startOf('day');
        produtoNoPedido.criado = today;
      }

      this.updateForm(produtoNoPedido);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const produtoNoPedido = this.createFromForm();
    if (produtoNoPedido.id !== undefined) {
      this.subscribeToSaveResponse(this.produtoNoPedidoService.update(produtoNoPedido));
    } else {
      this.subscribeToSaveResponse(this.produtoNoPedidoService.create(produtoNoPedido));
    }
  }

  trackProdutoById(index: number, item: IProduto): number {
    return item.id!;
  }

  trackPedidoById(index: number, item: IPedido): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProdutoNoPedido>>): void {
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

  protected updateForm(produtoNoPedido: IProdutoNoPedido): void {
    this.editForm.patchValue({
      id: produtoNoPedido.id,
      quantidade: produtoNoPedido.quantidade,
      preco: produtoNoPedido.preco,
      criado: produtoNoPedido.criado ? produtoNoPedido.criado.format(DATE_TIME_FORMAT) : null,
      produto: produtoNoPedido.produto,
      produto: produtoNoPedido.produto,
    });

    this.produtosSharedCollection = this.produtoService.addProdutoToCollectionIfMissing(
      this.produtosSharedCollection,
      produtoNoPedido.produto
    );
    this.pedidosSharedCollection = this.pedidoService.addPedidoToCollectionIfMissing(this.pedidosSharedCollection, produtoNoPedido.produto);
  }

  protected loadRelationshipsOptions(): void {
    this.produtoService
      .query()
      .pipe(map((res: HttpResponse<IProduto[]>) => res.body ?? []))
      .pipe(
        map((produtos: IProduto[]) => this.produtoService.addProdutoToCollectionIfMissing(produtos, this.editForm.get('produto')!.value))
      )
      .subscribe((produtos: IProduto[]) => (this.produtosSharedCollection = produtos));

    this.pedidoService
      .query()
      .pipe(map((res: HttpResponse<IPedido[]>) => res.body ?? []))
      .pipe(map((pedidos: IPedido[]) => this.pedidoService.addPedidoToCollectionIfMissing(pedidos, this.editForm.get('produto')!.value)))
      .subscribe((pedidos: IPedido[]) => (this.pedidosSharedCollection = pedidos));
  }

  protected createFromForm(): IProdutoNoPedido {
    return {
      ...new ProdutoNoPedido(),
      id: this.editForm.get(['id'])!.value,
      quantidade: this.editForm.get(['quantidade'])!.value,
      preco: this.editForm.get(['preco'])!.value,
      criado: this.editForm.get(['criado'])!.value ? dayjs(this.editForm.get(['criado'])!.value, DATE_TIME_FORMAT) : undefined,
      produto: this.editForm.get(['produto'])!.value,
      produto: this.editForm.get(['produto'])!.value,
    };
  }
}
