package br.execute.ecommerce.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import br.execute.ecommerce.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProdutoNoPedidoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProdutoNoPedidoDTO.class);
        ProdutoNoPedidoDTO produtoNoPedidoDTO1 = new ProdutoNoPedidoDTO();
        produtoNoPedidoDTO1.setId(1L);
        ProdutoNoPedidoDTO produtoNoPedidoDTO2 = new ProdutoNoPedidoDTO();
        assertThat(produtoNoPedidoDTO1).isNotEqualTo(produtoNoPedidoDTO2);
        produtoNoPedidoDTO2.setId(produtoNoPedidoDTO1.getId());
        assertThat(produtoNoPedidoDTO1).isEqualTo(produtoNoPedidoDTO2);
        produtoNoPedidoDTO2.setId(2L);
        assertThat(produtoNoPedidoDTO1).isNotEqualTo(produtoNoPedidoDTO2);
        produtoNoPedidoDTO1.setId(null);
        assertThat(produtoNoPedidoDTO1).isNotEqualTo(produtoNoPedidoDTO2);
    }
}
