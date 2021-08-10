package br.execute.ecommerce.repository;

import br.execute.ecommerce.domain.Produto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Produto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {}
