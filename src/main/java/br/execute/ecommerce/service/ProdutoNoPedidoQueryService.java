package br.execute.ecommerce.service;

import br.execute.ecommerce.domain.*; // for static metamodels
import br.execute.ecommerce.domain.ProdutoNoPedido;
import br.execute.ecommerce.repository.ProdutoNoPedidoRepository;
import br.execute.ecommerce.service.criteria.ProdutoNoPedidoCriteria;
import br.execute.ecommerce.service.dto.ProdutoNoPedidoDTO;
import br.execute.ecommerce.service.mapper.ProdutoNoPedidoMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProdutoNoPedido} entities in the database.
 * The main input is a {@link ProdutoNoPedidoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProdutoNoPedidoDTO} or a {@link Page} of {@link ProdutoNoPedidoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProdutoNoPedidoQueryService extends QueryService<ProdutoNoPedido> {

    private final Logger log = LoggerFactory.getLogger(ProdutoNoPedidoQueryService.class);

    private final ProdutoNoPedidoRepository produtoNoPedidoRepository;

    private final ProdutoNoPedidoMapper produtoNoPedidoMapper;

    public ProdutoNoPedidoQueryService(ProdutoNoPedidoRepository produtoNoPedidoRepository, ProdutoNoPedidoMapper produtoNoPedidoMapper) {
        this.produtoNoPedidoRepository = produtoNoPedidoRepository;
        this.produtoNoPedidoMapper = produtoNoPedidoMapper;
    }

    /**
     * Return a {@link List} of {@link ProdutoNoPedidoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProdutoNoPedidoDTO> findByCriteria(ProdutoNoPedidoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProdutoNoPedido> specification = createSpecification(criteria);
        return produtoNoPedidoMapper.toDto(produtoNoPedidoRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProdutoNoPedidoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProdutoNoPedidoDTO> findByCriteria(ProdutoNoPedidoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProdutoNoPedido> specification = createSpecification(criteria);
        return produtoNoPedidoRepository.findAll(specification, page).map(produtoNoPedidoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProdutoNoPedidoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProdutoNoPedido> specification = createSpecification(criteria);
        return produtoNoPedidoRepository.count(specification);
    }

    /**
     * Function to convert {@link ProdutoNoPedidoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProdutoNoPedido> createSpecification(ProdutoNoPedidoCriteria criteria) {
        Specification<ProdutoNoPedido> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProdutoNoPedido_.id));
            }
            if (criteria.getQuantidade() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantidade(), ProdutoNoPedido_.quantidade));
            }
            if (criteria.getPreco() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPreco(), ProdutoNoPedido_.preco));
            }
            if (criteria.getCriado() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCriado(), ProdutoNoPedido_.criado));
            }
            if (criteria.getProdutoId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProdutoId(),
                            root -> root.join(ProdutoNoPedido_.produto, JoinType.LEFT).get(Produto_.id)
                        )
                    );
            }
            if (criteria.getPedidoId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPedidoId(),
                            root -> root.join(ProdutoNoPedido_.pedido, JoinType.LEFT).get(Pedido_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
