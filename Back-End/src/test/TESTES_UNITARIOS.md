# Testes Unitários - Biblioteca Online

## Resumo

Foram implementados **72 testes unitários** para o projeto Biblioteca Online, cobrindo os principais serviços da aplicação.

## Resultado dos Testes

✅ **72 testes executados com sucesso**
- ✅ 0 falhas
- ✅ 0 erros
- ✅ 0 testes ignorados

## Cobertura de Testes

### 1. LivroServiceTest (17 testes)
Testa todas as funcionalidades relacionadas ao gerenciamento de livros:

- ✅ Salvar novo livro com sucesso
- ✅ Atualizar livro existente
- ✅ Validação de nome duplicado
- ✅ Validação de ano de publicação (inválido, menor que 1500, futuro)
- ✅ Criar novo autor ao salvar livro
- ✅ Validação de autor existente
- ✅ Deletar livro
- ✅ Listar todos os livros
- ✅ Buscar livro por ID
- ✅ Listar livros paginados
- ✅ Buscar livros por gênero
- ✅ Buscar livros por palavra-chave
- ✅ Listar livros mais recentes
- ✅ Buscar livros por autor ID

### 2. UsuarioServiceTest (14 testes)
Testa funcionalidades de gerenciamento de usuários:

- ✅ Listar todos os usuários
- ✅ Promover usuário para admin
- ✅ Validação: não promover usuário que já é admin
- ✅ Validação: promover usuário inexistente
- ✅ Validação: role ADMIN não existe
- ✅ Deletar usuário com sucesso
- ✅ Validação: admin não pode deletar própria conta
- ✅ Validação: não permitir deletar outro admin
- ✅ Validação: deletar usuário inexistente
- ✅ Buscar usuário logado
- ✅ Listar usuários paginados
- ✅ Buscar usuários por palavra-chave
- ✅ Retornar página vazia quando não encontrar usuários

### 3. AvaliacaoServiceTest (13 testes)
Testa funcionalidades de avaliações de livros:

- ✅ Salvar nova avaliação com sucesso
- ✅ Atualizar avaliação existente ao salvar novamente
- ✅ Validação: livro inexistente
- ✅ Validação: usuário inexistente
- ✅ Atualizar avaliação existente
- ✅ Validação: atualizar avaliação de outro usuário
- ✅ Validação: atualizar avaliação inexistente
- ✅ Deletar avaliação com sucesso
- ✅ Validação: deletar avaliação de outro usuário
- ✅ Validação: deletar avaliação inexistente
- ✅ Buscar avaliações por livro ID
- ✅ Retornar lista vazia quando não há avaliações
- ✅ Validar nota ao salvar avaliação

### 4. CurtidosServiceTest (13 testes)
Testa funcionalidades de curtidas de livros:

- ✅ Obter usuário logado com sucesso
- ✅ Validação: buscar usuário logado inexistente
- ✅ Listar livros curtidos paginados
- ✅ Retornar página vazia quando não tem livros curtidos
- ✅ Exibir "Desconhecido" quando livro não tem autor
- ✅ Curtir livro com sucesso
- ✅ Não adicionar livro já curtido novamente
- ✅ Validação: curtir livro inexistente
- ✅ Descurtir livro com sucesso
- ✅ Validação: descurtir livro inexistente
- ✅ Descurtir livro mesmo que não esteja na lista
- ✅ Curtir múltiplos livros
- ✅ Manter outros livros curtidos ao descurtir um

### 5. AutorServiceTest (14 testes)
Testa funcionalidades de gerenciamento de autores:

- ✅ Listar todos os autores
- ✅ Retornar lista vazia quando não há autores
- ✅ Buscar autor por ID com sucesso
- ✅ Validação: buscar autor inexistente por ID
- ✅ Buscar autor por nome ignorando case
- ✅ Retornar Optional vazio quando autor não existe por nome
- ✅ Listar autores paginados
- ✅ Retornar página vazia quando não há autores paginados
- ✅ Deletar autor com sucesso
- ✅ Validação: deletar autor inexistente
- ✅ Buscar autores com diferentes variações de case no nome
- ✅ Listar autores com paginação em diferentes páginas
- ✅ Verificar se autor existe antes de deletar
- ✅ Buscar autor com todos os atributos preenchidos

### 6. BibliotecaOnlineApplicationTests (1 teste)
- ✅ Context loads (teste de inicialização da aplicação)

## Tecnologias Utilizadas

- **JUnit 5**: Framework de testes
- **Mockito**: Framework para criação de mocks
- **Spring Boot Test**: Suporte para testes em aplicações Spring Boot
- **AssertJ**: Biblioteca de assertions (implícita via JUnit)

## Padrões de Teste Implementados

### 1. Arrange-Act-Assert (AAA)
Todos os testes seguem o padrão AAA:
- **Arrange**: Configuração dos mocks e dados de teste
- **Act**: Execução do método a ser testado
- **Assert**: Verificação dos resultados

### 2. Nomenclatura Descritiva
Os testes utilizam nomes descritivos em português que explicam o comportamento esperado:
- `deveSalvarNovoLivroComSucesso()`
- `deveLancarExcecaoAoSalvarLivroComNomeDuplicado()`
- `naoDevePermitirAdminDeletarPropriaconta()`

### 3. Isolamento de Testes
Cada teste é independente e utiliza mocks para isolar as dependências:
- Uso de `@Mock` para dependências
- Uso de `@InjectMocks` para a classe sob teste
- Método `@BeforeEach` para configuração inicial

### 4. Cobertura de Cenários
Os testes cobrem:
- ✅ Casos de sucesso
- ✅ Casos de erro/exceção
- ✅ Validações de regras de negócio
- ✅ Casos de borda (edge cases)

## Como Executar os Testes

### Executar todos os testes:
```bash
mvnw test
```

### Executar testes de um serviço específico:
```bash
mvnw test -Dtest=LivroServiceTest
mvnw test -Dtest=UsuarioServiceTest
mvnw test -Dtest=AvaliacaoServiceTest
mvnw test -Dtest=CurtidosServiceTest
mvnw test -Dtest=AutorServiceTest
```

### Executar múltiplos testes específicos:
```bash
mvnw test -Dtest="LivroServiceTest,UsuarioServiceTest"
```

## Configuração Adicional

Foi criado o arquivo `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` com o conteúdo `mock-maker-inline` para garantir compatibilidade com JDK 21.

## Benefícios dos Testes Implementados

1. **Confiabilidade**: Garantem que o código funciona conforme esperado
2. **Refatoração Segura**: Permitem modificar o código com confiança
3. **Documentação**: Servem como documentação viva do comportamento do sistema
4. **Detecção Precoce de Bugs**: Identificam problemas antes de chegarem à produção
5. **Qualidade do Código**: Incentivam boas práticas de programação

## Métricas de Qualidade

- **Taxa de Sucesso**: 100% (72/72 testes passando)
- **Cobertura de Serviços**: 5 serviços principais testados
- **Tempo de Execução**: ~9.6 segundos para todos os testes
- **Manutenibilidade**: Alta (testes bem organizados e documentados)

## Próximos Passos Recomendados

1. Adicionar testes de integração para controllers
2. Implementar testes para os demais serviços (EmailService, FileStorageService, etc.)
3. Adicionar relatório de cobertura de código (JaCoCo)
4. Implementar testes de performance
5. Adicionar testes end-to-end com Selenium/Playwright

---

**Data de Criação**: 29/09/2025
**Versão**: 1.0
**Status**: ✅ Todos os testes passando
