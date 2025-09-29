package br.ifba.edu.BibliotecaOnline.service;

import br.ifba.edu.BibliotecaOnline.entities.Autor;
import br.ifba.edu.BibliotecaOnline.repository.AutorRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AutorService")
class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private AutorService autorService;

    private Autor autor1;
    private Autor autor2;

    @BeforeEach
    void setUp() {
        // Setup autor 1
        autor1 = new Autor();
        autor1.setId(1L);
        autor1.setNomeAutor("Machado de Assis");
        autor1.setDescricaoDoAutor("Escritor brasileiro do século XIX");
        autor1.setFotoAutor("machado.jpg");

        // Setup autor 2
        autor2 = new Autor();
        autor2.setId(2L);
        autor2.setNomeAutor("Clarice Lispector");
        autor2.setDescricaoDoAutor("Escritora brasileira do século XX");
        autor2.setFotoAutor("clarice.jpg");
    }

    @Test
    @DisplayName("Deve listar todos os autores")
    void deveListarTodosOsAutores() {
        // Arrange
        List<Autor> autores = Arrays.asList(autor1, autor2);
        when(autorRepository.findAll()).thenReturn(autores);

        // Act
        List<Autor> resultado = autorService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Machado de Assis", resultado.get(0).getNomeAutor());
        assertEquals("Clarice Lispector", resultado.get(1).getNomeAutor());
        verify(autorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há autores")
    void deveRetornarListaVaziaQuandoNaoHaAutores() {
        // Arrange
        when(autorRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Autor> resultado = autorService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(autorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar autor por ID com sucesso")
    void deveBuscarAutorPorIdComSucesso() {
        // Arrange
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor1));

        // Act
        Autor resultado = autorService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Machado de Assis", resultado.getNomeAutor());
        assertEquals(1L, resultado.getId());
        verify(autorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar autor inexistente por ID")
    void deveLancarExcecaoAoBuscarAutorInexistentePorId() {
        // Arrange
        when(autorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
            () -> autorService.buscarPorId(999L));
        assertTrue(exception.getMessage().contains("Autor não encontrado"));
        verify(autorRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve buscar autor por nome ignorando case")
    void deveBuscarAutorPorNomeIgnorandoCase() {
        // Arrange
        when(autorRepository.findByNomeAutorIgnoreCase("machado de assis"))
            .thenReturn(Optional.of(autor1));

        // Act
        Optional<Autor> resultado = autorService.buscarPorNomeIgnoreCase("machado de assis");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Machado de Assis", resultado.get().getNomeAutor());
        verify(autorRepository, times(1)).findByNomeAutorIgnoreCase("machado de assis");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando autor não existe por nome")
    void deveRetornarOptionalVazioQuandoAutorNaoExistePorNome() {
        // Arrange
        when(autorRepository.findByNomeAutorIgnoreCase("Autor Inexistente"))
            .thenReturn(Optional.empty());

        // Act
        Optional<Autor> resultado = autorService.buscarPorNomeIgnoreCase("Autor Inexistente");

        // Assert
        assertFalse(resultado.isPresent());
        verify(autorRepository, times(1)).findByNomeAutorIgnoreCase("Autor Inexistente");
    }

    @Test
    @DisplayName("Deve listar autores paginados")
    void deveListarAutoresPaginados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Autor> page = new PageImpl<>(Arrays.asList(autor1, autor2));
        when(autorRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<Autor> resultado = autorService.listarPaginado(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertEquals("Machado de Assis", resultado.getContent().get(0).getNomeAutor());
        verify(autorRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há autores paginados")
    void deveRetornarPaginaVaziaQuandoNaoHaAutoresPaginados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Autor> page = new PageImpl<>(Arrays.asList());
        when(autorRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<Autor> resultado = autorService.listarPaginado(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(0, resultado.getTotalElements());
        assertTrue(resultado.getContent().isEmpty());
    }

    @Test
    @DisplayName("Deve deletar autor com sucesso")
    void deveDeletarAutorComSucesso() {
        // Arrange
        when(autorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(autorRepository).deleteById(1L);

        // Act
        autorService.deletar(1L);

        // Assert
        verify(autorRepository, times(1)).existsById(1L);
        verify(autorRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar autor inexistente")
    void deveLancarExcecaoAoDeletarAutorInexistente() {
        // Arrange
        when(autorRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
            () -> autorService.deletar(999L));
        assertTrue(exception.getMessage().contains("Autor não encontrado"));
        verify(autorRepository, times(1)).existsById(999L);
        verify(autorRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve buscar autores com diferentes variações de case no nome")
    void deveBuscarAutoresComDiferentesVariacoesDeCaseNoNome() {
        // Arrange
        when(autorRepository.findByNomeAutorIgnoreCase("MACHADO DE ASSIS"))
            .thenReturn(Optional.of(autor1));
        when(autorRepository.findByNomeAutorIgnoreCase("machado de assis"))
            .thenReturn(Optional.of(autor1));
        when(autorRepository.findByNomeAutorIgnoreCase("Machado De Assis"))
            .thenReturn(Optional.of(autor1));

        // Act
        Optional<Autor> resultado1 = autorService.buscarPorNomeIgnoreCase("MACHADO DE ASSIS");
        Optional<Autor> resultado2 = autorService.buscarPorNomeIgnoreCase("machado de assis");
        Optional<Autor> resultado3 = autorService.buscarPorNomeIgnoreCase("Machado De Assis");

        // Assert
        assertTrue(resultado1.isPresent());
        assertTrue(resultado2.isPresent());
        assertTrue(resultado3.isPresent());
        assertEquals("Machado de Assis", resultado1.get().getNomeAutor());
        assertEquals("Machado de Assis", resultado2.get().getNomeAutor());
        assertEquals("Machado de Assis", resultado3.get().getNomeAutor());
    }

    @Test
    @DisplayName("Deve listar autores com paginação em diferentes páginas")
    void deveListarAutoresComPaginacaoEmDiferentesPaginas() {
        // Arrange
        Pageable pageable1 = PageRequest.of(0, 1);
        Pageable pageable2 = PageRequest.of(1, 1);
        
        Page<Autor> page1 = new PageImpl<>(Arrays.asList(autor1), pageable1, 2);
        Page<Autor> page2 = new PageImpl<>(Arrays.asList(autor2), pageable2, 2);
        
        when(autorRepository.findAll(pageable1)).thenReturn(page1);
        when(autorRepository.findAll(pageable2)).thenReturn(page2);

        // Act
        Page<Autor> resultado1 = autorService.listarPaginado(pageable1);
        Page<Autor> resultado2 = autorService.listarPaginado(pageable2);

        // Assert
        assertEquals(1, resultado1.getContent().size());
        assertEquals(1, resultado2.getContent().size());
        assertEquals("Machado de Assis", resultado1.getContent().get(0).getNomeAutor());
        assertEquals("Clarice Lispector", resultado2.getContent().get(0).getNomeAutor());
        assertEquals(2, resultado1.getTotalElements());
        assertEquals(2, resultado2.getTotalElements());
    }

    @Test
    @DisplayName("Deve verificar se autor existe antes de deletar")
    void deveVerificarSeAutorExisteAntesDeDeletar() {
        // Arrange
        when(autorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(autorRepository).deleteById(1L);

        // Act
        autorService.deletar(1L);

        // Assert
        verify(autorRepository, times(1)).existsById(1L);
        verify(autorRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve buscar autor com todos os atributos preenchidos")
    void deveBuscarAutorComTodosOsAtributosPreenchidos() {
        // Arrange
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor1));

        // Act
        Autor resultado = autorService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Machado de Assis", resultado.getNomeAutor());
        assertEquals("Escritor brasileiro do século XIX", resultado.getDescricaoDoAutor());
        assertEquals("machado.jpg", resultado.getFotoAutor());
    }
}