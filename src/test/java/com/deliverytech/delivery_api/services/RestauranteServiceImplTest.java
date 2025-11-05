package com.deliverytech.delivery_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.dtos.ProdutoResponseDTO;
import com.deliverytech.delivery_api.dtos.RestauranteDTO;
import com.deliverytech.delivery_api.dtos.RestauranteResponseDTO;
import com.deliverytech.delivery_api.dtos.TaxaEntregaResponseDTO;
import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.exceptions.ConflictException;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.exceptions.ValidationException;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestauranteServiceImpl - Testes Unitários")
class RestauranteServiceImplTest {

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RestauranteServiceImpl restauranteService;

    private Restaurante restaurante;
    private RestauranteDTO restauranteDTO;
    private RestauranteResponseDTO restauranteResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup Restaurante
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Restaurante Teste");
        restaurante.setEmail("restaurante@teste.com");
        restaurante.setCnpj("12345678000195");
        restaurante.setTelefone("16999999999");
        restaurante.setCategoria("Italiana");
        restaurante.setTaxaEntrega(BigDecimal.valueOf(5.00));
        restaurante.setEndereco("Rua Teste, 123");
        restaurante.setCidade("São Paulo");
        restaurante.setEstado("SP");
        restaurante.setCep("14870000");
        restaurante.setTempoEntregaMin(30);
        restaurante.setTempoEntregaMax(45);
        restaurante.setAtivo(true);

        // Setup RestauranteDTO
        restauranteDTO = new RestauranteDTO();
        restauranteDTO.setNome("Restaurante Teste");
        restauranteDTO.setEmail("restaurante@teste.com");
        restauranteDTO.setCnpj("12345678000195");
        restauranteDTO.setTelefone("16999999999");
        restauranteDTO.setCategoria("Italiana");
        restauranteDTO.setTaxaEntrega(BigDecimal.valueOf(5.00));
        restauranteDTO.setEndereco("Rua Teste, 123");
        restauranteDTO.setCidade("São Paulo");
        restauranteDTO.setEstado("SP");
        restauranteDTO.setCep("14870000");
        restauranteDTO.setTempoEntregaMin(30);
        restauranteDTO.setTempoEntregaMax(45);

