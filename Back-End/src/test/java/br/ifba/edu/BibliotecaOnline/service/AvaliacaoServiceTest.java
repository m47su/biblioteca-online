package br.ifba.edu.BibliotecaOnline.service;

import br.ifba.edu.BibliotecaOnline.DTO.AvaliacaoDTO;
import br.ifba.edu.BibliotecaOnline.DTO.CriarAvaliacaoDTO;
import br.ifba.edu.BibliotecaOnline.entities.AvaliacaoEntity;
import br.ifba.edu.BibliotecaOnline.entities.LivroEntity;
import br.ifba.edu.BibliotecaOnline.entities.Usuario;
import br.ifba.edu.BibliotecaOnline.mapper.AvaliacaoMapper;
import br.ifba.edu.BibliotecaOnline.repository.AvaliacaoRepository;
import br.ifba.edu.BibliotecaOnline.repository.LivroRepository;
import br.ifba.edu.BibliotecaOnline.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AvaliacaoService")
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AvaliacaoMapper avaliacaoMapper;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private Usuario usuario;
    private LivroEntity livro;
    private AvaliacaoEntity avaliacao;
    private CriarAvaliacaoDTO criarAvaliacaoDTO;
    private AvaliacaoDTO avaliacaoDTO;

    @BeforeEach
    void setUp() {
        // Setup usuário
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");

        // Setup livro
        livro = new LivroEntity();
        livro.setId(1L);
        livro.setNome("Dom Casmurro");

        // Setup avaliação entity
        avaliacao = new AvaliacaoEntity();
        avaliacao.setId(1L);
        avaliacao.setUsuario(usuario);
        avaliacao.setLivro(livro);
        avaliacao.setComentario("Excelente livro!");
        avaliacao.setNota(5);
        avaliacao.setDataAvaliacao(LocalDateTime.now());

        // Setup DTO para criar avaliação
        criarAvaliacaoDTO = new CriarAvaliacaoDTO();
        criarAvaliacaoDTO.setComentario("Excelente livro!");
        criarAvaliacaoDTO.setNota(5);

        // Setup DTO de resposta
        avaliacaoDTO = new AvaliacaoDTO();
        avaliacaoDTO.setId(1L);
        avaliacaoDTO.setComentario("Excelente livro!");
        avaliacaoDTO.setNota(5);
        avaliacaoDTO.setNomeUsuario("João Silva");
    }

    @Test
    @DisplayName("Deve salvar nova avaliação com sucesso")
    void deveSalvarNovaAvaliacaoComSucesso() {
        // Arrange
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(avaliacaoRepository.findByUsuarioAndLivro(usuario, livro)).thenReturn(Optional.empty());
        when(avaliacaoRepository.save(any(AvaliacaoEntity.class))).thenReturn(avaliacao);
        when(avaliacaoMapper.toDTO(any(AvaliacaoEntity.class))).thenReturn(avaliacaoDTO);

        // Act
        AvaliacaoDTO resultado = avaliacaoService.salvar(criarAvaliacaoDTO, 1L, "joao@test.com");

        // Assert
        assertNotNull(resultado);
        assertEquals("Excelente livro!", resultado.getComentario());
        assertEquals(5, resultado.getNota());
        verify(avaliacaoRepository, times(1)).save(any(AvaliacaoEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar avaliação existente ao tentar salvar novamente")
    void deveAtualizarAvaliacaoExistenteAoSalvarNovamente() {
        // Arrange
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(avaliacaoRepository.findByUsuarioAndLivro(usuario, livro)).thenReturn(Optional.of(avaliacao));
        when(avaliacaoRepository.save(any(AvaliacaoEntity.class))).thenReturn(avaliacao);
        when(avaliacaoMapper.toDTO(any(AvaliacaoEntity.class))).thenReturn(avaliacaoDTO);

        criarAvaliacaoDTO.setComentario("Livro ainda melhor na releitura!");
        criarAvaliacaoDTO.setNota(5);

        // Act
        AvaliacaoDTO resultado = avaliacaoService.salvar(criarAvaliacaoDTO, 1L, "joao@test.com");

        // Assert
        assertNotNull(resultado);
        verify(avaliacaoRepository, times(1)).findByUsuarioAndLivro(usuario, livro);
        verify(avaliacaoRepository, times(1)).save(any(AvaliacaoEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar avaliação para livro inexistente")
    void deveLancarExcecaoAoSalvarAvaliacaoParaLivroInexistente() {
        // Arrange
        when(livroRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> avaliacaoService.salvar(criarAvaliacaoDTO, 999L, "joao@test.com"));
        verify(avaliacaoRepository, never()).save(any(AvaliacaoEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar avaliação para usuário inexistente")
    void deveLancarExcecaoAoSalvarAvaliacaoParaUsuarioInexistente() {
        // Arrange
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(usuarioRepository.findByEmail("inexistente@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, 
            () -> avaliacaoService.salvar(criarAvaliacaoDTO, 1L, "inexistente@test.com"));
        verify(avaliacaoRepository, never()).save(any(AvaliacaoEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar avaliação existente com sucesso")
    void deveAtualizarAvaliacaoExistenteComSucesso() {
        // Arrange
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));
        when(avaliacaoRepository.save(any(AvaliacaoEntity.class))).thenReturn(avaliacao);
        when(avaliacaoMapper.toDTO(any(AvaliacaoEntity.class))).thenReturn(avaliacaoDTO);

        criarAvaliacaoDTO.setComentario("Comentário atualizado");
        criarAvaliacaoDTO.setNota(4);

        // Act
        AvaliacaoDTO resultado = avaliacaoService.atualizar(1L, criarAvaliacaoDTO, "joao@test.com");

        // Assert
        assertNotNull(resultado);
        verify(avaliacaoRepository, times(1)).findById(1L);
        verify(avaliacaoRepository, times(1)).save(any(AvaliacaoEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar avaliação de outro usuário")
    void deveLancarExcecaoAoAtualizarAvaliacaoDeOutroUsuario() {
        // Arrange
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));

        // Act & Assert
        assertThrows(SecurityException.class, 
            () -> avaliacaoService.atualizar(1L, criarAvaliacaoDTO, "outro@test.com"));
        verify(avaliacaoRepository, never()).save(any(AvaliacaoEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar avaliação inexistente")
    void deveLancarExcecaoAoAtualizarAvaliacaoInexistente() {
        // Arrange
        when(avaliacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> avaliacaoService.atualizar(999L, criarAvaliacaoDTO, "joao@test.com"));
        verify(avaliacaoRepository, never()).save(any(AvaliacaoEntity.class));
    }

    @Test
    @DisplayName("Deve deletar avaliação com sucesso")
    void deveDeletarAvaliacaoComSucesso() {
        // Arrange
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));
        doNothing().when(avaliacaoRepository).delete(any(AvaliacaoEntity.class));

        // Act
        avaliacaoService.deletar(1L, "joao@test.com");

        // Assert
        verify(avaliacaoRepository, times(1)).findById(1L);
        verify(avaliacaoRepository, times(1)).delete(avaliacao);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar avaliação de outro usuário")
    void deveLancarExcecaoAoDeletarAvaliacaoDeOutroUsuario() {
        // Arrange
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));

        // Act & Assert
        assertThrows(SecurityException.class, 
            () -> avaliacaoService.deletar(1L, "outro@test.com"));
        verify(avaliacaoRepository, never()).delete(any(AvaliacaoEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar avaliação inexistente")
    void deveLancarExcecaoAoDeletarAvaliacaoInexistente() {
        // Arrange
        when(avaliacaoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> avaliacaoService.deletar(999L, "joao@test.com"));
        verify(avaliacaoRepository, never()).delete(any(AvaliacaoEntity.class));
    }

    @Test
    @DisplayName("Deve buscar avaliações por livro ID")
    void deveBuscarAvaliacoesPorLivroId() {
        // Arrange
        List<AvaliacaoEntity> avaliacoes = Arrays.asList(avaliacao);
        when(avaliacaoRepository.findByLivroIdOrderByDataAvaliacaoDesc(1L)).thenReturn(avaliacoes);
        when(avaliacaoMapper.toDTO(any(AvaliacaoEntity.class))).thenReturn(avaliacaoDTO);

        // Act
        List<AvaliacaoDTO> resultado = avaliacaoService.buscarPorLivroId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Excelente livro!", resultado.get(0).getComentario());
        verify(avaliacaoRepository, times(1)).findByLivroIdOrderByDataAvaliacaoDesc(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há avaliações para o livro")
    void deveRetornarListaVaziaQuandoNaoHaAvaliacoes() {
        // Arrange
        when(avaliacaoRepository.findByLivroIdOrderByDataAvaliacaoDesc(1L)).thenReturn(Arrays.asList());

        // Act
        List<AvaliacaoDTO> resultado = avaliacaoService.buscarPorLivroId(1L);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(avaliacaoRepository, times(1)).findByLivroIdOrderByDataAvaliacaoDesc(1L);
    }

    @Test
    @DisplayName("Deve validar nota mínima e máxima ao salvar avaliação")
    void deveValidarNotaAoSalvarAvaliacao() {
        // Arrange
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(avaliacaoRepository.findByUsuarioAndLivro(usuario, livro)).thenReturn(Optional.empty());
        when(avaliacaoRepository.save(any(AvaliacaoEntity.class))).thenReturn(avaliacao);
        when(avaliacaoMapper.toDTO(any(AvaliacaoEntity.class))).thenReturn(avaliacaoDTO);

        // Test com nota válida
        criarAvaliacaoDTO.setNota(3);
        
        // Act
        AvaliacaoDTO resultado = avaliacaoService.salvar(criarAvaliacaoDTO, 1L, "joao@test.com");

        // Assert
        assertNotNull(resultado);
        verify(avaliacaoRepository, times(1)).save(any(AvaliacaoEntity.class));
    }
}