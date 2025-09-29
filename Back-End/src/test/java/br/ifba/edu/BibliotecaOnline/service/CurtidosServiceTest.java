package br.ifba.edu.BibliotecaOnline.service;

import br.ifba.edu.BibliotecaOnline.DTO.CurtidosDTO;
import br.ifba.edu.BibliotecaOnline.entities.Autor;
import br.ifba.edu.BibliotecaOnline.entities.LivroEntity;
import br.ifba.edu.BibliotecaOnline.entities.Usuario;
import br.ifba.edu.BibliotecaOnline.repository.LivroRepository;
import br.ifba.edu.BibliotecaOnline.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CurtidosService")
class CurtidosServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CurtidosService curtidosService;

    private Usuario usuario;
    private LivroEntity livro1;
    private LivroEntity livro2;
    private Autor autor;

    @BeforeEach
    void setUp() {
        // Setup autor
        autor = new Autor();
        autor.setId(1L);
        autor.setNomeAutor("Machado de Assis");

        // Setup usuário
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setLivrosCurtidos(new ArrayList<>());

        // Setup livros
        livro1 = new LivroEntity();
        livro1.setId(1L);
        livro1.setNome("Dom Casmurro");
        livro1.setCapaUrl("capa1.jpg");
        livro1.setAutor(autor);

        livro2 = new LivroEntity();
        livro2.setId(2L);
        livro2.setNome("Memórias Póstumas de Brás Cubas");
        livro2.setCapaUrl("capa2.jpg");
        livro2.setAutor(autor);
    }

    @Test
    @DisplayName("Deve obter usuário logado com sucesso")
    void deveObterUsuarioLogadoComSucesso() {
        // Arrange
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));

        // Act
        Usuario resultado = curtidosService.getUsuarioLogado(authentication);

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@test.com", resultado.getEmail());
        verify(usuarioRepository, times(1)).findByEmail("joao@test.com");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário logado inexistente")
    void deveLancarExcecaoAoBuscarUsuarioLogadoInexistente() {
        // Arrange
        when(authentication.getName()).thenReturn("inexistente@test.com");
        when(usuarioRepository.findByEmail("inexistente@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> curtidosService.getUsuarioLogado(authentication));
    }

    @Test
    @DisplayName("Deve listar livros curtidos paginados")
    void deveListarLivrosCurtidosPaginados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<LivroEntity> page = new PageImpl<>(Arrays.asList(livro1, livro2));
        
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findByUsuariosQueCurtiram_Id(1L, pageable)).thenReturn(page);

        // Act
        Page<CurtidosDTO> resultado = curtidosService.listarCurtidos(authentication, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertEquals("Dom Casmurro", resultado.getContent().get(0).getNome());
        assertEquals("Machado de Assis", resultado.getContent().get(0).getAutorNome());
        verify(livroRepository, times(1)).findByUsuariosQueCurtiram_Id(1L, pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando usuário não tem livros curtidos")
    void deveRetornarPaginaVaziaQuandoNaoTemLivrosCurtidos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<LivroEntity> page = new PageImpl<>(Arrays.asList());
        
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findByUsuariosQueCurtiram_Id(1L, pageable)).thenReturn(page);

        // Act
        Page<CurtidosDTO> resultado = curtidosService.listarCurtidos(authentication, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.getTotalElements());
        assertTrue(resultado.getContent().isEmpty());
    }

    @Test
    @DisplayName("Deve exibir 'Desconhecido' quando livro não tem autor")
    void deveExibirDesconhecidoQuandoLivroNaoTemAutor() {
        // Arrange
        livro1.setAutor(null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<LivroEntity> page = new PageImpl<>(Arrays.asList(livro1));
        
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findByUsuariosQueCurtiram_Id(1L, pageable)).thenReturn(page);

        // Act
        Page<CurtidosDTO> resultado = curtidosService.listarCurtidos(authentication, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Desconhecido", resultado.getContent().get(0).getAutorNome());
    }

    @Test
    @DisplayName("Deve curtir livro com sucesso")
    void deveCurtirLivroComSucesso() {
        // Arrange
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro1));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        curtidosService.curtirLivro(1L, authentication);

        // Assert
        verify(livroRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
        assertTrue(usuario.getLivrosCurtidos().contains(livro1));
    }

    @Test
    @DisplayName("Não deve adicionar livro já curtido novamente")
    void naoDeveAdicionarLivroJaCurtidoNovamente() {
        // Arrange
        usuario.getLivrosCurtidos().add(livro1);
        
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro1));

        // Act
        curtidosService.curtirLivro(1L, authentication);

        // Assert
        verify(livroRepository, times(1)).findById(1L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
        assertEquals(1, usuario.getLivrosCurtidos().size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao curtir livro inexistente")
    void deveLancarExcecaoAoCurtirLivroInexistente() {
        // Arrange
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> curtidosService.curtirLivro(999L, authentication));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve descurtir livro com sucesso")
    void deveDescurtirLivroComSucesso() {
        // Arrange
        usuario.getLivrosCurtidos().add(livro1);
        
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro1));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        curtidosService.descurtirLivro(1L, authentication);

        // Assert
        verify(livroRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
        assertFalse(usuario.getLivrosCurtidos().contains(livro1));
    }

    @Test
    @DisplayName("Deve lançar exceção ao descurtir livro inexistente")
    void deveLancarExcecaoAoDescurtirLivroInexistente() {
        // Arrange
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> curtidosService.descurtirLivro(999L, authentication));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve descurtir livro mesmo que não esteja na lista de curtidos")
    void deveDescurtirLivroMesmoQueNaoEstejaNaLista() {
        // Arrange
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro1));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        curtidosService.descurtirLivro(1L, authentication);

        // Assert
        verify(usuarioRepository, times(1)).save(usuario);
        assertFalse(usuario.getLivrosCurtidos().contains(livro1));
    }

    @Test
    @DisplayName("Deve curtir múltiplos livros")
    void deveCurtirMultiplosLivros() {
        // Arrange
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro1));
        when(livroRepository.findById(2L)).thenReturn(Optional.of(livro2));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        curtidosService.curtirLivro(1L, authentication);
        curtidosService.curtirLivro(2L, authentication);

        // Assert
        assertEquals(2, usuario.getLivrosCurtidos().size());
        assertTrue(usuario.getLivrosCurtidos().contains(livro1));
        assertTrue(usuario.getLivrosCurtidos().contains(livro2));
        verify(usuarioRepository, times(2)).save(usuario);
    }

    @Test
    @DisplayName("Deve manter outros livros curtidos ao descurtir um")
    void deveManterOutrosLivrosCurtidosAoDescurtirUm() {
        // Arrange
        usuario.getLivrosCurtidos().add(livro1);
        usuario.getLivrosCurtidos().add(livro2);
        
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro1));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        curtidosService.descurtirLivro(1L, authentication);

        // Assert
        assertEquals(1, usuario.getLivrosCurtidos().size());
        assertFalse(usuario.getLivrosCurtidos().contains(livro1));
        assertTrue(usuario.getLivrosCurtidos().contains(livro2));
    }
}