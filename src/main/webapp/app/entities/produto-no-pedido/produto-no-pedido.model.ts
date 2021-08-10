import * as dayjs from 'dayjs';
import { IProduto } from 'app/entities/produto/produto.model';
import { IPedido } from 'app/entities/pedido/pedido.model';

export interface IProdutoNoPedido {
  id?: number;
  quantidade?: number | null;
  preco?: number | null;
  criado?: dayjs.Dayjs | null;
  produto?: IProduto | null;
  produto?: IPedido | null;
}

export class ProdutoNoPedido implements IProdutoNoPedido {
  constructor(
    public id?: number,
    public quantidade?: number | null,
    public preco?: number | null,
    public criado?: dayjs.Dayjs | null,
    public produto?: IProduto | null,
    public produto?: IPedido | null
  ) {}
}

export function getProdutoNoPedidoIdentifier(produtoNoPedido: IProdutoNoPedido): number | undefined {
  return produtoNoPedido.id;
}
