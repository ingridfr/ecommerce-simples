package br.execute.ecommerce.service.dto;

import br.execute.ecommerce.domain.enumeration.StatusPedido;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link br.execute.ecommerce.domain.Pedido} entity.
 */
public class PedidoDTO implements Serializable {

    private Long id;

    private ZonedDateTime criado;

    private StatusPedido status;

    private Double precoTotal;

    @Lob
    private String comentario;

    private String codigoPagamento;

    private UsuarioDTO usuario;

    private UsuarioDTO endereco;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCriado() {
        return criado;
    }

    public void setCriado(ZonedDateTime criado) {
        this.criado = criado;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public Double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(Double precoTotal) {
        this.precoTotal = precoTotal;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getCodigoPagamento() {
        return codigoPagamento;
    }

    public void setCodigoPagamento(String codigoPagamento) {
        this.codigoPagamento = codigoPagamento;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public UsuarioDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(UsuarioDTO endereco) {
        this.endereco = endereco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PedidoDTO)) {
            return false;
        }

        PedidoDTO pedidoDTO = (PedidoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pedidoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PedidoDTO{" +
            "id=" + getId() +
            ", criado='" + getCriado() + "'" +
            ", status='" + getStatus() + "'" +
            ", precoTotal=" + getPrecoTotal() +
            ", comentario='" + getComentario() + "'" +
            ", codigoPagamento='" + getCodigoPagamento() + "'" +
            ", usuario=" + getUsuario() +
            ", endereco=" + getEndereco() +
            "}";
    }
}
