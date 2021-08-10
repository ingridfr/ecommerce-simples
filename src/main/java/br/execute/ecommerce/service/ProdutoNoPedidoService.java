package br.execute.ecommerce.service;

import br.execute.ecommerce.domain.ProdutoNoPedido;
import br.execute.ecommerce.repository.ProdutoNoPedidoRepository;
import br.execute.ecommerce.service.dto.ProdutoNoPedidoDTO;
import br.execute.ecommerce.service.mapper.ProdutoNoPedidoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProdutoNoPedido}.
 */
@Service
@Transactional
public class ProdutoNoPedidoService {

    private final Logger log = LoggerFactory.getLogger(ProdutoNoPedidoService.class);

    private final ProdutoNoPedidoRepository produtoNoPedidoRepository;

    private final ProdutoNoPedidoMapper produtoNoPedidoMapper;

    public ProdutoNoPedidoService(ProdutoNoPedidoRepository produtoNoPedidoRepository, ProdutoNoPedidoMapper produtoNoPedidoMapper) {
        this.produtoNoPedidoRepository = produtoNoPedidoRepository;
        this.produtoNoPedidoMapper = produtoNoPedidoMapper;
    }

    /**
     * Save a produtoNoPedido.
     *
     * @param produtoNoPedidoDTO the entity to save.
     * @return the persisted entity.
     */
    public ProdutoNoPedidoDTO save(ProdutoNoPedidoDTO produtoNoPedidoDTO) {
        log.debug("Request to save ProdutoNoPedido : {}", produtoNoPedidoDTO);
        ProdutoNoPedido produtoNoPedido = produtoNoPedidoMapper.toEntity(produtoNoPedidoDTO);
        produtoNoPedido = produtoNoPedidoRepository.save(produtoNoPedido);
        return produtoNoPedidoMapper.toDto(produtoNoPedido);
    }

    /**
     * Partially update a produtoNoPedido.
     *
     * @param produtoNoPedidoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProdutoNoPedidoDTO> partialUpdate(ProdutoNoPedidoDTO produtoNoPedidoDTO) {
        log.debug("Request to partially update ProdutoNoPedido : {}", produtoNoPedidoDTO);

        return produtoNoPedidoRepository
            .findById(produtoNoPedidoDTO.getId())
            .map(
                existingProdutoNoPedido -> {
                    produtoNoPedidoMapper.partialUpdate(existingProdutoNoPedido, produtoNoPedidoDTO);

                    return existingProdutoNoPedido;
                }
            )
            .map(produtoNoPedidoRepository::save)
            .map(produtoNoPedidoMapper::toDto);
    }

    /**
     * Get all the produtoNoPedidos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProdutoNoPedidoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProdutoNoPedidos");
        return produtoNoPedidoRepository.findAll(pageable).map(produtoNoPedidoMapper::toDto);
    }

    /**
     * Get one produtoNoPedido by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProdutoNoPedidoDTO> findOne(Long id) {
        log.debug("Request to get ProdutoNoPedido : {}", id);
        return produtoNoPedidoRepository.findById(id).map(produtoNoPedidoMapper::toDto);
    }

    /**
     * Delete the produtoNoPedido by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ProdutoNoPedido : {}", id);
        produtoNoPedidoRepository.deleteById(id);
    }
}
