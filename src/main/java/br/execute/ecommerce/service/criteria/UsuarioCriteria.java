package br.execute.ecommerce.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link br.execute.ecommerce.domain.Usuario} entity. This class is used
 * in {@link br.execute.ecommerce.web.rest.UsuarioResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /usuarios?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UsuarioCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private StringFilter cpf;

    private LocalDateFilter dataNascimento;

    private ZonedDateTimeFilter criado;

    public UsuarioCriteria() {}

    public UsuarioCriteria(UsuarioCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nome = other.nome == null ? null : other.nome.copy();
        this.cpf = other.cpf == null ? null : other.cpf.copy();
        this.dataNascimento = other.dataNascimento == null ? null : other.dataNascimento.copy();
        this.criado = other.criado == null ? null : other.criado.copy();
    }

    @Override
    public UsuarioCriteria copy() {
        return new UsuarioCriteria(this);
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

    public StringFilter getCpf() {
        return cpf;
    }

    public StringFilter cpf() {
        if (cpf == null) {
            cpf = new StringFilter();
        }
        return cpf;
    }

    public void setCpf(StringFilter cpf) {
        this.cpf = cpf;
    }

    public LocalDateFilter getDataNascimento() {
        return dataNascimento;
    }

    public LocalDateFilter dataNascimento() {
        if (dataNascimento == null) {
            dataNascimento = new LocalDateFilter();
        }
        return dataNascimento;
    }

    public void setDataNascimento(LocalDateFilter dataNascimento) {
        this.dataNascimento = dataNascimento;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UsuarioCriteria that = (UsuarioCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(cpf, that.cpf) &&
            Objects.equals(dataNascimento, that.dataNascimento) &&
            Objects.equals(criado, that.criado)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, cpf, dataNascimento, criado);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UsuarioCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nome != null ? "nome=" + nome + ", " : "") +
            (cpf != null ? "cpf=" + cpf + ", " : "") +
            (dataNascimento != null ? "dataNascimento=" + dataNascimento + ", " : "") +
            (criado != null ? "criado=" + criado + ", " : "") +
            "}";
    }
}
