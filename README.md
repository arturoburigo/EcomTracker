# EcomTracker

API REST de controle de estoque e vendas para e-commerce, desenvolvida em Java 8 com Spring Boot 2.7, MySQL e pagamentos via Stripe.

Este repositório é o objeto do **Projeto Final da disciplina de Clean Code**: o branch [`original`](../../tree/original) preserva a versão antiga do sistema e o branch `main` contém a versão refatorada.

## Descrição e principais funcionalidades

O EcomTracker gerencia estoque e vendas com pagamento real:

- **Autenticação e autorização** — login com JWT (`POST /auth`), registro de usuários comuns (`POST /users/register`) e administradores (`POST /users/register/admin`), com papéis `USER`/`ADMIN` controlando o acesso por rota via Spring Security;
- **Gestão de produtos** — CRUD com paginação, soft delete e controle de quantidade em estoque (escrita restrita a administradores);
- **Gestão de categorias** — cadastro, listagem paginada e inativação;
- **Pedidos com pagamento** — criação de pedido com validação de estoque, baixa automática de quantidade, cálculo do total e geração de sessão de checkout no Stripe;
- **Webhook de pagamento** — recebe `checkout.session.completed` do Stripe com verificação de assinatura e marca o pedido como `PAID`;
- **Documentação OpenAPI/Swagger** (`/swagger-ui.html`), migrações com Flyway e empacotamento com Docker.

## Análise dos principais problemas detectados

A versão no branch `original` apresentava os seguintes code smells:

| # | Code smell | Onde ocorria |
|---|-----------|--------------|
| 1 | **Controllers gordos / sem camada de service** — controllers acessavam repositórios diretamente e concentravam regra de negócio, autorização e tratamento de erro | `ProductsController`, `UsersController`, `CategoriesController` |
| 2 | **Inversão de camadas** — service retornando `ResponseEntity` e montando respostas HTTP no domínio | `OrderService.createOrder` |
| 3 | **Código duplicado** — checagem manual de `ROLE_ADMIN` repetida 3× (já garantida pelo `SecurityConfiguration`), verificação de JWT duplicada, blocos try/catch idênticos, cálculo de preço repetido | `ProductsController`, `UsersController`, `TokenService`, `OrderService` |
| 4 | **Tratamento de exceções genérico** — `catch (Exception)` engolindo erros, `RuntimeException` para tudo, "não encontrado" respondendo 400 (ou 500 via `getReferenceById`), `e.getMessage()` vazando detalhes internos ao cliente | controllers e services em geral |
| 5 | **Nomes ruins e inconsistentes** — entidades no plural (`Users`, `Products`...), enum `OrderStatusDTO` que não é DTO, campo `amount` mapeado na coluna `total_price`, três convenções de DTO convivendo, `getCategory_id()` em snake_case, pacote `com.api.EcomTracker` com maiúsculas | todo o projeto |
| 6 | **Valores mágicos e segredos no código** — URLs de produção hardcoded, `"brl"`, `multiply(100)`, fuso `"-03:00"`, expiração fixa, e **chaves secretas do Stripe commitadas** no `application.properties` e `docker-compose.yml` | `OrderService`, `TokenService`, configs |
| 7 | **Comentários redundantes e idiomas misturados** — comentários óbvios em PT num código em EN, mensagens de erro alternando idiomas e mensagem não profissional (`"What are you doing here buddy?"`) | `OrderService`, `UsersController` |
| 8 | **Field injection** — `@Autowired` em campo em todos os beans, escondendo dependências e dificultando teste | todos os beans |
| 9 | **Violação de contrato do framework** — `UserDetailsService` retornando `null` em vez de `UsernameNotFoundException`; identidade de login dividida entre `findByEmail` e `findByUsername` | `UserAuthenticationService`, `SecurityFilter` |
| 10 | **Long Method** — `createOrder` com ~70 linhas e 5 responsabilidades (auth, validação, estoque, persistência, Stripe) | `OrderService` |
| 11 | **Suíte de testes quebrada** — testes apontavam para rotas inexistentes (`/auth/register` vs `/users/register`) e o contexto nem subia | `UserControllerTest` |
| 12 | **Formatação inconsistente e ausência de linter** — arquivo inteiro com indentação deslocada, espaçamento irregular | `AuthenticationController` e outros |

Também havia um **bug funcional**: o `username` informado no registro era descartado (a entidade gravava o e-mail no lugar).

## Estratégias de refatoração utilizadas

Refatoração incremental, com técnicas do catálogo de Fowler e princípios do Clean Code:

