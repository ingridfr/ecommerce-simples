package br.execute.ecommerce.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProdutoNoPedidoMapperTest {

    private ProdutoNoPedidoMapper produtoNoPedidoMapper;

    @BeforeEach
    public void setUp() {
        produtoNoPedidoMapper = new ProdutoNoPedidoMapperImpl();
    }
}
