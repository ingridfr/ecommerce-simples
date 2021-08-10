package br.execute.ecommerce.service;

import br.execute.ecommerce.domain.*; // for static metamodels
import br.execute.ecommerce.domain.Pedido;
import br.execute.ecommerce.repository.PedidoRepository;
import br.execute.ecommerce.service.criteria.PedidoCriteria;
import br.execute.ecommerce.service.dto.PedidoDTO;
import br.execute.ecommerce.service.mapper.PedidoMapper;
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
 * Service for executing complex queries for {@link Pedido} entities in the database.
 * The main input is a {@link PedidoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PedidoDTO} or a {@link Page} of {@link PedidoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PedidoQueryService extends QueryService<Pedido> {

    private final Logger log = LoggerFactory.getLogger(PedidoQueryService.class);

    private final PedidoRepository pedidoRepository;

    private final PedidoMapper pedidoMapper;

    public PedidoQueryService(PedidoRepository pedidoRepository, PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
    }

    /**
     * Return a {@link List} of {@link PedidoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PedidoDTO> findByCriteria(PedidoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Pedido> specification = createSpecification(criteria);
        return pedidoMapper.toDto(pedidoRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PedidoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PedidoDTO> findByCriteria(PedidoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Pedido> specification = createSpecification(criteria);
        return pedidoRepository.findAll(specification, page).map(pedidoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PedidoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Pedido> specification = createSpecification(criteria);
        return pedidoRepository.count(specification);
    }

    /**
     * Function to convert {@link PedidoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Pedido> createSpecification(PedidoCriteria criteria) {
        Specification<Pedido> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Pedido_.id));
            }
            if (criteria.getCriado() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCriado(), Pedido_.criado));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Pedido_.status));
            }
            if (criteria.getPrecoTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrecoTotal(), Pedido_.precoTotal));
            }
            if (criteria.getCodigoPagamento() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCodigoPagamento(), Pedido_.codigoPagamento));
            }
            if (criteria.getUsuarioId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUsuarioId(), root -> root.join(Pedido_.usuario, JoinType.LEFT).get(Usuario_.id))
                    );
            }
            if (criteria.getEnderecoId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEnderecoId(), root -> root.join(Pedido_.endereco, JoinType.LEFT).get(Usuario_.id))
                    );
            }
        }
        return specification;
    }
}
