import * as dayjs from 'dayjs';

export interface IProduto {
  id?: number;
  nome?: string;
  descricao?: string | null;
  fotoUrl?: string | null;
  sku?: string | null;
  ean?: string | null;
  criado?: dayjs.Dayjs | null;
  preco?: number | null;
  precoPromocional?: number | null;
  totalEstoque?: number | null;
}

export class Produto implements IProduto {
  constructor(
    public id?: number,
    public nome?: string,
    public descricao?: string | null,
    public fotoUrl?: string | null,
    public sku?: string | null,
    public ean?: string | null,
    public criado?: dayjs.Dayjs | null,
    public preco?: number | null,
    public precoPromocional?: number | null,
    public totalEstoque?: number | null
  ) {}
}

export function getProdutoIdentifier(produto: IProduto): number | undefined {
  return produto.id;
}
