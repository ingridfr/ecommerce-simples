package br.execute.ecommerce.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link br.execute.ecommerce.domain.ProdutoNoPedido} entity.
 */
public class ProdutoNoPedidoDTO implements Serializable {

    private Long id;

    private Integer quantidade;

    private Double preco;

    private ZonedDateTime criado;

    private ProdutoDTO produto;

    private PedidoDTO produto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public ZonedDateTime getCriado() {
        return criado;
    }

    public void setCriado(ZonedDateTime criado) {
        this.criado = criado;
    }

    public ProdutoDTO getProduto() {
        return produto;
    }

    public void setProduto(ProdutoDTO produto) {
        this.produto = produto;
    }

    public PedidoDTO getProduto() {
        return produto;
    }

    public void setProduto(PedidoDTO produto) {
        this.produto = produto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProdutoNoPedidoDTO)) {
            return false;
        }

        ProdutoNoPedidoDTO produtoNoPedidoDTO = (ProdutoNoPedidoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, produtoNoPedidoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProdutoNoPedidoDTO{" +
            "id=" + getId() +
            ", quantidade=" + getQuantidade() +
            ", preco=" + getPreco() +
            ", criado='" + getCriado() + "'" +
            ", produto=" + getProduto() +
            ", produto=" + getProduto() +
            "}";
    }
}
