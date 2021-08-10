package br.execute.ecommerce.web.rest;

import static br.execute.ecommerce.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.execute.ecommerce.IntegrationTest;
import br.execute.ecommerce.domain.Pedido;
import br.execute.ecommerce.domain.Usuario;
import br.execute.ecommerce.domain.enumeration.StatusPedido;
import br.execute.ecommerce.repository.PedidoRepository;
import br.execute.ecommerce.service.criteria.PedidoCriteria;
import br.execute.ecommerce.service.dto.PedidoDTO;
import br.execute.ecommerce.service.mapper.PedidoMapper;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PedidoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PedidoResourceIT {

    private static final ZonedDateTime DEFAULT_CRIADO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CRIADO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CRIADO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final StatusPedido DEFAULT_STATUS = StatusPedido.PEDIDO;
    private static final StatusPedido UPDATED_STATUS = StatusPedido.AGUARDANDO_PAGAMENTO;

    private static final Double DEFAULT_PRECO_TOTAL = 1D;
    private static final Double UPDATED_PRECO_TOTAL = 2D;
    private static final Double SMALLER_PRECO_TOTAL = 1D - 1D;

    private static final String DEFAULT_COMENTARIO = "AAAAAAAAAA";
    private static final String UPDATED_COMENTARIO = "BBBBBBBBBB";

    private static final String DEFAULT_CODIGO_PAGAMENTO = "AAAAAAAAAA";
    private static final String UPDATED_CODIGO_PAGAMENTO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pedidos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoMapper pedidoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPedidoMockMvc;

    private Pedido pedido;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pedido createEntity(EntityManager em) {
        Pedido pedido = new Pedido()
            .criado(DEFAULT_CRIADO)
            .status(DEFAULT_STATUS)
            .precoTotal(DEFAULT_PRECO_TOTAL)
            .comentario(DEFAULT_COMENTARIO)
            .codigoPagamento(DEFAULT_CODIGO_PAGAMENTO);
        return pedido;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pedido createUpdatedEntity(EntityManager em) {
        Pedido pedido = new Pedido()
            .criado(UPDATED_CRIADO)
            .status(UPDATED_STATUS)
            .precoTotal(UPDATED_PRECO_TOTAL)
            .comentario(UPDATED_COMENTARIO)
            .codigoPagamento(UPDATED_CODIGO_PAGAMENTO);
        return pedido;
    }

    @BeforeEach
    public void initTest() {
        pedido = createEntity(em);
    }

    @Test
    @Transactional
    void createPedido() throws Exception {
        int databaseSizeBeforeCreate = pedidoRepository.findAll().size();
        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);
        restPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pedidoDTO)))
            .andExpect(status().isCreated());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeCreate + 1);
        Pedido testPedido = pedidoList.get(pedidoList.size() - 1);
        assertThat(testPedido.getCriado()).isEqualTo(DEFAULT_CRIADO);
        assertThat(testPedido.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPedido.getPrecoTotal()).isEqualTo(DEFAULT_PRECO_TOTAL);
        assertThat(testPedido.getComentario()).isEqualTo(DEFAULT_COMENTARIO);
        assertThat(testPedido.getCodigoPagamento()).isEqualTo(DEFAULT_CODIGO_PAGAMENTO);
    }

    @Test
    @Transactional
    void createPedidoWithExistingId() throws Exception {
        // Create the Pedido with an existing ID
        pedido.setId(1L);
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        int databaseSizeBeforeCreate = pedidoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPedidoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pedidoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPedidos() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pedido.getId().intValue())))
            .andExpect(jsonPath("$.[*].criado").value(hasItem(sameInstant(DEFAULT_CRIADO))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].precoTotal").value(hasItem(DEFAULT_PRECO_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].comentario").value(hasItem(DEFAULT_COMENTARIO.toString())))
            .andExpect(jsonPath("$.[*].codigoPagamento").value(hasItem(DEFAULT_CODIGO_PAGAMENTO)));
    }

    @Test
    @Transactional
    void getPedido() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get the pedido
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL_ID, pedido.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pedido.getId().intValue()))
            .andExpect(jsonPath("$.criado").value(sameInstant(DEFAULT_CRIADO)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.precoTotal").value(DEFAULT_PRECO_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.comentario").value(DEFAULT_COMENTARIO.toString()))
            .andExpect(jsonPath("$.codigoPagamento").value(DEFAULT_CODIGO_PAGAMENTO));
    }

    @Test
    @Transactional
    void getPedidosByIdFiltering() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        Long id = pedido.getId();

        defaultPedidoShouldBeFound("id.equals=" + id);
        defaultPedidoShouldNotBeFound("id.notEquals=" + id);

        defaultPedidoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPedidoShouldNotBeFound("id.greaterThan=" + id);

        defaultPedidoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPedidoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPedidosByCriadoIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where criado equals to DEFAULT_CRIADO
        defaultPedidoShouldBeFound("criado.equals=" + DEFAULT_CRIADO);

        // Get all the pedidoList where criado equals to UPDATED_CRIADO
        defaultPedidoShouldNotBeFound("criado.equals=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllPedidosByCriadoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where criado not equals to DEFAULT_CRIADO
        defaultPedidoShouldNotBeFound("criado.notEquals=" + DEFAULT_CRIADO);

        // Get all the pedidoList where criado not equals to UPDATED_CRIADO
        defaultPedidoShouldBeFound("criado.notEquals=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllPedidosByCriadoIsInShouldWork() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where criado in DEFAULT_CRIADO or UPDATED_CRIADO
        defaultPedidoShouldBeFound("criado.in=" + DEFAULT_CRIADO + "," + UPDATED_CRIADO);

        // Get all the pedidoList where criado equals to UPDATED_CRIADO
        defaultPedidoShouldNotBeFound("criado.in=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllPedidosByCriadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where criado is not null
        defaultPedidoShouldBeFound("criado.specified=true");

        // Get all the pedidoList where criado is null
        defaultPedidoShouldNotBeFound("criado.specified=false");
    }

    @Test
    @Transactional
    void getAllPedidosByCriadoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where criado is greater than or equal to DEFAULT_CRIADO
        defaultPedidoShouldBeFound("criado.greaterThanOrEqual=" + DEFAULT_CRIADO);

        // Get all the pedidoList where criado is greater than or equal to UPDATED_CRIADO
        defaultPedidoShouldNotBeFound("criado.greaterThanOrEqual=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllPedidosByCriadoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where criado is less than or equal to DEFAULT_CRIADO
        defaultPedidoShouldBeFound("criado.lessThanOrEqual=" + DEFAULT_CRIADO);

        // Get all the pedidoList where criado is less than or equal to SMALLER_CRIADO
        defaultPedidoShouldNotBeFound("criado.lessThanOrEqual=" + SMALLER_CRIADO);
    }

    @Test
    @Transactional
    void getAllPedidosByCriadoIsLessThanSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where criado is less than DEFAULT_CRIADO
        defaultPedidoShouldNotBeFound("criado.lessThan=" + DEFAULT_CRIADO);

        // Get all the pedidoList where criado is less than UPDATED_CRIADO
        defaultPedidoShouldBeFound("criado.lessThan=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllPedidosByCriadoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where criado is greater than DEFAULT_CRIADO
        defaultPedidoShouldNotBeFound("criado.greaterThan=" + DEFAULT_CRIADO);

        // Get all the pedidoList where criado is greater than SMALLER_CRIADO
        defaultPedidoShouldBeFound("criado.greaterThan=" + SMALLER_CRIADO);
    }

    @Test
    @Transactional
    void getAllPedidosByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where status equals to DEFAULT_STATUS
        defaultPedidoShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the pedidoList where status equals to UPDATED_STATUS
        defaultPedidoShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPedidosByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where status not equals to DEFAULT_STATUS
        defaultPedidoShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the pedidoList where status not equals to UPDATED_STATUS
        defaultPedidoShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPedidosByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultPedidoShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the pedidoList where status equals to UPDATED_STATUS
        defaultPedidoShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPedidosByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where status is not null
        defaultPedidoShouldBeFound("status.specified=true");

        // Get all the pedidoList where status is null
        defaultPedidoShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllPedidosByPrecoTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where precoTotal equals to DEFAULT_PRECO_TOTAL
        defaultPedidoShouldBeFound("precoTotal.equals=" + DEFAULT_PRECO_TOTAL);

        // Get all the pedidoList where precoTotal equals to UPDATED_PRECO_TOTAL
        defaultPedidoShouldNotBeFound("precoTotal.equals=" + UPDATED_PRECO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPedidosByPrecoTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where precoTotal not equals to DEFAULT_PRECO_TOTAL
        defaultPedidoShouldNotBeFound("precoTotal.notEquals=" + DEFAULT_PRECO_TOTAL);

        // Get all the pedidoList where precoTotal not equals to UPDATED_PRECO_TOTAL
        defaultPedidoShouldBeFound("precoTotal.notEquals=" + UPDATED_PRECO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPedidosByPrecoTotalIsInShouldWork() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where precoTotal in DEFAULT_PRECO_TOTAL or UPDATED_PRECO_TOTAL
        defaultPedidoShouldBeFound("precoTotal.in=" + DEFAULT_PRECO_TOTAL + "," + UPDATED_PRECO_TOTAL);

        // Get all the pedidoList where precoTotal equals to UPDATED_PRECO_TOTAL
        defaultPedidoShouldNotBeFound("precoTotal.in=" + UPDATED_PRECO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPedidosByPrecoTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where precoTotal is not null
        defaultPedidoShouldBeFound("precoTotal.specified=true");

        // Get all the pedidoList where precoTotal is null
        defaultPedidoShouldNotBeFound("precoTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllPedidosByPrecoTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where precoTotal is greater than or equal to DEFAULT_PRECO_TOTAL
        defaultPedidoShouldBeFound("precoTotal.greaterThanOrEqual=" + DEFAULT_PRECO_TOTAL);

        // Get all the pedidoList where precoTotal is greater than or equal to UPDATED_PRECO_TOTAL
        defaultPedidoShouldNotBeFound("precoTotal.greaterThanOrEqual=" + UPDATED_PRECO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPedidosByPrecoTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where precoTotal is less than or equal to DEFAULT_PRECO_TOTAL
        defaultPedidoShouldBeFound("precoTotal.lessThanOrEqual=" + DEFAULT_PRECO_TOTAL);

        // Get all the pedidoList where precoTotal is less than or equal to SMALLER_PRECO_TOTAL
        defaultPedidoShouldNotBeFound("precoTotal.lessThanOrEqual=" + SMALLER_PRECO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPedidosByPrecoTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where precoTotal is less than DEFAULT_PRECO_TOTAL
        defaultPedidoShouldNotBeFound("precoTotal.lessThan=" + DEFAULT_PRECO_TOTAL);

        // Get all the pedidoList where precoTotal is less than UPDATED_PRECO_TOTAL
        defaultPedidoShouldBeFound("precoTotal.lessThan=" + UPDATED_PRECO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPedidosByPrecoTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where precoTotal is greater than DEFAULT_PRECO_TOTAL
        defaultPedidoShouldNotBeFound("precoTotal.greaterThan=" + DEFAULT_PRECO_TOTAL);

        // Get all the pedidoList where precoTotal is greater than SMALLER_PRECO_TOTAL
        defaultPedidoShouldBeFound("precoTotal.greaterThan=" + SMALLER_PRECO_TOTAL);
    }

    @Test
    @Transactional
    void getAllPedidosByCodigoPagamentoIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where codigoPagamento equals to DEFAULT_CODIGO_PAGAMENTO
        defaultPedidoShouldBeFound("codigoPagamento.equals=" + DEFAULT_CODIGO_PAGAMENTO);

        // Get all the pedidoList where codigoPagamento equals to UPDATED_CODIGO_PAGAMENTO
        defaultPedidoShouldNotBeFound("codigoPagamento.equals=" + UPDATED_CODIGO_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllPedidosByCodigoPagamentoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where codigoPagamento not equals to DEFAULT_CODIGO_PAGAMENTO
        defaultPedidoShouldNotBeFound("codigoPagamento.notEquals=" + DEFAULT_CODIGO_PAGAMENTO);

        // Get all the pedidoList where codigoPagamento not equals to UPDATED_CODIGO_PAGAMENTO
        defaultPedidoShouldBeFound("codigoPagamento.notEquals=" + UPDATED_CODIGO_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllPedidosByCodigoPagamentoIsInShouldWork() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where codigoPagamento in DEFAULT_CODIGO_PAGAMENTO or UPDATED_CODIGO_PAGAMENTO
        defaultPedidoShouldBeFound("codigoPagamento.in=" + DEFAULT_CODIGO_PAGAMENTO + "," + UPDATED_CODIGO_PAGAMENTO);

        // Get all the pedidoList where codigoPagamento equals to UPDATED_CODIGO_PAGAMENTO
        defaultPedidoShouldNotBeFound("codigoPagamento.in=" + UPDATED_CODIGO_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllPedidosByCodigoPagamentoIsNullOrNotNull() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where codigoPagamento is not null
        defaultPedidoShouldBeFound("codigoPagamento.specified=true");

        // Get all the pedidoList where codigoPagamento is null
        defaultPedidoShouldNotBeFound("codigoPagamento.specified=false");
    }

    @Test
    @Transactional
    void getAllPedidosByCodigoPagamentoContainsSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where codigoPagamento contains DEFAULT_CODIGO_PAGAMENTO
        defaultPedidoShouldBeFound("codigoPagamento.contains=" + DEFAULT_CODIGO_PAGAMENTO);

        // Get all the pedidoList where codigoPagamento contains UPDATED_CODIGO_PAGAMENTO
        defaultPedidoShouldNotBeFound("codigoPagamento.contains=" + UPDATED_CODIGO_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllPedidosByCodigoPagamentoNotContainsSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        // Get all the pedidoList where codigoPagamento does not contain DEFAULT_CODIGO_PAGAMENTO
        defaultPedidoShouldNotBeFound("codigoPagamento.doesNotContain=" + DEFAULT_CODIGO_PAGAMENTO);

        // Get all the pedidoList where codigoPagamento does not contain UPDATED_CODIGO_PAGAMENTO
        defaultPedidoShouldBeFound("codigoPagamento.doesNotContain=" + UPDATED_CODIGO_PAGAMENTO);
    }

    @Test
    @Transactional
    void getAllPedidosByUsuarioIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);
        Usuario usuario = UsuarioResourceIT.createEntity(em);
        em.persist(usuario);
        em.flush();
        pedido.setUsuario(usuario);
        pedidoRepository.saveAndFlush(pedido);
        Long usuarioId = usuario.getId();

        // Get all the pedidoList where usuario equals to usuarioId
        defaultPedidoShouldBeFound("usuarioId.equals=" + usuarioId);

        // Get all the pedidoList where usuario equals to (usuarioId + 1)
        defaultPedidoShouldNotBeFound("usuarioId.equals=" + (usuarioId + 1));
    }

    @Test
    @Transactional
    void getAllPedidosByEnderecoIsEqualToSomething() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);
        Usuario endereco = UsuarioResourceIT.createEntity(em);
        em.persist(endereco);
        em.flush();
        pedido.setEndereco(endereco);
        pedidoRepository.saveAndFlush(pedido);
        Long enderecoId = endereco.getId();

        // Get all the pedidoList where endereco equals to enderecoId
        defaultPedidoShouldBeFound("enderecoId.equals=" + enderecoId);

        // Get all the pedidoList where endereco equals to (enderecoId + 1)
        defaultPedidoShouldNotBeFound("enderecoId.equals=" + (enderecoId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPedidoShouldBeFound(String filter) throws Exception {
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pedido.getId().intValue())))
            .andExpect(jsonPath("$.[*].criado").value(hasItem(sameInstant(DEFAULT_CRIADO))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].precoTotal").value(hasItem(DEFAULT_PRECO_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].comentario").value(hasItem(DEFAULT_COMENTARIO.toString())))
            .andExpect(jsonPath("$.[*].codigoPagamento").value(hasItem(DEFAULT_CODIGO_PAGAMENTO)));

        // Check, that the count call also returns 1
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPedidoShouldNotBeFound(String filter) throws Exception {
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPedidoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPedido() throws Exception {
        // Get the pedido
        restPedidoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPedido() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();

        // Update the pedido
        Pedido updatedPedido = pedidoRepository.findById(pedido.getId()).get();
        // Disconnect from session so that the updates on updatedPedido are not directly saved in db
        em.detach(updatedPedido);
        updatedPedido
            .criado(UPDATED_CRIADO)
            .status(UPDATED_STATUS)
            .precoTotal(UPDATED_PRECO_TOTAL)
            .comentario(UPDATED_COMENTARIO)
            .codigoPagamento(UPDATED_CODIGO_PAGAMENTO);
        PedidoDTO pedidoDTO = pedidoMapper.toDto(updatedPedido);

        restPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pedidoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
        Pedido testPedido = pedidoList.get(pedidoList.size() - 1);
        assertThat(testPedido.getCriado()).isEqualTo(UPDATED_CRIADO);
        assertThat(testPedido.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPedido.getPrecoTotal()).isEqualTo(UPDATED_PRECO_TOTAL);
        assertThat(testPedido.getComentario()).isEqualTo(UPDATED_COMENTARIO);
        assertThat(testPedido.getCodigoPagamento()).isEqualTo(UPDATED_CODIGO_PAGAMENTO);
    }

    @Test
    @Transactional
    void putNonExistingPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pedidoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pedidoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePedidoWithPatch() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();

        // Update the pedido using partial update
        Pedido partialUpdatedPedido = new Pedido();
        partialUpdatedPedido.setId(pedido.getId());

        partialUpdatedPedido.criado(UPDATED_CRIADO).precoTotal(UPDATED_PRECO_TOTAL).codigoPagamento(UPDATED_CODIGO_PAGAMENTO);

        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPedido.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPedido))
            )
            .andExpect(status().isOk());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
        Pedido testPedido = pedidoList.get(pedidoList.size() - 1);
        assertThat(testPedido.getCriado()).isEqualTo(UPDATED_CRIADO);
        assertThat(testPedido.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPedido.getPrecoTotal()).isEqualTo(UPDATED_PRECO_TOTAL);
        assertThat(testPedido.getComentario()).isEqualTo(DEFAULT_COMENTARIO);
        assertThat(testPedido.getCodigoPagamento()).isEqualTo(UPDATED_CODIGO_PAGAMENTO);
    }

    @Test
    @Transactional
    void fullUpdatePedidoWithPatch() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();

        // Update the pedido using partial update
        Pedido partialUpdatedPedido = new Pedido();
        partialUpdatedPedido.setId(pedido.getId());

        partialUpdatedPedido
            .criado(UPDATED_CRIADO)
            .status(UPDATED_STATUS)
            .precoTotal(UPDATED_PRECO_TOTAL)
            .comentario(UPDATED_COMENTARIO)
            .codigoPagamento(UPDATED_CODIGO_PAGAMENTO);

        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPedido.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPedido))
            )
            .andExpect(status().isOk());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
        Pedido testPedido = pedidoList.get(pedidoList.size() - 1);
        assertThat(testPedido.getCriado()).isEqualTo(UPDATED_CRIADO);
        assertThat(testPedido.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPedido.getPrecoTotal()).isEqualTo(UPDATED_PRECO_TOTAL);
        assertThat(testPedido.getComentario()).isEqualTo(UPDATED_COMENTARIO);
        assertThat(testPedido.getCodigoPagamento()).isEqualTo(UPDATED_CODIGO_PAGAMENTO);
    }

    @Test
    @Transactional
    void patchNonExistingPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pedidoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPedido() throws Exception {
        int databaseSizeBeforeUpdate = pedidoRepository.findAll().size();
        pedido.setId(count.incrementAndGet());

        // Create the Pedido
        PedidoDTO pedidoDTO = pedidoMapper.toDto(pedido);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPedidoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pedidoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pedido in the database
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePedido() throws Exception {
        // Initialize the database
        pedidoRepository.saveAndFlush(pedido);

        int databaseSizeBeforeDelete = pedidoRepository.findAll().size();

        // Delete the pedido
        restPedidoMockMvc
            .perform(delete(ENTITY_API_URL_ID, pedido.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pedido> pedidoList = pedidoRepository.findAll();
        assertThat(pedidoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
