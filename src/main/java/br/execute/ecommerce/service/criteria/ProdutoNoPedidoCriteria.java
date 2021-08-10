package br.execute.ecommerce.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link br.execute.ecommerce.domain.ProdutoNoPedido} entity. This class is used
 * in {@link br.execute.ecommerce.web.rest.ProdutoNoPedidoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /produto-no-pedidos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProdutoNoPedidoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter quantidade;

    private DoubleFilter preco;

    private ZonedDateTimeFilter criado;

    private LongFilter produtoId;

    private LongFilter produtoId;

    public ProdutoNoPedidoCriteria() {}

    public ProdutoNoPedidoCriteria(ProdutoNoPedidoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.quantidade = other.quantidade == null ? null : other.quantidade.copy();
        this.preco = other.preco == null ? null : other.preco.copy();
        this.criado = other.criado == null ? null : other.criado.copy();
        this.produtoId = other.produtoId == null ? null : other.produtoId.copy();
        this.produtoId = other.produtoId == null ? null : other.produtoId.copy();
    }

    @Override
    public ProdutoNoPedidoCriteria copy() {
        return new ProdutoNoPedidoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getQuantidade() {
        return quantidade;
    }

    public IntegerFilter quantidade() {
        if (quantidade == null) {
            quantidade = new IntegerFilter();
        }
        return quantidade;
    }

    public void setQuantidade(IntegerFilter quantidade) {
        this.quantidade = quantidade;
    }

    public DoubleFilter getPreco() {
        return preco;
    }

    public DoubleFilter preco() {
        if (preco == null) {
            preco = new DoubleFilter();
        }
        return preco;
    }

    public void setPreco(DoubleFilter preco) {
        this.preco = preco;
    }

    public ZonedDateTimeFilter getCriado() {
        return criado;
    }

    public ZonedDateTimeFilter criado() {
        if (criado == null) {
            criado = new ZonedDateTimeFilter();
        }
        return criado;
    }

    public void setCriado(ZonedDateTimeFilter criado) {
        this.criado = criado;
    }

    public LongFilter getProdutoId() {
        return produtoId;
    }

    public LongFilter produtoId() {
        if (produtoId == null) {
            produtoId = new LongFilter();
        }
        return produtoId;
    }

    public void setProdutoId(LongFilter produtoId) {
        this.produtoId = produtoId;
    }

    public LongFilter getProdutoId() {
        return produtoId;
    }

    public LongFilter produtoId() {
        if (produtoId == null) {
            produtoId = new LongFilter();
        }
        return produtoId;
    }

    public void setProdutoId(LongFilter produtoId) {
        this.produtoId = produtoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProdutoNoPedidoCriteria that = (ProdutoNoPedidoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantidade, that.quantidade) &&
            Objects.equals(preco, that.preco) &&
            Objects.equals(criado, that.criado) &&
            Objects.equals(produtoId, that.produtoId) &&
            Objects.equals(produtoId, that.produtoId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantidade, preco, criado, produtoId, produtoId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProdutoNoPedidoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (quantidade != null ? "quantidade=" + quantidade + ", " : "") +
            (preco != null ? "preco=" + preco + ", " : "") +
            (criado != null ? "criado=" + criado + ", " : "") +
            (produtoId != null ? "produtoId=" + produtoId + ", " : "") +
            (produtoId != null ? "produtoId=" + produtoId + ", " : "") +
            "}";
    }
}
