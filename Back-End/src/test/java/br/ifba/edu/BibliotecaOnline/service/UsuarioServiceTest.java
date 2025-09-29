package br.ifba.edu.BibliotecaOnline.service;

import br.ifba.edu.BibliotecaOnline.entities.Role;
import br.ifba.edu.BibliotecaOnline.entities.Usuario;
import br.ifba.edu.BibliotecaOnline.repository.RoleRepository;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Usuario admin;
    private Role roleAdmin;
    private Role roleUser;

    @BeforeEach
    void setUp() {
        // Setup roles
        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("USER");

        roleAdmin = new Role();
        roleAdmin.setId(2L);
        roleAdmin.setName("ADMIN");

        // Setup usuário comum
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setSenha("senha123");
        usuario.setRoles(new HashSet<>(Arrays.asList(roleUser)));

        // Setup admin
        admin = new Usuario();
        admin.setId(2L);
        admin.setNome("Admin User");
        admin.setEmail("admin@test.com");
        admin.setSenha("admin123");
        admin.setRoles(new HashSet<>(Arrays.asList(roleAdmin, roleUser)));
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosOsUsuarios() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario, admin);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<Usuario> resultado = usuarioService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve promover usuário para admin com sucesso")
    void devePromoverUsuarioParaAdmin() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(roleAdmin));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.promoverParaAdmin(1L);

        // Assert
        verify(usuarioRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findByName("ADMIN");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Não deve promover usuário que já é admin")
    void naoDevePromoverUsuarioQueJaEhAdmin() {
        // Arrange
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(admin));

        // Act
        usuarioService.promoverParaAdmin(2L);

        // Assert
        verify(usuarioRepository, times(1)).findById(2L);
        verify(roleRepository, never()).findByName("ADMIN");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao promover usuário inexistente")
    void deveLancarExcecaoAoPromoverUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.promoverParaAdmin(999L));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando role ADMIN não existe")
    void deveLancarExcecaoQuandoRoleAdminNaoExiste() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.promoverParaAdmin(1L));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).delete(any(Usuario.class));

        // Act
        usuarioService.deletarUsuario(1L);

        // Assert
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    @DisplayName("Não deve permitir admin deletar a própria conta")
    void naoDevePermitirAdminDeletarPropriaconta() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> usuarioService.deletarUsuario(2L));
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    @DisplayName("Não deve permitir deletar outro admin")
    void naoDevePermitirDeletarOutroAdmin() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        
        Usuario outroAdmin = new Usuario();
        outroAdmin.setId(3L);
        outroAdmin.setNome("Outro Admin");
        outroAdmin.setEmail("outro@test.com");
        outroAdmin.setRoles(new HashSet<>(Arrays.asList(roleAdmin)));
        
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(outroAdmin));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> usuarioService.deletarUsuario(3L));
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void deveLancarExcecaoAoDeletarUsuarioInexistente() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");
        
        when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.deletarUsuario(999L));
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve buscar usuário logado")
    void deveBuscarUsuarioLogado() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("joao@test.com");
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(usuario));

        // Act
        Usuario resultado = usuarioService.buscarUsuarioLogado();

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
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("inexistente@test.com");
        when(usuarioRepository.findByEmail("inexistente@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.buscarUsuarioLogado());
    }

    @Test
    @DisplayName("Deve listar usuários paginados")
    void deveListarUsuariosPaginados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> page = new PageImpl<>(Arrays.asList(usuario, admin));
        when(usuarioRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<Usuario> resultado = usuarioService.listarTodosPaginado(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        verify(usuarioRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar usuários por palavra-chave")
    void deveBuscarUsuariosPorPalavraChave() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> page = new PageImpl<>(Arrays.asList(usuario));
        when(usuarioRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(
                "João", "João", pageable)).thenReturn(page);

        // Act
        Page<Usuario> resultado = usuarioService.buscarPorPalavraChave("João", pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("João Silva", resultado.getContent().get(0).getNome());
        verify(usuarioRepository, times(1))
                .findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase("João", "João", pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não encontrar usuários por palavra-chave")
    void deveRetornarPaginaVaziaQuandoNaoEncontrarUsuarios() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> page = new PageImpl<>(Collections.emptyList());
        when(usuarioRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(
                "Inexistente", "Inexistente", pageable)).thenReturn(page);

        // Act
        Page<Usuario> resultado = usuarioService.buscarPorPalavraChave("Inexistente", pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.getTotalElements());
        assertTrue(resultado.getContent().isEmpty());
    }
}