package br.execute.ecommerce.web.rest;

import static br.execute.ecommerce.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.execute.ecommerce.IntegrationTest;
import br.execute.ecommerce.domain.Pedido;
import br.execute.ecommerce.domain.Produto;
import br.execute.ecommerce.domain.ProdutoNoPedido;
import br.execute.ecommerce.repository.ProdutoNoPedidoRepository;
import br.execute.ecommerce.service.criteria.ProdutoNoPedidoCriteria;
import br.execute.ecommerce.service.dto.ProdutoNoPedidoDTO;
import br.execute.ecommerce.service.mapper.ProdutoNoPedidoMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProdutoNoPedidoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProdutoNoPedidoResourceIT {

    private static final Integer DEFAULT_QUANTIDADE = 1;
    private static final Integer UPDATED_QUANTIDADE = 2;
    private static final Integer SMALLER_QUANTIDADE = 1 - 1;

    private static final Double DEFAULT_PRECO = 1D;
    private static final Double UPDATED_PRECO = 2D;
    private static final Double SMALLER_PRECO = 1D - 1D;

    private static final ZonedDateTime DEFAULT_CRIADO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CRIADO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CRIADO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/produto-no-pedidos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProdutoNoPedidoRepository produtoNoPedidoRepository;

    @Autowired
    private ProdutoNoPedidoMapper produtoNoPedidoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProdutoNoPedidoMockMvc;

    private ProdutoNoPedido produtoNoPedido;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProdutoNoPedido createEntity(EntityManager em) {
        ProdutoNoPedido produtoNoPedido = new ProdutoNoPedido().quantidade(DEFAULT_QUANTIDADE).preco(DEFAULT_PRECO).criado(DEFAULT_CRIADO);
        return produtoNoPedido;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProdutoNoPedido createUpdatedEntity(EntityManager em) {
        ProdutoNoPedido produtoNoPedido = new ProdutoNoPedido().quantidade(UPDATED_QUANTIDADE).preco(UPDATED_PRECO).criado(UPDATED_CRIADO);
        return produtoNoPedido;
    }

    @BeforeEach
    public void initTest() {
        produtoNoPedido = createEntity(em);
    }

    @Test
    @Transactional
    void createProdutoNoPedido() throws Exception {
        int databaseSizeBeforeCreate = produtoNoPedidoRepository.findAll().size();
        // Create the ProdutoNoPedido
        ProdutoNoPedidoDTO produtoNoPedidoDTO = produtoNoPedidoMapper.toDto(produtoNoPedido);
        restProdutoNoPedidoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produtoNoPedidoDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeCreate + 1);
        ProdutoNoPedido testProdutoNoPedido = produtoNoPedidoList.get(produtoNoPedidoList.size() - 1);
        assertThat(testProdutoNoPedido.getQuantidade()).isEqualTo(DEFAULT_QUANTIDADE);
        assertThat(testProdutoNoPedido.getPreco()).isEqualTo(DEFAULT_PRECO);
        assertThat(testProdutoNoPedido.getCriado()).isEqualTo(DEFAULT_CRIADO);
    }

    @Test
    @Transactional
    void createProdutoNoPedidoWithExistingId() throws Exception {
        // Create the ProdutoNoPedido with an existing ID
        produtoNoPedido.setId(1L);
        ProdutoNoPedidoDTO produtoNoPedidoDTO = produtoNoPedidoMapper.toDto(produtoNoPedido);

        int databaseSizeBeforeCreate = produtoNoPedidoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProdutoNoPedidoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produtoNoPedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidos() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList
        restProdutoNoPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produtoNoPedido.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantidade").value(hasItem(DEFAULT_QUANTIDADE)))
            .andExpect(jsonPath("$.[*].preco").value(hasItem(DEFAULT_PRECO.doubleValue())))
            .andExpect(jsonPath("$.[*].criado").value(hasItem(sameInstant(DEFAULT_CRIADO))));
    }

    @Test
    @Transactional
    void getProdutoNoPedido() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get the produtoNoPedido
        restProdutoNoPedidoMockMvc
            .perform(get(ENTITY_API_URL_ID, produtoNoPedido.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(produtoNoPedido.getId().intValue()))
            .andExpect(jsonPath("$.quantidade").value(DEFAULT_QUANTIDADE))
            .andExpect(jsonPath("$.preco").value(DEFAULT_PRECO.doubleValue()))
            .andExpect(jsonPath("$.criado").value(sameInstant(DEFAULT_CRIADO)));
    }

    @Test
    @Transactional
    void getProdutoNoPedidosByIdFiltering() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        Long id = produtoNoPedido.getId();

        defaultProdutoNoPedidoShouldBeFound("id.equals=" + id);
        defaultProdutoNoPedidoShouldNotBeFound("id.notEquals=" + id);

        defaultProdutoNoPedidoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProdutoNoPedidoShouldNotBeFound("id.greaterThan=" + id);

        defaultProdutoNoPedidoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProdutoNoPedidoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByQuantidadeIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where quantidade equals to DEFAULT_QUANTIDADE
        defaultProdutoNoPedidoShouldBeFound("quantidade.equals=" + DEFAULT_QUANTIDADE);

        // Get all the produtoNoPedidoList where quantidade equals to UPDATED_QUANTIDADE
        defaultProdutoNoPedidoShouldNotBeFound("quantidade.equals=" + UPDATED_QUANTIDADE);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByQuantidadeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where quantidade not equals to DEFAULT_QUANTIDADE
        defaultProdutoNoPedidoShouldNotBeFound("quantidade.notEquals=" + DEFAULT_QUANTIDADE);

        // Get all the produtoNoPedidoList where quantidade not equals to UPDATED_QUANTIDADE
        defaultProdutoNoPedidoShouldBeFound("quantidade.notEquals=" + UPDATED_QUANTIDADE);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByQuantidadeIsInShouldWork() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where quantidade in DEFAULT_QUANTIDADE or UPDATED_QUANTIDADE
        defaultProdutoNoPedidoShouldBeFound("quantidade.in=" + DEFAULT_QUANTIDADE + "," + UPDATED_QUANTIDADE);

        // Get all the produtoNoPedidoList where quantidade equals to UPDATED_QUANTIDADE
        defaultProdutoNoPedidoShouldNotBeFound("quantidade.in=" + UPDATED_QUANTIDADE);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByQuantidadeIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where quantidade is not null
        defaultProdutoNoPedidoShouldBeFound("quantidade.specified=true");

        // Get all the produtoNoPedidoList where quantidade is null
        defaultProdutoNoPedidoShouldNotBeFound("quantidade.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByQuantidadeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where quantidade is greater than or equal to DEFAULT_QUANTIDADE
        defaultProdutoNoPedidoShouldBeFound("quantidade.greaterThanOrEqual=" + DEFAULT_QUANTIDADE);

        // Get all the produtoNoPedidoList where quantidade is greater than or equal to UPDATED_QUANTIDADE
        defaultProdutoNoPedidoShouldNotBeFound("quantidade.greaterThanOrEqual=" + UPDATED_QUANTIDADE);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByQuantidadeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where quantidade is less than or equal to DEFAULT_QUANTIDADE
        defaultProdutoNoPedidoShouldBeFound("quantidade.lessThanOrEqual=" + DEFAULT_QUANTIDADE);

        // Get all the produtoNoPedidoList where quantidade is less than or equal to SMALLER_QUANTIDADE
        defaultProdutoNoPedidoShouldNotBeFound("quantidade.lessThanOrEqual=" + SMALLER_QUANTIDADE);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByQuantidadeIsLessThanSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where quantidade is less than DEFAULT_QUANTIDADE
        defaultProdutoNoPedidoShouldNotBeFound("quantidade.lessThan=" + DEFAULT_QUANTIDADE);

        // Get all the produtoNoPedidoList where quantidade is less than UPDATED_QUANTIDADE
        defaultProdutoNoPedidoShouldBeFound("quantidade.lessThan=" + UPDATED_QUANTIDADE);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByQuantidadeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where quantidade is greater than DEFAULT_QUANTIDADE
        defaultProdutoNoPedidoShouldNotBeFound("quantidade.greaterThan=" + DEFAULT_QUANTIDADE);

        // Get all the produtoNoPedidoList where quantidade is greater than SMALLER_QUANTIDADE
        defaultProdutoNoPedidoShouldBeFound("quantidade.greaterThan=" + SMALLER_QUANTIDADE);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByPrecoIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where preco equals to DEFAULT_PRECO
        defaultProdutoNoPedidoShouldBeFound("preco.equals=" + DEFAULT_PRECO);

        // Get all the produtoNoPedidoList where preco equals to UPDATED_PRECO
        defaultProdutoNoPedidoShouldNotBeFound("preco.equals=" + UPDATED_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByPrecoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where preco not equals to DEFAULT_PRECO
        defaultProdutoNoPedidoShouldNotBeFound("preco.notEquals=" + DEFAULT_PRECO);

        // Get all the produtoNoPedidoList where preco not equals to UPDATED_PRECO
        defaultProdutoNoPedidoShouldBeFound("preco.notEquals=" + UPDATED_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByPrecoIsInShouldWork() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where preco in DEFAULT_PRECO or UPDATED_PRECO
        defaultProdutoNoPedidoShouldBeFound("preco.in=" + DEFAULT_PRECO + "," + UPDATED_PRECO);

        // Get all the produtoNoPedidoList where preco equals to UPDATED_PRECO
        defaultProdutoNoPedidoShouldNotBeFound("preco.in=" + UPDATED_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByPrecoIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where preco is not null
        defaultProdutoNoPedidoShouldBeFound("preco.specified=true");

        // Get all the produtoNoPedidoList where preco is null
        defaultProdutoNoPedidoShouldNotBeFound("preco.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByPrecoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where preco is greater than or equal to DEFAULT_PRECO
        defaultProdutoNoPedidoShouldBeFound("preco.greaterThanOrEqual=" + DEFAULT_PRECO);

        // Get all the produtoNoPedidoList where preco is greater than or equal to UPDATED_PRECO
        defaultProdutoNoPedidoShouldNotBeFound("preco.greaterThanOrEqual=" + UPDATED_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByPrecoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where preco is less than or equal to DEFAULT_PRECO
        defaultProdutoNoPedidoShouldBeFound("preco.lessThanOrEqual=" + DEFAULT_PRECO);

        // Get all the produtoNoPedidoList where preco is less than or equal to SMALLER_PRECO
        defaultProdutoNoPedidoShouldNotBeFound("preco.lessThanOrEqual=" + SMALLER_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByPrecoIsLessThanSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where preco is less than DEFAULT_PRECO
        defaultProdutoNoPedidoShouldNotBeFound("preco.lessThan=" + DEFAULT_PRECO);

        // Get all the produtoNoPedidoList where preco is less than UPDATED_PRECO
        defaultProdutoNoPedidoShouldBeFound("preco.lessThan=" + UPDATED_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByPrecoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where preco is greater than DEFAULT_PRECO
        defaultProdutoNoPedidoShouldNotBeFound("preco.greaterThan=" + DEFAULT_PRECO);

        // Get all the produtoNoPedidoList where preco is greater than SMALLER_PRECO
        defaultProdutoNoPedidoShouldBeFound("preco.greaterThan=" + SMALLER_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByCriadoIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where criado equals to DEFAULT_CRIADO
        defaultProdutoNoPedidoShouldBeFound("criado.equals=" + DEFAULT_CRIADO);

        // Get all the produtoNoPedidoList where criado equals to UPDATED_CRIADO
        defaultProdutoNoPedidoShouldNotBeFound("criado.equals=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByCriadoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where criado not equals to DEFAULT_CRIADO
        defaultProdutoNoPedidoShouldNotBeFound("criado.notEquals=" + DEFAULT_CRIADO);

        // Get all the produtoNoPedidoList where criado not equals to UPDATED_CRIADO
        defaultProdutoNoPedidoShouldBeFound("criado.notEquals=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByCriadoIsInShouldWork() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where criado in DEFAULT_CRIADO or UPDATED_CRIADO
        defaultProdutoNoPedidoShouldBeFound("criado.in=" + DEFAULT_CRIADO + "," + UPDATED_CRIADO);

        // Get all the produtoNoPedidoList where criado equals to UPDATED_CRIADO
        defaultProdutoNoPedidoShouldNotBeFound("criado.in=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByCriadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where criado is not null
        defaultProdutoNoPedidoShouldBeFound("criado.specified=true");

        // Get all the produtoNoPedidoList where criado is null
        defaultProdutoNoPedidoShouldNotBeFound("criado.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByCriadoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where criado is greater than or equal to DEFAULT_CRIADO
        defaultProdutoNoPedidoShouldBeFound("criado.greaterThanOrEqual=" + DEFAULT_CRIADO);

        // Get all the produtoNoPedidoList where criado is greater than or equal to UPDATED_CRIADO
        defaultProdutoNoPedidoShouldNotBeFound("criado.greaterThanOrEqual=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByCriadoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where criado is less than or equal to DEFAULT_CRIADO
        defaultProdutoNoPedidoShouldBeFound("criado.lessThanOrEqual=" + DEFAULT_CRIADO);

        // Get all the produtoNoPedidoList where criado is less than or equal to SMALLER_CRIADO
        defaultProdutoNoPedidoShouldNotBeFound("criado.lessThanOrEqual=" + SMALLER_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByCriadoIsLessThanSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where criado is less than DEFAULT_CRIADO
        defaultProdutoNoPedidoShouldNotBeFound("criado.lessThan=" + DEFAULT_CRIADO);

        // Get all the produtoNoPedidoList where criado is less than UPDATED_CRIADO
        defaultProdutoNoPedidoShouldBeFound("criado.lessThan=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByCriadoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        // Get all the produtoNoPedidoList where criado is greater than DEFAULT_CRIADO
        defaultProdutoNoPedidoShouldNotBeFound("criado.greaterThan=" + DEFAULT_CRIADO);

        // Get all the produtoNoPedidoList where criado is greater than SMALLER_CRIADO
        defaultProdutoNoPedidoShouldBeFound("criado.greaterThan=" + SMALLER_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByProdutoIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);
        Produto produto = ProdutoResourceIT.createEntity(em);
        em.persist(produto);
        em.flush();
        produtoNoPedido.setProduto(produto);
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);
        Long produtoId = produto.getId();

        // Get all the produtoNoPedidoList where produto equals to produtoId
        defaultProdutoNoPedidoShouldBeFound("produtoId.equals=" + produtoId);

        // Get all the produtoNoPedidoList where produto equals to (produtoId + 1)
        defaultProdutoNoPedidoShouldNotBeFound("produtoId.equals=" + (produtoId + 1));
    }

    @Test
    @Transactional
    void getAllProdutoNoPedidosByPedidoIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);
        Pedido pedido = PedidoResourceIT.createEntity(em);
        em.persist(pedido);
        em.flush();
        produtoNoPedido.setPedido(pedido);
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);
        Long pedidoId = pedido.getId();

        // Get all the produtoNoPedidoList where pedido equals to pedidoId
        defaultProdutoNoPedidoShouldBeFound("pedidoId.equals=" + pedidoId);

        // Get all the produtoNoPedidoList where pedido equals to (pedidoId + 1)
        defaultProdutoNoPedidoShouldNotBeFound("pedidoId.equals=" + (pedidoId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProdutoNoPedidoShouldBeFound(String filter) throws Exception {
        restProdutoNoPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produtoNoPedido.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantidade").value(hasItem(DEFAULT_QUANTIDADE)))
            .andExpect(jsonPath("$.[*].preco").value(hasItem(DEFAULT_PRECO.doubleValue())))
            .andExpect(jsonPath("$.[*].criado").value(hasItem(sameInstant(DEFAULT_CRIADO))));

        // Check, that the count call also returns 1
        restProdutoNoPedidoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProdutoNoPedidoShouldNotBeFound(String filter) throws Exception {
        restProdutoNoPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProdutoNoPedidoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProdutoNoPedido() throws Exception {
        // Get the produtoNoPedido
        restProdutoNoPedidoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProdutoNoPedido() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        int databaseSizeBeforeUpdate = produtoNoPedidoRepository.findAll().size();

        // Update the produtoNoPedido
        ProdutoNoPedido updatedProdutoNoPedido = produtoNoPedidoRepository.findById(produtoNoPedido.getId()).get();
        // Disconnect from session so that the updates on updatedProdutoNoPedido are not directly saved in db
        em.detach(updatedProdutoNoPedido);
        updatedProdutoNoPedido.quantidade(UPDATED_QUANTIDADE).preco(UPDATED_PRECO).criado(UPDATED_CRIADO);
        ProdutoNoPedidoDTO produtoNoPedidoDTO = produtoNoPedidoMapper.toDto(updatedProdutoNoPedido);

        restProdutoNoPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produtoNoPedidoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(produtoNoPedidoDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeUpdate);
        ProdutoNoPedido testProdutoNoPedido = produtoNoPedidoList.get(produtoNoPedidoList.size() - 1);
        assertThat(testProdutoNoPedido.getQuantidade()).isEqualTo(UPDATED_QUANTIDADE);
        assertThat(testProdutoNoPedido.getPreco()).isEqualTo(UPDATED_PRECO);
        assertThat(testProdutoNoPedido.getCriado()).isEqualTo(UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void putNonExistingProdutoNoPedido() throws Exception {
        int databaseSizeBeforeUpdate = produtoNoPedidoRepository.findAll().size();
        produtoNoPedido.setId(count.incrementAndGet());

        // Create the ProdutoNoPedido
        ProdutoNoPedidoDTO produtoNoPedidoDTO = produtoNoPedidoMapper.toDto(produtoNoPedido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProdutoNoPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produtoNoPedidoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(produtoNoPedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProdutoNoPedido() throws Exception {
        int databaseSizeBeforeUpdate = produtoNoPedidoRepository.findAll().size();
        produtoNoPedido.setId(count.incrementAndGet());

        // Create the ProdutoNoPedido
        ProdutoNoPedidoDTO produtoNoPedidoDTO = produtoNoPedidoMapper.toDto(produtoNoPedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProdutoNoPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(produtoNoPedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProdutoNoPedido() throws Exception {
        int databaseSizeBeforeUpdate = produtoNoPedidoRepository.findAll().size();
        produtoNoPedido.setId(count.incrementAndGet());

        // Create the ProdutoNoPedido
        ProdutoNoPedidoDTO produtoNoPedidoDTO = produtoNoPedidoMapper.toDto(produtoNoPedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProdutoNoPedidoMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produtoNoPedidoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProdutoNoPedidoWithPatch() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        int databaseSizeBeforeUpdate = produtoNoPedidoRepository.findAll().size();

        // Update the produtoNoPedido using partial update
        ProdutoNoPedido partialUpdatedProdutoNoPedido = new ProdutoNoPedido();
        partialUpdatedProdutoNoPedido.setId(produtoNoPedido.getId());

        partialUpdatedProdutoNoPedido.quantidade(UPDATED_QUANTIDADE);

        restProdutoNoPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProdutoNoPedido.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProdutoNoPedido))
            )
            .andExpect(status().isOk());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeUpdate);
        ProdutoNoPedido testProdutoNoPedido = produtoNoPedidoList.get(produtoNoPedidoList.size() - 1);
        assertThat(testProdutoNoPedido.getQuantidade()).isEqualTo(UPDATED_QUANTIDADE);
        assertThat(testProdutoNoPedido.getPreco()).isEqualTo(DEFAULT_PRECO);
        assertThat(testProdutoNoPedido.getCriado()).isEqualTo(DEFAULT_CRIADO);
    }

    @Test
    @Transactional
    void fullUpdateProdutoNoPedidoWithPatch() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        int databaseSizeBeforeUpdate = produtoNoPedidoRepository.findAll().size();

        // Update the produtoNoPedido using partial update
        ProdutoNoPedido partialUpdatedProdutoNoPedido = new ProdutoNoPedido();
        partialUpdatedProdutoNoPedido.setId(produtoNoPedido.getId());

        partialUpdatedProdutoNoPedido.quantidade(UPDATED_QUANTIDADE).preco(UPDATED_PRECO).criado(UPDATED_CRIADO);

        restProdutoNoPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProdutoNoPedido.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProdutoNoPedido))
            )
            .andExpect(status().isOk());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeUpdate);
        ProdutoNoPedido testProdutoNoPedido = produtoNoPedidoList.get(produtoNoPedidoList.size() - 1);
        assertThat(testProdutoNoPedido.getQuantidade()).isEqualTo(UPDATED_QUANTIDADE);
        assertThat(testProdutoNoPedido.getPreco()).isEqualTo(UPDATED_PRECO);
        assertThat(testProdutoNoPedido.getCriado()).isEqualTo(UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void patchNonExistingProdutoNoPedido() throws Exception {
        int databaseSizeBeforeUpdate = produtoNoPedidoRepository.findAll().size();
        produtoNoPedido.setId(count.incrementAndGet());

        // Create the ProdutoNoPedido
        ProdutoNoPedidoDTO produtoNoPedidoDTO = produtoNoPedidoMapper.toDto(produtoNoPedido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProdutoNoPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, produtoNoPedidoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(produtoNoPedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProdutoNoPedido() throws Exception {
        int databaseSizeBeforeUpdate = produtoNoPedidoRepository.findAll().size();
        produtoNoPedido.setId(count.incrementAndGet());

        // Create the ProdutoNoPedido
        ProdutoNoPedidoDTO produtoNoPedidoDTO = produtoNoPedidoMapper.toDto(produtoNoPedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProdutoNoPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(produtoNoPedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProdutoNoPedido() throws Exception {
        int databaseSizeBeforeUpdate = produtoNoPedidoRepository.findAll().size();
        produtoNoPedido.setId(count.incrementAndGet());

        // Create the ProdutoNoPedido
        ProdutoNoPedidoDTO produtoNoPedidoDTO = produtoNoPedidoMapper.toDto(produtoNoPedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProdutoNoPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(produtoNoPedidoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProdutoNoPedido in the database
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProdutoNoPedido() throws Exception {
        // Initialize the database
        produtoNoPedidoRepository.saveAndFlush(produtoNoPedido);

        int databaseSizeBeforeDelete = produtoNoPedidoRepository.findAll().size();

        // Delete the produtoNoPedido
        restProdutoNoPedidoMockMvc
            .perform(delete(ENTITY_API_URL_ID, produtoNoPedido.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProdutoNoPedido> produtoNoPedidoList = produtoNoPedidoRepository.findAll();
        assertThat(produtoNoPedidoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
