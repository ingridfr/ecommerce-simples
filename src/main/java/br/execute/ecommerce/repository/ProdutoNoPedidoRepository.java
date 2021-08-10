package br.execute.ecommerce.repository;

import br.execute.ecommerce.domain.ProdutoNoPedido;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProdutoNoPedido entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProdutoNoPedidoRepository extends JpaRepository<ProdutoNoPedido, Long>, JpaSpecificationExecutor<ProdutoNoPedido> {}
