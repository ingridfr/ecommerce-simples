package br.execute.ecommerce.service;

import br.execute.ecommerce.domain.*; // for static metamodels
import br.execute.ecommerce.domain.Produto;
import br.execute.ecommerce.repository.ProdutoRepository;
import br.execute.ecommerce.service.criteria.ProdutoCriteria;
import br.execute.ecommerce.service.dto.ProdutoDTO;
import br.execute.ecommerce.service.mapper.ProdutoMapper;
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
 * Service for executing complex queries for {@link Produto} entities in the database.
 * The main input is a {@link ProdutoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProdutoDTO} or a {@link Page} of {@link ProdutoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProdutoQueryService extends QueryService<Produto> {

    private final Logger log = LoggerFactory.getLogger(ProdutoQueryService.class);

    private final ProdutoRepository produtoRepository;

    private final ProdutoMapper produtoMapper;

    public ProdutoQueryService(ProdutoRepository produtoRepository, ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
    }

    /**
     * Return a {@link List} of {@link ProdutoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProdutoDTO> findByCriteria(ProdutoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Produto> specification = createSpecification(criteria);
        return produtoMapper.toDto(produtoRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProdutoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProdutoDTO> findByCriteria(ProdutoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Produto> specification = createSpecification(criteria);
        return produtoRepository.findAll(specification, page).map(produtoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProdutoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Produto> specification = createSpecification(criteria);
        return produtoRepository.count(specification);
    }

    /**
     * Function to convert {@link ProdutoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Produto> createSpecification(ProdutoCriteria criteria) {
        Specification<Produto> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Produto_.id));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Produto_.nome));
            }
            if (criteria.getFotoUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFotoUrl(), Produto_.fotoUrl));
            }
            if (criteria.getSku() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSku(), Produto_.sku));
            }
            if (criteria.getEan() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEan(), Produto_.ean));
            }
            if (criteria.getCriado() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCriado(), Produto_.criado));
            }
            if (criteria.getPreco() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPreco(), Produto_.preco));
            }
            if (criteria.getPrecoPromocional() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrecoPromocional(), Produto_.precoPromocional));
            }
            if (criteria.getTotalEstoque() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalEstoque(), Produto_.totalEstoque));
            }
        }
        return specification;
    }
}
