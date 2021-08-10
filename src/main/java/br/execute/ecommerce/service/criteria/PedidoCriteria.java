package br.execute.ecommerce.service.criteria;

import br.execute.ecommerce.domain.enumeration.StatusPedido;
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
 * Criteria class for the {@link br.execute.ecommerce.domain.Pedido} entity. This class is used
 * in {@link br.execute.ecommerce.web.rest.PedidoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pedidos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PedidoCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatusPedido
     */
    public static class StatusPedidoFilter extends Filter<StatusPedido> {

        public StatusPedidoFilter() {}

        public StatusPedidoFilter(StatusPedidoFilter filter) {
            super(filter);
        }

        @Override
        public StatusPedidoFilter copy() {
            return new StatusPedidoFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter criado;

    private StatusPedidoFilter status;

    private DoubleFilter precoTotal;

    private StringFilter codigoPagamento;

    private LongFilter usuarioId;

    private LongFilter enderecoId;

    public PedidoCriteria() {}

    public PedidoCriteria(PedidoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.criado = other.criado == null ? null : other.criado.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.precoTotal = other.precoTotal == null ? null : other.precoTotal.copy();
        this.codigoPagamento = other.codigoPagamento == null ? null : other.codigoPagamento.copy();
        this.usuarioId = other.usuarioId == null ? null : other.usuarioId.copy();
        this.enderecoId = other.enderecoId == null ? null : other.enderecoId.copy();
    }

    @Override
    public PedidoCriteria copy() {
        return new PedidoCriteria(this);
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

    public StatusPedidoFilter getStatus() {
        return status;
    }

    public StatusPedidoFilter status() {
        if (status == null) {
            status = new StatusPedidoFilter();
        }
        return status;
    }

    public void setStatus(StatusPedidoFilter status) {
        this.status = status;
    }

    public DoubleFilter getPrecoTotal() {
        return precoTotal;
    }

    public DoubleFilter precoTotal() {
        if (precoTotal == null) {
            precoTotal = new DoubleFilter();
        }
        return precoTotal;
    }

    public void setPrecoTotal(DoubleFilter precoTotal) {
        this.precoTotal = precoTotal;
    }

    public StringFilter getCodigoPagamento() {
        return codigoPagamento;
    }

    public StringFilter codigoPagamento() {
        if (codigoPagamento == null) {
            codigoPagamento = new StringFilter();
        }
        return codigoPagamento;
    }

    public void setCodigoPagamento(StringFilter codigoPagamento) {
        this.codigoPagamento = codigoPagamento;
    }

    public LongFilter getUsuarioId() {
        return usuarioId;
    }

    public LongFilter usuarioId() {
        if (usuarioId == null) {
            usuarioId = new LongFilter();
        }
        return usuarioId;
    }

    public void setUsuarioId(LongFilter usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LongFilter getEnderecoId() {
        return enderecoId;
    }

    public LongFilter enderecoId() {
        if (enderecoId == null) {
            enderecoId = new LongFilter();
        }
        return enderecoId;
    }

    public void setEnderecoId(LongFilter enderecoId) {
        this.enderecoId = enderecoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PedidoCriteria that = (PedidoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(criado, that.criado) &&
            Objects.equals(status, that.status) &&
            Objects.equals(precoTotal, that.precoTotal) &&
            Objects.equals(codigoPagamento, that.codigoPagamento) &&
            Objects.equals(usuarioId, that.usuarioId) &&
            Objects.equals(enderecoId, that.enderecoId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, criado, status, precoTotal, codigoPagamento, usuarioId, enderecoId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PedidoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (criado != null ? "criado=" + criado + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (precoTotal != null ? "precoTotal=" + precoTotal + ", " : "") +
            (codigoPagamento != null ? "codigoPagamento=" + codigoPagamento + ", " : "") +
            (usuarioId != null ? "usuarioId=" + usuarioId + ", " : "") +
            (enderecoId != null ? "enderecoId=" + enderecoId + ", " : "") +
            "}";
    }
}
