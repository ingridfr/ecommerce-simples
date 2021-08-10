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
 * Criteria class for the {@link br.execute.ecommerce.domain.Produto} entity. This class is used
 * in {@link br.execute.ecommerce.web.rest.ProdutoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /produtos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProdutoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private StringFilter fotoUrl;

    private StringFilter sku;

    private StringFilter ean;

    private ZonedDateTimeFilter criado;

    private DoubleFilter preco;

    private DoubleFilter precoPromocional;

    private IntegerFilter totalEstoque;

    public ProdutoCriteria() {}

    public ProdutoCriteria(ProdutoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nome = other.nome == null ? null : other.nome.copy();
        this.fotoUrl = other.fotoUrl == null ? null : other.fotoUrl.copy();
        this.sku = other.sku == null ? null : other.sku.copy();
        this.ean = other.ean == null ? null : other.ean.copy();
        this.criado = other.criado == null ? null : other.criado.copy();
        this.preco = other.preco == null ? null : other.preco.copy();
        this.precoPromocional = other.precoPromocional == null ? null : other.precoPromocional.copy();
        this.totalEstoque = other.totalEstoque == null ? null : other.totalEstoque.copy();
    }

    @Override
    public ProdutoCriteria copy() {
        return new ProdutoCriteria(this);
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

    public StringFilter getNome() {
        return nome;
    }

    public StringFilter nome() {
        if (nome == null) {
            nome = new StringFilter();
        }
        return nome;
    }

    public void setNome(StringFilter nome) {
        this.nome = nome;
    }

    public StringFilter getFotoUrl() {
        return fotoUrl;
    }

    public StringFilter fotoUrl() {
        if (fotoUrl == null) {
            fotoUrl = new StringFilter();
        }
        return fotoUrl;
    }

    public void setFotoUrl(StringFilter fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public StringFilter getSku() {
        return sku;
    }

    public StringFilter sku() {
        if (sku == null) {
            sku = new StringFilter();
        }
        return sku;
    }

    public void setSku(StringFilter sku) {
        this.sku = sku;
    }

    public StringFilter getEan() {
        return ean;
    }

    public StringFilter ean() {
        if (ean == null) {
            ean = new StringFilter();
        }
        return ean;
    }

    public void setEan(StringFilter ean) {
        this.ean = ean;
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

    public DoubleFilter getPrecoPromocional() {
        return precoPromocional;
    }

    public DoubleFilter precoPromocional() {
        if (precoPromocional == null) {
            precoPromocional = new DoubleFilter();
        }
        return precoPromocional;
    }

    public void setPrecoPromocional(DoubleFilter precoPromocional) {
        this.precoPromocional = precoPromocional;
    }

    public IntegerFilter getTotalEstoque() {
        return totalEstoque;
    }

    public IntegerFilter totalEstoque() {
        if (totalEstoque == null) {
            totalEstoque = new IntegerFilter();
        }
        return totalEstoque;
    }

    public void setTotalEstoque(IntegerFilter totalEstoque) {
        this.totalEstoque = totalEstoque;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProdutoCriteria that = (ProdutoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(fotoUrl, that.fotoUrl) &&
            Objects.equals(sku, that.sku) &&
            Objects.equals(ean, that.ean) &&
            Objects.equals(criado, that.criado) &&
            Objects.equals(preco, that.preco) &&
            Objects.equals(precoPromocional, that.precoPromocional) &&
            Objects.equals(totalEstoque, that.totalEstoque)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, fotoUrl, sku, ean, criado, preco, precoPromocional, totalEstoque);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProdutoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nome != null ? "nome=" + nome + ", " : "") +
            (fotoUrl != null ? "fotoUrl=" + fotoUrl + ", " : "") +
            (sku != null ? "sku=" + sku + ", " : "") +
            (ean != null ? "ean=" + ean + ", " : "") +
            (criado != null ? "criado=" + criado + ", " : "") +
            (preco != null ? "preco=" + preco + ", " : "") +
            (precoPromocional != null ? "precoPromocional=" + precoPromocional + ", " : "") +
            (totalEstoque != null ? "totalEstoque=" + totalEstoque + ", " : "") +
            "}";
    }
}
