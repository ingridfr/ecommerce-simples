package br.execute.ecommerce.domain;

import br.execute.ecommerce.domain.enumeration.StatusPedido;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A Pedido.
 */
@Entity
@Table(name = "pedido")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "criado")
    private ZonedDateTime criado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusPedido status;

    @Column(name = "preco_total")
    private Double precoTotal;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "comentario")
    private String comentario;

    @Column(name = "codigo_pagamento")
    private String codigoPagamento;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Usuario endereco;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido id(Long id) {
        this.id = id;
        return this;
    }

    public ZonedDateTime getCriado() {
        return this.criado;
    }

    public Pedido criado(ZonedDateTime criado) {
        this.criado = criado;
        return this;
    }

    public void setCriado(ZonedDateTime criado) {
        this.criado = criado;
    }

    public StatusPedido getStatus() {
        return this.status;
    }

    public Pedido status(StatusPedido status) {
        this.status = status;
        return this;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public Double getPrecoTotal() {
        return this.precoTotal;
    }

    public Pedido precoTotal(Double precoTotal) {
        this.precoTotal = precoTotal;
        return this;
    }

    public void setPrecoTotal(Double precoTotal) {
        this.precoTotal = precoTotal;
    }

    public String getComentario() {
        return this.comentario;
    }

    public Pedido comentario(String comentario) {
        this.comentario = comentario;
        return this;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getCodigoPagamento() {
        return this.codigoPagamento;
    }

    public Pedido codigoPagamento(String codigoPagamento) {
        this.codigoPagamento = codigoPagamento;
        return this;
    }

    public void setCodigoPagamento(String codigoPagamento) {
        this.codigoPagamento = codigoPagamento;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public Pedido usuario(Usuario usuario) {
        this.setUsuario(usuario);
        return this;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getEndereco() {
        return this.endereco;
    }

    public Pedido endereco(Usuario usuario) {
        this.setEndereco(usuario);
        return this;
    }

    public void setEndereco(Usuario usuario) {
        this.endereco = usuario;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pedido)) {
            return false;
        }
        return id != null && id.equals(((Pedido) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pedido{" +
            "id=" + getId() +
            ", criado='" + getCriado() + "'" +
            ", status='" + getStatus() + "'" +
            ", precoTotal=" + getPrecoTotal() +
            ", comentario='" + getComentario() + "'" +
            ", codigoPagamento='" + getCodigoPagamento() + "'" +
            "}";
    }
}
