package br.execute.ecommerce.web.rest;

import br.execute.ecommerce.repository.ProdutoNoPedidoRepository;
import br.execute.ecommerce.service.ProdutoNoPedidoQueryService;
import br.execute.ecommerce.service.ProdutoNoPedidoService;
import br.execute.ecommerce.service.criteria.ProdutoNoPedidoCriteria;
import br.execute.ecommerce.service.dto.ProdutoNoPedidoDTO;
import br.execute.ecommerce.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link br.execute.ecommerce.domain.ProdutoNoPedido}.
 */
@RestController
@RequestMapping("/api")
public class ProdutoNoPedidoResource {

    private final Logger log = LoggerFactory.getLogger(ProdutoNoPedidoResource.class);

    private static final String ENTITY_NAME = "produtoNoPedido";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProdutoNoPedidoService produtoNoPedidoService;

    private final ProdutoNoPedidoRepository produtoNoPedidoRepository;

    private final ProdutoNoPedidoQueryService produtoNoPedidoQueryService;

    public ProdutoNoPedidoResource(
        ProdutoNoPedidoService produtoNoPedidoService,
        ProdutoNoPedidoRepository produtoNoPedidoRepository,
        ProdutoNoPedidoQueryService produtoNoPedidoQueryService
    ) {
        this.produtoNoPedidoService = produtoNoPedidoService;
        this.produtoNoPedidoRepository = produtoNoPedidoRepository;
        this.produtoNoPedidoQueryService = produtoNoPedidoQueryService;
    }

    /**
     * {@code POST  /produto-no-pedidos} : Create a new produtoNoPedido.
     *
     * @param produtoNoPedidoDTO the produtoNoPedidoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new produtoNoPedidoDTO, or with status {@code 400 (Bad Request)} if the produtoNoPedido has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/produto-no-pedidos")
    public ResponseEntity<ProdutoNoPedidoDTO> createProdutoNoPedido(@RequestBody ProdutoNoPedidoDTO produtoNoPedidoDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProdutoNoPedido : {}", produtoNoPedidoDTO);
        if (produtoNoPedidoDTO.getId() != null) {
            throw new BadRequestAlertException("A new produtoNoPedido cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProdutoNoPedidoDTO result = produtoNoPedidoService.save(produtoNoPedidoDTO);
        return ResponseEntity
            .created(new URI("/api/produto-no-pedidos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /produto-no-pedidos/:id} : Updates an existing produtoNoPedido.
     *
     * @param id the id of the produtoNoPedidoDTO to save.
     * @param produtoNoPedidoDTO the produtoNoPedidoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produtoNoPedidoDTO,
     * or with status {@code 400 (Bad Request)} if the produtoNoPedidoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the produtoNoPedidoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/produto-no-pedidos/{id}")
    public ResponseEntity<ProdutoNoPedidoDTO> updateProdutoNoPedido(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProdutoNoPedidoDTO produtoNoPedidoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProdutoNoPedido : {}, {}", id, produtoNoPedidoDTO);
        if (produtoNoPedidoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produtoNoPedidoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!produtoNoPedidoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProdutoNoPedidoDTO result = produtoNoPedidoService.save(produtoNoPedidoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, produtoNoPedidoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /produto-no-pedidos/:id} : Partial updates given fields of an existing produtoNoPedido, field will ignore if it is null
     *
     * @param id the id of the produtoNoPedidoDTO to save.
     * @param produtoNoPedidoDTO the produtoNoPedidoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produtoNoPedidoDTO,
     * or with status {@code 400 (Bad Request)} if the produtoNoPedidoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the produtoNoPedidoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the produtoNoPedidoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/produto-no-pedidos/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ProdutoNoPedidoDTO> partialUpdateProdutoNoPedido(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProdutoNoPedidoDTO produtoNoPedidoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProdutoNoPedido partially : {}, {}", id, produtoNoPedidoDTO);
        if (produtoNoPedidoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produtoNoPedidoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!produtoNoPedidoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProdutoNoPedidoDTO> result = produtoNoPedidoService.partialUpdate(produtoNoPedidoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, produtoNoPedidoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /produto-no-pedidos} : get all the produtoNoPedidos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of produtoNoPedidos in body.
     */
    @GetMapping("/produto-no-pedidos")
    public ResponseEntity<List<ProdutoNoPedidoDTO>> getAllProdutoNoPedidos(ProdutoNoPedidoCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProdutoNoPedidos by criteria: {}", criteria);
        Page<ProdutoNoPedidoDTO> page = produtoNoPedidoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /produto-no-pedidos/count} : count all the produtoNoPedidos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/produto-no-pedidos/count")
    public ResponseEntity<Long> countProdutoNoPedidos(ProdutoNoPedidoCriteria criteria) {
        log.debug("REST request to count ProdutoNoPedidos by criteria: {}", criteria);
        return ResponseEntity.ok().body(produtoNoPedidoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /produto-no-pedidos/:id} : get the "id" produtoNoPedido.
     *
     * @param id the id of the produtoNoPedidoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the produtoNoPedidoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/produto-no-pedidos/{id}")
    public ResponseEntity<ProdutoNoPedidoDTO> getProdutoNoPedido(@PathVariable Long id) {
        log.debug("REST request to get ProdutoNoPedido : {}", id);
        Optional<ProdutoNoPedidoDTO> produtoNoPedidoDTO = produtoNoPedidoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(produtoNoPedidoDTO);
    }

    /**
     * {@code DELETE  /produto-no-pedidos/:id} : delete the "id" produtoNoPedido.
     *
     * @param id the id of the produtoNoPedidoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/produto-no-pedidos/{id}")
    public ResponseEntity<Void> deleteProdutoNoPedido(@PathVariable Long id) {
        log.debug("REST request to delete ProdutoNoPedido : {}", id);
        produtoNoPedidoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