1. **Rename Class/Field/Package** — entidades no singular (`User`, `Product`, `Category`, `Order`, `Role`), enum `OrderStatus`, pacote `com.api.ecomtracker` minúsculo, DTOs padronizados na convenção `*Request`/`*Response`. Os nomes de tabelas/colunas foram preservados (`@Table`/`@Column`) para manter as migrações Flyway válidas;
2. **Extract Class (camada de service)** — `ProductService`, `CategoryService`, `UserService` e `AuthenticationService`; controllers passaram a apenas traduzir HTTP ⇄ domínio. A organização foi reestruturada por camadas: `controller`, `service`, `repository`, `domain`, `dto`, `exception`, `security`, `config`;
3. **Replace Error Code with Exception + handler global** — exceções de domínio (`ResourceNotFoundException`, `BusinessException`, `InvalidTokenException`) tratadas em um `@RestControllerAdvice` único, com status HTTP corretos (404/400/401/403) e sem vazar detalhes internos;
4. **Extract Method/Class no long method** — `OrderService.checkout` ficou com a regra de negócio e a integração com o Stripe foi extraída para `StripeCheckoutService`; regras de estoque viraram métodos do domínio (`Product.hasStockFor`, `decreaseStock`);
5. **Eliminação de duplicação** — autorização centralizada apenas no `SecurityConfiguration`; verificação de JWT unificada em um único método;
6. **Constructor Injection** — todos os beans usam injeção por construtor com campos `final` (dependências explícitas e testáveis);
7. **Externalização de configuração** — URLs do Stripe, moeda, issuer e expiração do token movidos para `application.properties` (`@ConfigurationProperties`/`@Value`); chaves secretas removidas do repositório e lidas de variáveis de ambiente;
8. **Correções de contrato e bugs** — `UserDetailsService` lança `UsernameNotFoundException`; login unificado por e-mail; `username` do registro passou a ser persistido; webhook responde 400 para assinatura inválida; remoção de código morto (`UserUpdateDTO`, `OrderResponseDTO`) e de dependência redundante (`javax.persistence-api`).

### Ferramentas

- **Spotless** (google-java-format, estilo AOSP) — formatação automática, com `spotless:check` falhando o build;
- **Checkstyle** — regras de nomenclatura, imports, tamanho de método (máx. 40 linhas), estrutura e chaves; falha o build em violação;
- **Integração do linter em três etapas**: build Maven (fase `validate`), git hook de pre-commit (`.githooks/pre-commit`) e CI no GitHub Actions (`.github/workflows/ci.yml`);
- **JaCoCo** — relatório de cobertura gerado em todo build de teste.

## Testes implementados e cobertura

Suíte com **50 testes unitários** (JUnit 5 + Mockito + AssertJ), sem dependência de banco de dados nem de contexto Spring completo:

- **Services** — `CategoryService`, `ProductService`, `UserService` e `OrderService`: caminhos felizes, recurso inexistente, e-mail duplicado, papel inválido, estoque insuficiente e cálculo do total;
- **Controllers** — `ProductController`, `CategoryController`, `UserController` e `OrderController` via MockMvc standalone com o `GlobalExceptionHandler` registrado: status 201/200/204, `Location`, 400 de validação e 404;
- **Segurança** — `TokenService` (round-trip do JWT, token malformado, secret e issuer errados) e `JwtAuthenticationFilter` (token válido, inválido e ausente);
- **Domínio** — regras de estoque de `Product`.

**Cobertura atingida (JaCoCo): 64,1% de linhas e 64,8% de métodos** — acima da meta de ~50% do trabalho. A versão original tinha 1 classe de teste que sequer executava.

```bash
./mvnw clean verify          # lint + testes + cobertura
open target/site/jacoco/index.html   # relatório de cobertura
```

## Instalação e execução

### Pré-requisitos

- JDK 17 (compila para Java 8) e Docker/Docker Compose;
- Conta no [Stripe](https://stripe.com) (modo teste) para as chaves de pagamento.

### Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `JWT_SECRET` | Segredo de assinatura dos tokens JWT |
| `STRIPE_API_KEY` | Chave secreta da API do Stripe (`sk_test_...`) |
| `STRIPE_WEBHOOK_SECRET` | Segredo do webhook (`whsec_...`) |
| `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` | Conexão MySQL (apenas execução sem Docker) |

### Com Docker (recomendado)

```bash
export JWT_SECRET=algum-segredo
export STRIPE_API_KEY=sk_test_...
export STRIPE_WEBHOOK_SECRET=whsec_...
docker compose up --build
```

Sobe o MySQL 8, a API em `http://localhost:8080` (migrações Flyway aplicadas automaticamente) e o Stripe CLI encaminhando webhooks para a aplicação. A documentação interativa fica em `http://localhost:8080/swagger-ui.html`.

### Sem Docker

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ecomtracker
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=root
export JWT_SECRET=... STRIPE_API_KEY=... STRIPE_WEBHOOK_SECRET=...
./mvnw spring-boot:run
```

### Linter e hook de pre-commit

```bash
./mvnw spotless:apply        # formata o código
./mvnw spotless:check checkstyle:check   # valida sem buildar tudo
git config core.hooksPath .githooks      # ativa o hook de pre-commit (uma vez)
```
