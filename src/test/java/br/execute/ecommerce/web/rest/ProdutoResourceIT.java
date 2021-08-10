package br.execute.ecommerce.web.rest;

import static br.execute.ecommerce.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.execute.ecommerce.IntegrationTest;
import br.execute.ecommerce.domain.Produto;
import br.execute.ecommerce.repository.ProdutoRepository;
import br.execute.ecommerce.service.criteria.ProdutoCriteria;
import br.execute.ecommerce.service.dto.ProdutoDTO;
import br.execute.ecommerce.service.mapper.ProdutoMapper;
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
 * Integration tests for the {@link ProdutoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProdutoResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final String DEFAULT_FOTO_URL = "AAAAAAAAAA";
    private static final String UPDATED_FOTO_URL = "BBBBBBBBBB";

    private static final String DEFAULT_SKU = "AAAAAAAAAA";
    private static final String UPDATED_SKU = "BBBBBBBBBB";

    private static final String DEFAULT_EAN = "AAAAAAAAAA";
    private static final String UPDATED_EAN = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CRIADO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CRIADO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CRIADO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Double DEFAULT_PRECO = 1D;
    private static final Double UPDATED_PRECO = 2D;
    private static final Double SMALLER_PRECO = 1D - 1D;

    private static final Double DEFAULT_PRECO_PROMOCIONAL = 1D;
    private static final Double UPDATED_PRECO_PROMOCIONAL = 2D;
    private static final Double SMALLER_PRECO_PROMOCIONAL = 1D - 1D;

    private static final Integer DEFAULT_TOTAL_ESTOQUE = 1;
    private static final Integer UPDATED_TOTAL_ESTOQUE = 2;
    private static final Integer SMALLER_TOTAL_ESTOQUE = 1 - 1;

    private static final String ENTITY_API_URL = "/api/produtos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoMapper produtoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProdutoMockMvc;

    private Produto produto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produto createEntity(EntityManager em) {
        Produto produto = new Produto()
            .nome(DEFAULT_NOME)
            .descricao(DEFAULT_DESCRICAO)
            .fotoUrl(DEFAULT_FOTO_URL)
            .sku(DEFAULT_SKU)
            .ean(DEFAULT_EAN)
            .criado(DEFAULT_CRIADO)
            .preco(DEFAULT_PRECO)
            .precoPromocional(DEFAULT_PRECO_PROMOCIONAL)
            .totalEstoque(DEFAULT_TOTAL_ESTOQUE);
        return produto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produto createUpdatedEntity(EntityManager em) {
        Produto produto = new Produto()
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .fotoUrl(UPDATED_FOTO_URL)
            .sku(UPDATED_SKU)
            .ean(UPDATED_EAN)
            .criado(UPDATED_CRIADO)
            .preco(UPDATED_PRECO)
            .precoPromocional(UPDATED_PRECO_PROMOCIONAL)
            .totalEstoque(UPDATED_TOTAL_ESTOQUE);
        return produto;
    }

    @BeforeEach
    public void initTest() {
        produto = createEntity(em);
    }

    @Test
    @Transactional
    void createProduto() throws Exception {
        int databaseSizeBeforeCreate = produtoRepository.findAll().size();
        // Create the Produto
        ProdutoDTO produtoDTO = produtoMapper.toDto(produto);
        restProdutoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produtoDTO)))
            .andExpect(status().isCreated());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeCreate + 1);
        Produto testProduto = produtoList.get(produtoList.size() - 1);
        assertThat(testProduto.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testProduto.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testProduto.getFotoUrl()).isEqualTo(DEFAULT_FOTO_URL);
        assertThat(testProduto.getSku()).isEqualTo(DEFAULT_SKU);
        assertThat(testProduto.getEan()).isEqualTo(DEFAULT_EAN);
        assertThat(testProduto.getCriado()).isEqualTo(DEFAULT_CRIADO);
        assertThat(testProduto.getPreco()).isEqualTo(DEFAULT_PRECO);
        assertThat(testProduto.getPrecoPromocional()).isEqualTo(DEFAULT_PRECO_PROMOCIONAL);
        assertThat(testProduto.getTotalEstoque()).isEqualTo(DEFAULT_TOTAL_ESTOQUE);
    }

    @Test
    @Transactional
    void createProdutoWithExistingId() throws Exception {
        // Create the Produto with an existing ID
        produto.setId(1L);
        ProdutoDTO produtoDTO = produtoMapper.toDto(produto);

        int databaseSizeBeforeCreate = produtoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProdutoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produtoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = produtoRepository.findAll().size();
        // set the field null
        produto.setNome(null);

        // Create the Produto, which fails.
        ProdutoDTO produtoDTO = produtoMapper.toDto(produto);

        restProdutoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produtoDTO)))
            .andExpect(status().isBadRequest());

        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProdutos() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList
        restProdutoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produto.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())))
            .andExpect(jsonPath("$.[*].fotoUrl").value(hasItem(DEFAULT_FOTO_URL)))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].ean").value(hasItem(DEFAULT_EAN)))
            .andExpect(jsonPath("$.[*].criado").value(hasItem(sameInstant(DEFAULT_CRIADO))))
            .andExpect(jsonPath("$.[*].preco").value(hasItem(DEFAULT_PRECO.doubleValue())))
            .andExpect(jsonPath("$.[*].precoPromocional").value(hasItem(DEFAULT_PRECO_PROMOCIONAL.doubleValue())))
            .andExpect(jsonPath("$.[*].totalEstoque").value(hasItem(DEFAULT_TOTAL_ESTOQUE)));
    }

    @Test
    @Transactional
    void getProduto() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get the produto
        restProdutoMockMvc
            .perform(get(ENTITY_API_URL_ID, produto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(produto.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO.toString()))
            .andExpect(jsonPath("$.fotoUrl").value(DEFAULT_FOTO_URL))
            .andExpect(jsonPath("$.sku").value(DEFAULT_SKU))
            .andExpect(jsonPath("$.ean").value(DEFAULT_EAN))
            .andExpect(jsonPath("$.criado").value(sameInstant(DEFAULT_CRIADO)))
            .andExpect(jsonPath("$.preco").value(DEFAULT_PRECO.doubleValue()))
            .andExpect(jsonPath("$.precoPromocional").value(DEFAULT_PRECO_PROMOCIONAL.doubleValue()))
            .andExpect(jsonPath("$.totalEstoque").value(DEFAULT_TOTAL_ESTOQUE));
    }

    @Test
    @Transactional
    void getProdutosByIdFiltering() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        Long id = produto.getId();

        defaultProdutoShouldBeFound("id.equals=" + id);
        defaultProdutoShouldNotBeFound("id.notEquals=" + id);

        defaultProdutoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProdutoShouldNotBeFound("id.greaterThan=" + id);

        defaultProdutoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProdutoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProdutosByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where nome equals to DEFAULT_NOME
        defaultProdutoShouldBeFound("nome.equals=" + DEFAULT_NOME);

        // Get all the produtoList where nome equals to UPDATED_NOME
        defaultProdutoShouldNotBeFound("nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllProdutosByNomeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where nome not equals to DEFAULT_NOME
        defaultProdutoShouldNotBeFound("nome.notEquals=" + DEFAULT_NOME);

        // Get all the produtoList where nome not equals to UPDATED_NOME
        defaultProdutoShouldBeFound("nome.notEquals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllProdutosByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where nome in DEFAULT_NOME or UPDATED_NOME
        defaultProdutoShouldBeFound("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME);

        // Get all the produtoList where nome equals to UPDATED_NOME
        defaultProdutoShouldNotBeFound("nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllProdutosByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where nome is not null
        defaultProdutoShouldBeFound("nome.specified=true");

        // Get all the produtoList where nome is null
        defaultProdutoShouldNotBeFound("nome.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutosByNomeContainsSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where nome contains DEFAULT_NOME
        defaultProdutoShouldBeFound("nome.contains=" + DEFAULT_NOME);

        // Get all the produtoList where nome contains UPDATED_NOME
        defaultProdutoShouldNotBeFound("nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllProdutosByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where nome does not contain DEFAULT_NOME
        defaultProdutoShouldNotBeFound("nome.doesNotContain=" + DEFAULT_NOME);

        // Get all the produtoList where nome does not contain UPDATED_NOME
        defaultProdutoShouldBeFound("nome.doesNotContain=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllProdutosByFotoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where fotoUrl equals to DEFAULT_FOTO_URL
        defaultProdutoShouldBeFound("fotoUrl.equals=" + DEFAULT_FOTO_URL);

        // Get all the produtoList where fotoUrl equals to UPDATED_FOTO_URL
        defaultProdutoShouldNotBeFound("fotoUrl.equals=" + UPDATED_FOTO_URL);
    }

    @Test
    @Transactional
    void getAllProdutosByFotoUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where fotoUrl not equals to DEFAULT_FOTO_URL
        defaultProdutoShouldNotBeFound("fotoUrl.notEquals=" + DEFAULT_FOTO_URL);

        // Get all the produtoList where fotoUrl not equals to UPDATED_FOTO_URL
        defaultProdutoShouldBeFound("fotoUrl.notEquals=" + UPDATED_FOTO_URL);
    }

    @Test
    @Transactional
    void getAllProdutosByFotoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where fotoUrl in DEFAULT_FOTO_URL or UPDATED_FOTO_URL
        defaultProdutoShouldBeFound("fotoUrl.in=" + DEFAULT_FOTO_URL + "," + UPDATED_FOTO_URL);

        // Get all the produtoList where fotoUrl equals to UPDATED_FOTO_URL
        defaultProdutoShouldNotBeFound("fotoUrl.in=" + UPDATED_FOTO_URL);
    }

    @Test
    @Transactional
    void getAllProdutosByFotoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where fotoUrl is not null
        defaultProdutoShouldBeFound("fotoUrl.specified=true");

        // Get all the produtoList where fotoUrl is null
        defaultProdutoShouldNotBeFound("fotoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutosByFotoUrlContainsSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where fotoUrl contains DEFAULT_FOTO_URL
        defaultProdutoShouldBeFound("fotoUrl.contains=" + DEFAULT_FOTO_URL);

        // Get all the produtoList where fotoUrl contains UPDATED_FOTO_URL
        defaultProdutoShouldNotBeFound("fotoUrl.contains=" + UPDATED_FOTO_URL);
    }

    @Test
    @Transactional
    void getAllProdutosByFotoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where fotoUrl does not contain DEFAULT_FOTO_URL
        defaultProdutoShouldNotBeFound("fotoUrl.doesNotContain=" + DEFAULT_FOTO_URL);

        // Get all the produtoList where fotoUrl does not contain UPDATED_FOTO_URL
        defaultProdutoShouldBeFound("fotoUrl.doesNotContain=" + UPDATED_FOTO_URL);
    }

    @Test
    @Transactional
    void getAllProdutosBySkuIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where sku equals to DEFAULT_SKU
        defaultProdutoShouldBeFound("sku.equals=" + DEFAULT_SKU);

        // Get all the produtoList where sku equals to UPDATED_SKU
        defaultProdutoShouldNotBeFound("sku.equals=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProdutosBySkuIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where sku not equals to DEFAULT_SKU
        defaultProdutoShouldNotBeFound("sku.notEquals=" + DEFAULT_SKU);

        // Get all the produtoList where sku not equals to UPDATED_SKU
        defaultProdutoShouldBeFound("sku.notEquals=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProdutosBySkuIsInShouldWork() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where sku in DEFAULT_SKU or UPDATED_SKU
        defaultProdutoShouldBeFound("sku.in=" + DEFAULT_SKU + "," + UPDATED_SKU);

        // Get all the produtoList where sku equals to UPDATED_SKU
        defaultProdutoShouldNotBeFound("sku.in=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProdutosBySkuIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where sku is not null
        defaultProdutoShouldBeFound("sku.specified=true");

        // Get all the produtoList where sku is null
        defaultProdutoShouldNotBeFound("sku.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutosBySkuContainsSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where sku contains DEFAULT_SKU
        defaultProdutoShouldBeFound("sku.contains=" + DEFAULT_SKU);

        // Get all the produtoList where sku contains UPDATED_SKU
        defaultProdutoShouldNotBeFound("sku.contains=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProdutosBySkuNotContainsSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where sku does not contain DEFAULT_SKU
        defaultProdutoShouldNotBeFound("sku.doesNotContain=" + DEFAULT_SKU);

        // Get all the produtoList where sku does not contain UPDATED_SKU
        defaultProdutoShouldBeFound("sku.doesNotContain=" + UPDATED_SKU);
    }

    @Test
    @Transactional
    void getAllProdutosByEanIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where ean equals to DEFAULT_EAN
        defaultProdutoShouldBeFound("ean.equals=" + DEFAULT_EAN);

        // Get all the produtoList where ean equals to UPDATED_EAN
        defaultProdutoShouldNotBeFound("ean.equals=" + UPDATED_EAN);
    }

    @Test
    @Transactional
    void getAllProdutosByEanIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where ean not equals to DEFAULT_EAN
        defaultProdutoShouldNotBeFound("ean.notEquals=" + DEFAULT_EAN);

        // Get all the produtoList where ean not equals to UPDATED_EAN
        defaultProdutoShouldBeFound("ean.notEquals=" + UPDATED_EAN);
    }

    @Test
    @Transactional
    void getAllProdutosByEanIsInShouldWork() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where ean in DEFAULT_EAN or UPDATED_EAN
        defaultProdutoShouldBeFound("ean.in=" + DEFAULT_EAN + "," + UPDATED_EAN);

        // Get all the produtoList where ean equals to UPDATED_EAN
        defaultProdutoShouldNotBeFound("ean.in=" + UPDATED_EAN);
    }

    @Test
    @Transactional
    void getAllProdutosByEanIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where ean is not null
        defaultProdutoShouldBeFound("ean.specified=true");

        // Get all the produtoList where ean is null
        defaultProdutoShouldNotBeFound("ean.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutosByEanContainsSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where ean contains DEFAULT_EAN
        defaultProdutoShouldBeFound("ean.contains=" + DEFAULT_EAN);

        // Get all the produtoList where ean contains UPDATED_EAN
        defaultProdutoShouldNotBeFound("ean.contains=" + UPDATED_EAN);
    }

    @Test
    @Transactional
    void getAllProdutosByEanNotContainsSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where ean does not contain DEFAULT_EAN
        defaultProdutoShouldNotBeFound("ean.doesNotContain=" + DEFAULT_EAN);

        // Get all the produtoList where ean does not contain UPDATED_EAN
        defaultProdutoShouldBeFound("ean.doesNotContain=" + UPDATED_EAN);
    }

    @Test
    @Transactional
    void getAllProdutosByCriadoIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where criado equals to DEFAULT_CRIADO
        defaultProdutoShouldBeFound("criado.equals=" + DEFAULT_CRIADO);

        // Get all the produtoList where criado equals to UPDATED_CRIADO
        defaultProdutoShouldNotBeFound("criado.equals=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutosByCriadoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where criado not equals to DEFAULT_CRIADO
        defaultProdutoShouldNotBeFound("criado.notEquals=" + DEFAULT_CRIADO);

        // Get all the produtoList where criado not equals to UPDATED_CRIADO
        defaultProdutoShouldBeFound("criado.notEquals=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutosByCriadoIsInShouldWork() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where criado in DEFAULT_CRIADO or UPDATED_CRIADO
        defaultProdutoShouldBeFound("criado.in=" + DEFAULT_CRIADO + "," + UPDATED_CRIADO);

        // Get all the produtoList where criado equals to UPDATED_CRIADO
        defaultProdutoShouldNotBeFound("criado.in=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutosByCriadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where criado is not null
        defaultProdutoShouldBeFound("criado.specified=true");

        // Get all the produtoList where criado is null
        defaultProdutoShouldNotBeFound("criado.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutosByCriadoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where criado is greater than or equal to DEFAULT_CRIADO
        defaultProdutoShouldBeFound("criado.greaterThanOrEqual=" + DEFAULT_CRIADO);

        // Get all the produtoList where criado is greater than or equal to UPDATED_CRIADO
        defaultProdutoShouldNotBeFound("criado.greaterThanOrEqual=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutosByCriadoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where criado is less than or equal to DEFAULT_CRIADO
        defaultProdutoShouldBeFound("criado.lessThanOrEqual=" + DEFAULT_CRIADO);

        // Get all the produtoList where criado is less than or equal to SMALLER_CRIADO
        defaultProdutoShouldNotBeFound("criado.lessThanOrEqual=" + SMALLER_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutosByCriadoIsLessThanSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where criado is less than DEFAULT_CRIADO
        defaultProdutoShouldNotBeFound("criado.lessThan=" + DEFAULT_CRIADO);

        // Get all the produtoList where criado is less than UPDATED_CRIADO
        defaultProdutoShouldBeFound("criado.lessThan=" + UPDATED_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutosByCriadoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where criado is greater than DEFAULT_CRIADO
        defaultProdutoShouldNotBeFound("criado.greaterThan=" + DEFAULT_CRIADO);

        // Get all the produtoList where criado is greater than SMALLER_CRIADO
        defaultProdutoShouldBeFound("criado.greaterThan=" + SMALLER_CRIADO);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where preco equals to DEFAULT_PRECO
        defaultProdutoShouldBeFound("preco.equals=" + DEFAULT_PRECO);

        // Get all the produtoList where preco equals to UPDATED_PRECO
        defaultProdutoShouldNotBeFound("preco.equals=" + UPDATED_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where preco not equals to DEFAULT_PRECO
        defaultProdutoShouldNotBeFound("preco.notEquals=" + DEFAULT_PRECO);

        // Get all the produtoList where preco not equals to UPDATED_PRECO
        defaultProdutoShouldBeFound("preco.notEquals=" + UPDATED_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoIsInShouldWork() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where preco in DEFAULT_PRECO or UPDATED_PRECO
        defaultProdutoShouldBeFound("preco.in=" + DEFAULT_PRECO + "," + UPDATED_PRECO);

        // Get all the produtoList where preco equals to UPDATED_PRECO
        defaultProdutoShouldNotBeFound("preco.in=" + UPDATED_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where preco is not null
        defaultProdutoShouldBeFound("preco.specified=true");

        // Get all the produtoList where preco is null
        defaultProdutoShouldNotBeFound("preco.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where preco is greater than or equal to DEFAULT_PRECO
        defaultProdutoShouldBeFound("preco.greaterThanOrEqual=" + DEFAULT_PRECO);

        // Get all the produtoList where preco is greater than or equal to UPDATED_PRECO
        defaultProdutoShouldNotBeFound("preco.greaterThanOrEqual=" + UPDATED_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where preco is less than or equal to DEFAULT_PRECO
        defaultProdutoShouldBeFound("preco.lessThanOrEqual=" + DEFAULT_PRECO);

        // Get all the produtoList where preco is less than or equal to SMALLER_PRECO
        defaultProdutoShouldNotBeFound("preco.lessThanOrEqual=" + SMALLER_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoIsLessThanSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where preco is less than DEFAULT_PRECO
        defaultProdutoShouldNotBeFound("preco.lessThan=" + DEFAULT_PRECO);

        // Get all the produtoList where preco is less than UPDATED_PRECO
        defaultProdutoShouldBeFound("preco.lessThan=" + UPDATED_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where preco is greater than DEFAULT_PRECO
        defaultProdutoShouldNotBeFound("preco.greaterThan=" + DEFAULT_PRECO);

        // Get all the produtoList where preco is greater than SMALLER_PRECO
        defaultProdutoShouldBeFound("preco.greaterThan=" + SMALLER_PRECO);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoPromocionalIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where precoPromocional equals to DEFAULT_PRECO_PROMOCIONAL
        defaultProdutoShouldBeFound("precoPromocional.equals=" + DEFAULT_PRECO_PROMOCIONAL);

        // Get all the produtoList where precoPromocional equals to UPDATED_PRECO_PROMOCIONAL
        defaultProdutoShouldNotBeFound("precoPromocional.equals=" + UPDATED_PRECO_PROMOCIONAL);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoPromocionalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where precoPromocional not equals to DEFAULT_PRECO_PROMOCIONAL
        defaultProdutoShouldNotBeFound("precoPromocional.notEquals=" + DEFAULT_PRECO_PROMOCIONAL);

        // Get all the produtoList where precoPromocional not equals to UPDATED_PRECO_PROMOCIONAL
        defaultProdutoShouldBeFound("precoPromocional.notEquals=" + UPDATED_PRECO_PROMOCIONAL);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoPromocionalIsInShouldWork() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where precoPromocional in DEFAULT_PRECO_PROMOCIONAL or UPDATED_PRECO_PROMOCIONAL
        defaultProdutoShouldBeFound("precoPromocional.in=" + DEFAULT_PRECO_PROMOCIONAL + "," + UPDATED_PRECO_PROMOCIONAL);

        // Get all the produtoList where precoPromocional equals to UPDATED_PRECO_PROMOCIONAL
        defaultProdutoShouldNotBeFound("precoPromocional.in=" + UPDATED_PRECO_PROMOCIONAL);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoPromocionalIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where precoPromocional is not null
        defaultProdutoShouldBeFound("precoPromocional.specified=true");

        // Get all the produtoList where precoPromocional is null
        defaultProdutoShouldNotBeFound("precoPromocional.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoPromocionalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where precoPromocional is greater than or equal to DEFAULT_PRECO_PROMOCIONAL
        defaultProdutoShouldBeFound("precoPromocional.greaterThanOrEqual=" + DEFAULT_PRECO_PROMOCIONAL);

        // Get all the produtoList where precoPromocional is greater than or equal to UPDATED_PRECO_PROMOCIONAL
        defaultProdutoShouldNotBeFound("precoPromocional.greaterThanOrEqual=" + UPDATED_PRECO_PROMOCIONAL);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoPromocionalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where precoPromocional is less than or equal to DEFAULT_PRECO_PROMOCIONAL
        defaultProdutoShouldBeFound("precoPromocional.lessThanOrEqual=" + DEFAULT_PRECO_PROMOCIONAL);

        // Get all the produtoList where precoPromocional is less than or equal to SMALLER_PRECO_PROMOCIONAL
        defaultProdutoShouldNotBeFound("precoPromocional.lessThanOrEqual=" + SMALLER_PRECO_PROMOCIONAL);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoPromocionalIsLessThanSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where precoPromocional is less than DEFAULT_PRECO_PROMOCIONAL
        defaultProdutoShouldNotBeFound("precoPromocional.lessThan=" + DEFAULT_PRECO_PROMOCIONAL);

        // Get all the produtoList where precoPromocional is less than UPDATED_PRECO_PROMOCIONAL
        defaultProdutoShouldBeFound("precoPromocional.lessThan=" + UPDATED_PRECO_PROMOCIONAL);
    }

    @Test
    @Transactional
    void getAllProdutosByPrecoPromocionalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where precoPromocional is greater than DEFAULT_PRECO_PROMOCIONAL
        defaultProdutoShouldNotBeFound("precoPromocional.greaterThan=" + DEFAULT_PRECO_PROMOCIONAL);

        // Get all the produtoList where precoPromocional is greater than SMALLER_PRECO_PROMOCIONAL
        defaultProdutoShouldBeFound("precoPromocional.greaterThan=" + SMALLER_PRECO_PROMOCIONAL);
    }

    @Test
    @Transactional
    void getAllProdutosByTotalEstoqueIsEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where totalEstoque equals to DEFAULT_TOTAL_ESTOQUE
        defaultProdutoShouldBeFound("totalEstoque.equals=" + DEFAULT_TOTAL_ESTOQUE);

        // Get all the produtoList where totalEstoque equals to UPDATED_TOTAL_ESTOQUE
        defaultProdutoShouldNotBeFound("totalEstoque.equals=" + UPDATED_TOTAL_ESTOQUE);
    }

    @Test
    @Transactional
    void getAllProdutosByTotalEstoqueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where totalEstoque not equals to DEFAULT_TOTAL_ESTOQUE
        defaultProdutoShouldNotBeFound("totalEstoque.notEquals=" + DEFAULT_TOTAL_ESTOQUE);

        // Get all the produtoList where totalEstoque not equals to UPDATED_TOTAL_ESTOQUE
        defaultProdutoShouldBeFound("totalEstoque.notEquals=" + UPDATED_TOTAL_ESTOQUE);
    }

    @Test
    @Transactional
    void getAllProdutosByTotalEstoqueIsInShouldWork() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where totalEstoque in DEFAULT_TOTAL_ESTOQUE or UPDATED_TOTAL_ESTOQUE
        defaultProdutoShouldBeFound("totalEstoque.in=" + DEFAULT_TOTAL_ESTOQUE + "," + UPDATED_TOTAL_ESTOQUE);

        // Get all the produtoList where totalEstoque equals to UPDATED_TOTAL_ESTOQUE
        defaultProdutoShouldNotBeFound("totalEstoque.in=" + UPDATED_TOTAL_ESTOQUE);
    }

    @Test
    @Transactional
    void getAllProdutosByTotalEstoqueIsNullOrNotNull() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where totalEstoque is not null
        defaultProdutoShouldBeFound("totalEstoque.specified=true");

        // Get all the produtoList where totalEstoque is null
        defaultProdutoShouldNotBeFound("totalEstoque.specified=false");
    }

    @Test
    @Transactional
    void getAllProdutosByTotalEstoqueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where totalEstoque is greater than or equal to DEFAULT_TOTAL_ESTOQUE
        defaultProdutoShouldBeFound("totalEstoque.greaterThanOrEqual=" + DEFAULT_TOTAL_ESTOQUE);

        // Get all the produtoList where totalEstoque is greater than or equal to UPDATED_TOTAL_ESTOQUE
        defaultProdutoShouldNotBeFound("totalEstoque.greaterThanOrEqual=" + UPDATED_TOTAL_ESTOQUE);
    }

    @Test
    @Transactional
    void getAllProdutosByTotalEstoqueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where totalEstoque is less than or equal to DEFAULT_TOTAL_ESTOQUE
        defaultProdutoShouldBeFound("totalEstoque.lessThanOrEqual=" + DEFAULT_TOTAL_ESTOQUE);

        // Get all the produtoList where totalEstoque is less than or equal to SMALLER_TOTAL_ESTOQUE
        defaultProdutoShouldNotBeFound("totalEstoque.lessThanOrEqual=" + SMALLER_TOTAL_ESTOQUE);
    }

    @Test
    @Transactional
    void getAllProdutosByTotalEstoqueIsLessThanSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where totalEstoque is less than DEFAULT_TOTAL_ESTOQUE
        defaultProdutoShouldNotBeFound("totalEstoque.lessThan=" + DEFAULT_TOTAL_ESTOQUE);

        // Get all the produtoList where totalEstoque is less than UPDATED_TOTAL_ESTOQUE
        defaultProdutoShouldBeFound("totalEstoque.lessThan=" + UPDATED_TOTAL_ESTOQUE);
    }

    @Test
    @Transactional
    void getAllProdutosByTotalEstoqueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        // Get all the produtoList where totalEstoque is greater than DEFAULT_TOTAL_ESTOQUE
        defaultProdutoShouldNotBeFound("totalEstoque.greaterThan=" + DEFAULT_TOTAL_ESTOQUE);

        // Get all the produtoList where totalEstoque is greater than SMALLER_TOTAL_ESTOQUE
        defaultProdutoShouldBeFound("totalEstoque.greaterThan=" + SMALLER_TOTAL_ESTOQUE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProdutoShouldBeFound(String filter) throws Exception {
        restProdutoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produto.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())))
            .andExpect(jsonPath("$.[*].fotoUrl").value(hasItem(DEFAULT_FOTO_URL)))
            .andExpect(jsonPath("$.[*].sku").value(hasItem(DEFAULT_SKU)))
            .andExpect(jsonPath("$.[*].ean").value(hasItem(DEFAULT_EAN)))
            .andExpect(jsonPath("$.[*].criado").value(hasItem(sameInstant(DEFAULT_CRIADO))))
            .andExpect(jsonPath("$.[*].preco").value(hasItem(DEFAULT_PRECO.doubleValue())))
            .andExpect(jsonPath("$.[*].precoPromocional").value(hasItem(DEFAULT_PRECO_PROMOCIONAL.doubleValue())))
            .andExpect(jsonPath("$.[*].totalEstoque").value(hasItem(DEFAULT_TOTAL_ESTOQUE)));

        // Check, that the count call also returns 1
        restProdutoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProdutoShouldNotBeFound(String filter) throws Exception {
        restProdutoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProdutoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduto() throws Exception {
        // Get the produto
        restProdutoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProduto() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        int databaseSizeBeforeUpdate = produtoRepository.findAll().size();

        // Update the produto
        Produto updatedProduto = produtoRepository.findById(produto.getId()).get();
        // Disconnect from session so that the updates on updatedProduto are not directly saved in db
        em.detach(updatedProduto);
        updatedProduto
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .fotoUrl(UPDATED_FOTO_URL)
            .sku(UPDATED_SKU)
            .ean(UPDATED_EAN)
            .criado(UPDATED_CRIADO)
            .preco(UPDATED_PRECO)
            .precoPromocional(UPDATED_PRECO_PROMOCIONAL)
            .totalEstoque(UPDATED_TOTAL_ESTOQUE);
        ProdutoDTO produtoDTO = produtoMapper.toDto(updatedProduto);

        restProdutoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produtoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(produtoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeUpdate);
        Produto testProduto = produtoList.get(produtoList.size() - 1);
        assertThat(testProduto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testProduto.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testProduto.getFotoUrl()).isEqualTo(UPDATED_FOTO_URL);
        assertThat(testProduto.getSku()).isEqualTo(UPDATED_SKU);
        assertThat(testProduto.getEan()).isEqualTo(UPDATED_EAN);
        assertThat(testProduto.getCriado()).isEqualTo(UPDATED_CRIADO);
        assertThat(testProduto.getPreco()).isEqualTo(UPDATED_PRECO);
        assertThat(testProduto.getPrecoPromocional()).isEqualTo(UPDATED_PRECO_PROMOCIONAL);
        assertThat(testProduto.getTotalEstoque()).isEqualTo(UPDATED_TOTAL_ESTOQUE);
    }

    @Test
    @Transactional
    void putNonExistingProduto() throws Exception {
        int databaseSizeBeforeUpdate = produtoRepository.findAll().size();
        produto.setId(count.incrementAndGet());

        // Create the Produto
        ProdutoDTO produtoDTO = produtoMapper.toDto(produto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProdutoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produtoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(produtoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduto() throws Exception {
        int databaseSizeBeforeUpdate = produtoRepository.findAll().size();
        produto.setId(count.incrementAndGet());

        // Create the Produto
        ProdutoDTO produtoDTO = produtoMapper.toDto(produto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProdutoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(produtoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduto() throws Exception {
        int databaseSizeBeforeUpdate = produtoRepository.findAll().size();
        produto.setId(count.incrementAndGet());

        // Create the Produto
        ProdutoDTO produtoDTO = produtoMapper.toDto(produto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProdutoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produtoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProdutoWithPatch() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        int databaseSizeBeforeUpdate = produtoRepository.findAll().size();

        // Update the produto using partial update
        Produto partialUpdatedProduto = new Produto();
        partialUpdatedProduto.setId(produto.getId());

        partialUpdatedProduto.preco(UPDATED_PRECO).totalEstoque(UPDATED_TOTAL_ESTOQUE);

        restProdutoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduto))
            )
            .andExpect(status().isOk());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeUpdate);
        Produto testProduto = produtoList.get(produtoList.size() - 1);
        assertThat(testProduto.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testProduto.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testProduto.getFotoUrl()).isEqualTo(DEFAULT_FOTO_URL);
        assertThat(testProduto.getSku()).isEqualTo(DEFAULT_SKU);
        assertThat(testProduto.getEan()).isEqualTo(DEFAULT_EAN);
        assertThat(testProduto.getCriado()).isEqualTo(DEFAULT_CRIADO);
        assertThat(testProduto.getPreco()).isEqualTo(UPDATED_PRECO);
        assertThat(testProduto.getPrecoPromocional()).isEqualTo(DEFAULT_PRECO_PROMOCIONAL);
        assertThat(testProduto.getTotalEstoque()).isEqualTo(UPDATED_TOTAL_ESTOQUE);
    }

    @Test
    @Transactional
    void fullUpdateProdutoWithPatch() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        int databaseSizeBeforeUpdate = produtoRepository.findAll().size();

        // Update the produto using partial update
        Produto partialUpdatedProduto = new Produto();
        partialUpdatedProduto.setId(produto.getId());

        partialUpdatedProduto
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .fotoUrl(UPDATED_FOTO_URL)
            .sku(UPDATED_SKU)
            .ean(UPDATED_EAN)
            .criado(UPDATED_CRIADO)
            .preco(UPDATED_PRECO)
            .precoPromocional(UPDATED_PRECO_PROMOCIONAL)
            .totalEstoque(UPDATED_TOTAL_ESTOQUE);

        restProdutoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduto))
            )
            .andExpect(status().isOk());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeUpdate);
        Produto testProduto = produtoList.get(produtoList.size() - 1);
        assertThat(testProduto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testProduto.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testProduto.getFotoUrl()).isEqualTo(UPDATED_FOTO_URL);
        assertThat(testProduto.getSku()).isEqualTo(UPDATED_SKU);
        assertThat(testProduto.getEan()).isEqualTo(UPDATED_EAN);
        assertThat(testProduto.getCriado()).isEqualTo(UPDATED_CRIADO);
        assertThat(testProduto.getPreco()).isEqualTo(UPDATED_PRECO);
        assertThat(testProduto.getPrecoPromocional()).isEqualTo(UPDATED_PRECO_PROMOCIONAL);
        assertThat(testProduto.getTotalEstoque()).isEqualTo(UPDATED_TOTAL_ESTOQUE);
    }

    @Test
    @Transactional
    void patchNonExistingProduto() throws Exception {
        int databaseSizeBeforeUpdate = produtoRepository.findAll().size();
        produto.setId(count.incrementAndGet());

        // Create the Produto
        ProdutoDTO produtoDTO = produtoMapper.toDto(produto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProdutoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, produtoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(produtoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduto() throws Exception {
        int databaseSizeBeforeUpdate = produtoRepository.findAll().size();
        produto.setId(count.incrementAndGet());

        // Create the Produto
        ProdutoDTO produtoDTO = produtoMapper.toDto(produto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProdutoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(produtoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduto() throws Exception {
        int databaseSizeBeforeUpdate = produtoRepository.findAll().size();
        produto.setId(count.incrementAndGet());

        // Create the Produto
        ProdutoDTO produtoDTO = produtoMapper.toDto(produto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProdutoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(produtoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produto in the database
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduto() throws Exception {
        // Initialize the database
        produtoRepository.saveAndFlush(produto);

        int databaseSizeBeforeDelete = produtoRepository.findAll().size();

        // Delete the produto
        restProdutoMockMvc
            .perform(delete(ENTITY_API_URL_ID, produto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Produto> produtoList = produtoRepository.findAll();
        assertThat(produtoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
