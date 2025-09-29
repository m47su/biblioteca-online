package br.ifba.edu.BibliotecaOnline.service;

import br.ifba.edu.BibliotecaOnline.DTO.LivroDTO;
import br.ifba.edu.BibliotecaOnline.entities.Autor;
import br.ifba.edu.BibliotecaOnline.entities.LivroEntity;
import br.ifba.edu.BibliotecaOnline.entities.Usuario;
import br.ifba.edu.BibliotecaOnline.excecao.AnoPublicacaoInvalidoException;
import br.ifba.edu.BibliotecaOnline.excecao.AutorExistenteException;
import br.ifba.edu.BibliotecaOnline.excecao.LivroDuplicadoException;
import br.ifba.edu.BibliotecaOnline.mapper.LivroMapper;
import br.ifba.edu.BibliotecaOnline.model.GeneroEnum;
import br.ifba.edu.BibliotecaOnline.repository.AutorRepository;
import br.ifba.edu.BibliotecaOnline.repository.LivroRepository;
import br.ifba.edu.BibliotecaOnline.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do LivroService")
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LivroMapper livroMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LivroService livroService;

    private LivroDTO livroDTO;
    private LivroEntity livroEntity;
    private Autor autor;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Setup autor
        autor = new Autor();
        autor.setId(1L);
        autor.setNomeAutor("Machado de Assis");
        autor.setDescricaoDoAutor("Escritor brasileiro");

        // Setup usuário
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("admin@test.com");
        usuario.setNome("Admin");

        // Setup LivroDTO
        livroDTO = new LivroDTO();
        livroDTO.setNome("Dom Casmurro");
        livroDTO.setAnoPublicacao(1899);
        livroDTO.setSinopse("Romance clássico brasileiro");
        livroDTO.setGenero(GeneroEnum.ROMANCE);
        livroDTO.setAutorId(1L);
        livroDTO.setTipoAutor("existente");

        // Setup LivroEntity
        livroEntity = new LivroEntity();
        livroEntity.setId(1L);
        livroEntity.setNome("Dom Casmurro");
        livroEntity.setAnoPublicacao(1899);
        livroEntity.setSinopse("Romance clássico brasileiro");
        livroEntity.setGenero(GeneroEnum.ROMANCE);
        livroEntity.setAutor(autor);
        livroEntity.setPublicadoPor(usuario);
    }

    @Test
    @DisplayName("Deve salvar um novo livro com sucesso")
    void deveSalvarNovoLivroComSucesso() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.existsByNome(anyString())).thenReturn(false);
        when(livroMapper.toEntity(any(LivroDTO.class))).thenReturn(livroEntity);
        when(livroRepository.save(any(LivroEntity.class))).thenReturn(livroEntity);
        when(livroMapper.toDTO(any(LivroEntity.class))).thenReturn(livroDTO);

        // Act
        LivroDTO resultado = livroService.salvar(livroDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Dom Casmurro", resultado.getNome());
        verify(livroRepository, times(1)).save(any(LivroEntity.class));
        verify(autorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve atualizar um livro existente")
    void deveAtualizarLivroExistente() {
        // Arrange
        livroDTO.setId(1L);
        livroDTO.setNome("Dom Casmurro - Edição Revisada");
        
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livroEntity));
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(livroRepository.existsByNomeAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(livroRepository.save(any(LivroEntity.class))).thenReturn(livroEntity);
        when(livroMapper.toDTO(any(LivroEntity.class))).thenReturn(livroDTO);

        // Act
        LivroDTO resultado = livroService.salvar(livroDTO);

        // Assert
        assertNotNull(resultado);
        verify(livroRepository, times(1)).findById(1L);
        verify(livroRepository, times(1)).save(any(LivroEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar livro com nome duplicado")
    void deveLancarExcecaoAoSalvarLivroComNomeDuplicado() {
        // Arrange
        when(livroRepository.existsByNome(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(LivroDuplicadoException.class, () -> livroService.salvar(livroDTO));
        verify(livroRepository, never()).save(any(LivroEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar livro com ano inválido")
    void deveLancarExcecaoAoSalvarLivroComAnoInvalido() {
        // Arrange
        livroDTO.setAnoPublicacao(LocalDate.now().getYear() + 1);

        // Act & Assert
        assertThrows(AnoPublicacaoInvalidoException.class, () -> livroService.salvar(livroDTO));
        verify(livroRepository, never()).save(any(LivroEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar livro com ano menor que 1500")
    void deveLancarExcecaoAoSalvarLivroComAnoMenorQue1500() {
        // Arrange
        livroDTO.setAnoPublicacao(1499);

        // Act & Assert
        assertThrows(AnoPublicacaoInvalidoException.class, () -> livroService.salvar(livroDTO));
        verify(livroRepository, never()).save(any(LivroEntity.class));
    }

    @Test
    @DisplayName("Deve criar novo autor ao salvar livro com tipo 'novo'")
    void deveCriarNovoAutorAoSalvarLivro() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        
        livroDTO.setTipoAutor("novo");
        livroDTO.setNovoAutorNome("Clarice Lispector");
        livroDTO.setNovoAutorDescricao("Escritora brasileira");
        livroDTO.setAutorId(null);
        
        when(autorRepository.findByNomeAutorIgnoreCase("Clarice Lispector")).thenReturn(Optional.empty());
        when(autorRepository.save(any(Autor.class))).thenReturn(autor);
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        when(livroRepository.existsByNome(anyString())).thenReturn(false);
        when(livroMapper.toEntity(any(LivroDTO.class))).thenReturn(livroEntity);
        when(livroRepository.save(any(LivroEntity.class))).thenReturn(livroEntity);
        when(livroMapper.toDTO(any(LivroEntity.class))).thenReturn(livroDTO);

        // Act
        LivroDTO resultado = livroService.salvar(livroDTO);

        // Assert
        assertNotNull(resultado);
        verify(autorRepository, times(1)).save(any(Autor.class));
        verify(livroRepository, times(1)).save(any(LivroEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar autor que já existe")
    void deveLancarExcecaoAoTentarCriarAutorExistente() {
        // Arrange
        livroDTO.setTipoAutor("novo");
        livroDTO.setNovoAutorNome("Machado de Assis");
        livroDTO.setAutorId(null);
        
        when(autorRepository.findByNomeAutorIgnoreCase("Machado de Assis")).thenReturn(Optional.of(autor));

        // Act & Assert
        assertThrows(AutorExistenteException.class, () -> livroService.salvar(livroDTO));
        verify(livroRepository, never()).save(any(LivroEntity.class));
    }

    @Test
    @DisplayName("Deve deletar livro com sucesso")
    void deveDeletarLivroComSucesso() {
        // Arrange
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livroEntity));
        doNothing().when(livroRepository).delete(any(LivroEntity.class));

        // Act
        livroService.deletar(1L);

        // Assert
        verify(livroRepository, times(1)).findById(1L);
        verify(livroRepository, times(1)).delete(livroEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar livro inexistente")
    void deveLancarExcecaoAoDeletarLivroInexistente() {
        // Arrange
        when(livroRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> livroService.deletar(1L));
        verify(livroRepository, never()).delete(any(LivroEntity.class));
    }

    @Test
    @DisplayName("Deve listar todos os livros")
    void deveListarTodosOsLivros() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(usuario);
        
        List<LivroEntity> livros = Arrays.asList(livroEntity);
        when(livroRepository.findAll()).thenReturn(livros);
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        when(livroMapper.toDTO(any(LivroEntity.class))).thenReturn(livroDTO);

        // Act
        List<LivroDTO> resultado = livroService.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(livroRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar livro por ID")
    void deveBuscarLivroPorId() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(usuario);
        
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livroEntity));
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        when(livroMapper.toDTO(any(LivroEntity.class))).thenReturn(livroDTO);

        // Act
        LivroDTO resultado = livroService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Dom Casmurro", resultado.getNome());
        verify(livroRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar livro inexistente")
    void deveLancarExcecaoAoBuscarLivroInexistente() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        
        when(livroRepository.findById(1L)).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> livroService.buscarPorId(1L));
    }

    @Test
    @DisplayName("Deve listar livros paginados")
    void deveListarLivrosPaginados() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(usuario);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<LivroEntity> page = new PageImpl<>(Arrays.asList(livroEntity));
        
        when(livroRepository.findAll(pageable)).thenReturn(page);
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        when(livroMapper.toDTO(any(LivroEntity.class))).thenReturn(livroDTO);

        // Act
        Page<LivroDTO> resultado = livroService.listarPaginado(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(livroRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar livros por gênero")
    void deveBuscarLivrosPorGenero() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(usuario);
        
        List<LivroEntity> livros = Arrays.asList(livroEntity);
        when(livroRepository.findByGenero(GeneroEnum.ROMANCE)).thenReturn(livros);
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        when(livroMapper.toDTO(any(LivroEntity.class))).thenReturn(livroDTO);

        // Act
        List<LivroDTO> resultado = livroService.listarPorGenero(GeneroEnum.ROMANCE);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(livroRepository, times(1)).findByGenero(GeneroEnum.ROMANCE);
    }

    @Test
    @DisplayName("Deve buscar livros por palavra-chave")
    void deveBuscarLivrosPorPalavraChave() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(usuario);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<LivroEntity> page = new PageImpl<>(Arrays.asList(livroEntity));
        
        when(livroRepository.findByNomeContainingIgnoreCaseOrAutorNomeAutorContainingIgnoreCase(
                "Dom", "Dom", pageable)).thenReturn(page);
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        when(livroMapper.toDTO(any(LivroEntity.class))).thenReturn(livroDTO);

        // Act
        Page<LivroDTO> resultado = livroService.buscarPorPalavraChave("Dom", pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deve listar livros mais recentes")
    void deveListarLivrosMaisRecentes() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(usuario);
        
        List<LivroEntity> livros = Arrays.asList(livroEntity);
        when(livroRepository.findAll(ArgumentMatchers.<Sort>any())).thenReturn(livros);
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        when(livroMapper.toDTO(any(LivroEntity.class))).thenReturn(livroDTO);

        // Act
        List<LivroDTO> resultado = livroService.listarMaisRecentes();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve buscar livros por autor ID")
    void deveBuscarLivrosPorAutorId() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(usuario);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<LivroEntity> page = new PageImpl<>(Arrays.asList(livroEntity));
        
        when(livroRepository.findByAutorId(1L, pageable)).thenReturn(page);
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(usuario));
        when(livroMapper.toDTO(any(LivroEntity.class))).thenReturn(livroDTO);

        // Act
        Page<LivroDTO> resultado = livroService.buscarPorAutorId(1L, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(livroRepository, times(1)).findByAutorId(1L, pageable);
    }
}
