package br.execute.ecommerce.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.execute.ecommerce.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProdutoNoPedidoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProdutoNoPedido.class);
        ProdutoNoPedido produtoNoPedido1 = new ProdutoNoPedido();
        produtoNoPedido1.setId(1L);
        ProdutoNoPedido produtoNoPedido2 = new ProdutoNoPedido();
        produtoNoPedido2.setId(produtoNoPedido1.getId());
        assertThat(produtoNoPedido1).isEqualTo(produtoNoPedido2);
        produtoNoPedido2.setId(2L);
        assertThat(produtoNoPedido1).isNotEqualTo(produtoNoPedido2);
        produtoNoPedido1.setId(null);
        assertThat(produtoNoPedido1).isNotEqualTo(produtoNoPedido2);
    }
}