        // Setup RestauranteResponseDTO
        restauranteResponseDTO = new RestauranteResponseDTO();
        restauranteResponseDTO.setId(1L);
        restauranteResponseDTO.setNome("Restaurante Teste");
        restauranteResponseDTO.setCategoria("Italiana");
    }

    @Test
    @DisplayName("Deve cadastrar restaurante com sucesso")
    void deveCadastrarRestauranteComSucesso() {
        when(restauranteRepository.existsByEmail(anyString())).thenReturn(false);
        when(restauranteRepository.existsByCnpj(anyString())).thenReturn(false);
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class)))
            .thenReturn(restauranteResponseDTO);

        RestauranteResponseDTO result = restauranteService.cadastrar(restauranteDTO);

        assertNotNull(result);
        assertEquals("Restaurante Teste", result.getNome());
        verify(restauranteRepository).existsByEmail(anyString());
        verify(restauranteRepository).existsByCnpj(anyString());
        verify(restauranteRepository).save(any(Restaurante.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar restaurante com email duplicado")
    void deveLancarExcecaoAoCadastrarRestauranteComEmailDuplicado() {
        when(restauranteRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, 
            () -> restauranteService.cadastrar(restauranteDTO));
        verify(restauranteRepository).existsByEmail(anyString());
        verify(restauranteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar restaurante com CNPJ duplicado")
    void deveLancarExcecaoAoCadastrarRestauranteComCnpjDuplicado() {
        when(restauranteRepository.existsByEmail(anyString())).thenReturn(false);
        when(restauranteRepository.existsByCnpj(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, 
            () -> restauranteService.cadastrar(restauranteDTO));
        verify(restauranteRepository).existsByCnpj(anyString());
        verify(restauranteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar restaurante sem nome")
    void deveLancarExcecaoAoCadastrarRestauranteSemNome() {
        restauranteDTO.setNome(null);

        assertThrows(ValidationException.class, 
            () -> restauranteService.cadastrar(restauranteDTO));
        verify(restauranteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar restaurante sem CNPJ")
    void deveLancarExcecaoAoCadastrarRestauranteSemCnpj() {
        restauranteDTO.setCnpj(null);

        assertThrows(ValidationException.class, 
            () -> restauranteService.cadastrar(restauranteDTO));
        verify(restauranteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar restaurante sem email")
    void deveLancarExcecaoAoCadastrarRestauranteSemEmail() {
        restauranteDTO.setEmail(null);

        assertThrows(ValidationException.class, 
            () -> restauranteService.cadastrar(restauranteDTO));
        verify(restauranteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar restaurante com taxa de entrega inválida")
    void deveLancarExcecaoAoCadastrarRestauranteComTaxaEntregaInvalida() {
        restauranteDTO.setTaxaEntrega(BigDecimal.valueOf(-1));

        assertThrows(ValidationException.class, 
            () -> restauranteService.cadastrar(restauranteDTO));
        verify(restauranteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar restaurante sem categoria")
    void deveLancarExcecaoAoCadastrarRestauranteSemCategoria() {
        restauranteDTO.setCategoria(null);

        assertThrows(ValidationException.class, 
            () -> restauranteService.cadastrar(restauranteDTO));
        verify(restauranteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar restaurante por ID com sucesso")
    void deveBuscarRestaurantePorId() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class)))
            .thenReturn(restauranteResponseDTO);

        RestauranteResponseDTO result = restauranteService.buscarPorId(1L);

        assertNotNull(result);
        verify(restauranteRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar restaurante inexistente")
    void deveLancarExcecaoAoBuscarRestauranteInexistente() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> restauranteService.buscarPorId(1L));
        verify(restauranteRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve listar restaurantes disponíveis")
    void deveListarRestaurantesDisponiveis() {
        when(restauranteRepository.findByAtivoTrue(any(Pageable.class)))
            .thenReturn(new PageImpl<>(Arrays.asList(restaurante)));
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class)))
            .thenReturn(restauranteResponseDTO);

        Page<RestauranteResponseDTO> result = restauranteService.listarDisponiveis(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(restauranteRepository).findByAtivoTrue(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar restaurantes por categoria")
    void deveListarRestaurantesPorCategoria() {
        when(restauranteRepository.findByCategoriaAndAtivoTrue(eq("Italiana"), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Arrays.asList(restaurante)));
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class)))
            .thenReturn(restauranteResponseDTO);

        Page<RestauranteResponseDTO> result = restauranteService.listarPorCategoria("Italiana", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(restauranteRepository).findByCategoriaAndAtivoTrue(eq("Italiana"), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar restaurantes com categoria vazia")
    void deveLancarExcecaoAoListarRestaurantesComCategoriaVazia() {
        assertThrows(ValidationException.class, 
            () -> restauranteService.listarPorCategoria("", Pageable.unpaged()));
    }

    @Test
    @DisplayName("Deve atualizar restaurante com sucesso")
    void deveAtualizarRestauranteComSucesso() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class)))
            .thenReturn(restauranteResponseDTO);

        RestauranteResponseDTO result = restauranteService.atualizar(1L, restauranteDTO);

        assertNotNull(result);
        verify(restauranteRepository).findById(1L);
        verify(restauranteRepository).save(any(Restaurante.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar restaurante inexistente")
    void deveLancarExcecaoAoAtualizarRestauranteInexistente() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> restauranteService.atualizar(1L, restauranteDTO));
        verify(restauranteRepository).findById(1L);
        verify(restauranteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve alterar status do restaurante com sucesso")
    void deveAlterarStatusDoRestaurante() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class)))
            .thenReturn(restauranteResponseDTO);

        RestauranteResponseDTO result = restauranteService.alterarStatus(1L, false);

        assertNotNull(result);
        verify(restauranteRepository).findById(1L);
        verify(restauranteRepository).save(any(Restaurante.class));
    }

    @Test
    @DisplayName("Deve alternar status do restaurante")
    void deveAlternarStatusDoRestaurante() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class)))
            .thenReturn(restauranteResponseDTO);

        RestauranteResponseDTO result = restauranteService.alterarStatus(1L);

        assertNotNull(result);
        verify(restauranteRepository).findById(1L);
        verify(restauranteRepository).save(any(Restaurante.class));
    }

    @Test
    @DisplayName("Deve calcular taxa de entrega com sucesso")
    void deveCalcularTaxaEntregaComSucesso() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        TaxaEntregaResponseDTO result = restauranteService.calcularTaxaEntrega(1L, "14870100");

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(5.00), result.getTaxaEntrega());
        assertNotNull(result.getDistanciaKm());
        assertNotNull(result.getTempoEstimadoMinutos());
        verify(restauranteRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao calcular taxa de entrega com restaurante inexistente")
    void deveLancarExcecaoAoCalcularTaxaEntregaComRestauranteInexistente() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> restauranteService.calcularTaxaEntrega(1L, "14870100"));
        verify(restauranteRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao calcular taxa de entrega com restaurante inativo")
    void deveLancarExcecaoAoCalcularTaxaEntregaComRestauranteInativo() {
        restaurante.setAtivo(false);
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        assertThrows(ValidationException.class, 
            () -> restauranteService.calcularTaxaEntrega(1L, "14870100"));
        verify(restauranteRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao calcular taxa de entrega com CEP vazio")
    void deveLancarExcecaoAoCalcularTaxaEntregaComCepVazio() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        assertThrows(ValidationException.class, 
            () -> restauranteService.calcularTaxaEntrega(1L, ""));
        verify(restauranteRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao calcular taxa de entrega com CEP inválido")
    void deveLancarExcecaoAoCalcularTaxaEntregaComCepInvalido() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        assertThrows(ValidationException.class, 
            () -> restauranteService.calcularTaxaEntrega(1L, "123"));
        verify(restauranteRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar restaurante por nome")
    void deveBuscarRestaurantePorNome() {
        when(restauranteRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(eq("Teste"), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Arrays.asList(restaurante)));
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class)))
            .thenReturn(restauranteResponseDTO);

        Page<RestauranteResponseDTO> result = restauranteService.buscarPorNome("Teste", Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(restauranteRepository).findByNomeContainingIgnoreCaseAndAtivoTrue(eq("Teste"), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar restaurante com nome vazio")
    void deveLancarExcecaoAoBuscarRestauranteComNomeVazio() {
        assertThrows(ValidationException.class, 
            () -> restauranteService.buscarPorNome("", Pageable.unpaged()));
    }

    @Test
    @DisplayName("Deve buscar restaurante com produtos")
    void deveBuscarRestauranteComProdutos() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Pizza");
        produto.setRestaurante(restaurante);

        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findByRestauranteIdAndDisponivelTrue(eq(1L), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Arrays.asList(produto)));
        when(modelMapper.map(any(Restaurante.class), eq(RestauranteResponseDTO.class)))
            .thenReturn(restauranteResponseDTO);
        when(modelMapper.map(any(Produto.class), eq(ProdutoResponseDTO.class)))
            .thenReturn(new ProdutoResponseDTO());

        RestauranteResponseDTO result = restauranteService.buscarComProdutos(1L);

        assertNotNull(result);
        verify(restauranteRepository).findById(1L);
        verify(produtoRepository).findByRestauranteIdAndDisponivelTrue(eq(1L), any(Pageable.class));
    }
}