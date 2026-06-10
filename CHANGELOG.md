# Changelog

Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) e o projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [Unreleased]

## [2.0.0] - 2026-06-10

Refatoração completa do projeto aplicando princípios de Clean Code (Projeto Final da disciplina). A versão anterior está preservada no branch [`original`](../../tree/original).

### Added

- Camada de service: `ProductService`, `CategoryService`, `UserService`, `OrderService`, `AuthenticationService` e `StripeCheckoutService` — controllers não acessam mais repositórios;
- Exceções de domínio (`ResourceNotFoundException`, `BusinessException`, `InvalidTokenException`) com tratamento centralizado em `GlobalExceptionHandler` (`@RestControllerAdvice`) e status HTTP corretos (404/400/401/403);
- Suíte de testes unitários com 50 testes (JUnit 5 + Mockito + AssertJ) cobrindo services, controllers, segurança (JWT) e domínio — 64,1% de cobertura de linhas (JaCoCo);
- Linter integrado em três etapas: Spotless (google-java-format) e Checkstyle falhando o build Maven, git hook de pre-commit (`.githooks/pre-commit`) e pipeline de CI no GitHub Actions;
- Relatório de cobertura JaCoCo gerado em todo build de teste;
- Propriedades de configuração para Stripe (moeda, URLs de retorno) e JWT (issuer, expiração).

### Changed

- Pacote raiz renomeado de `com.api.EcomTracker` para `com.api.ecomtracker` (convenção Java) e projeto reorganizado por camadas: `controller`, `service`, `repository`, `domain`, `dto`, `exception`, `security`, `config`;
- Entidades renomeadas para o singular: `Users`→`User`, `Products`→`Product`, `Categories`→`Category`, `Orders`→`Order`, `Roles`→`Role`; enum `OrderStatusDTO`→`OrderStatus`; campo `Orders.amount`→`Order.totalPrice` (tabelas e colunas do banco preservadas);
- DTOs padronizados na convenção `*Request`/`*Response` (antes havia três convenções de nomenclatura); campo `category_id` do payload de produto renomeado para `categoryId`;
- Injeção de dependências por construtor (campos `final`) em substituição a `@Autowired` em campo, em todos os beans;
- Autorização de administrador centralizada no `SecurityConfiguration` (removida a checagem manual duplicada em três endpoints);
- `OrderService` deixou de retornar `ResponseEntity` e de acessar `SecurityContextHolder`; a integração com o Stripe foi extraída para `StripeCheckoutService` e as regras de estoque para o domínio (`Product.hasStockFor`/`decreaseStock`);
- Verificação de JWT unificada em um único método do `TokenService`; expiração calculada com `Instant` (sem fuso hardcoded);
- Login unificado por e-mail (token usa e-mail como subject);
- Valores mágicos externalizados para `application.properties`: URLs de sucesso/cancelamento do Stripe, moeda, issuer e tempo de expiração do token;
- `POST /orders` passou a responder JSON `{orderId, checkoutUrl}` em vez de uma string com a URL;
- Código todo formatado pelo Spotless (estilo AOSP) e em um único idioma (inglês), sem comentários redundantes;
- Dockerfile atualizado (build com JDK 17) e `docker-compose.yml` parametrizado por variáveis de ambiente.

### Fixed

- `username` informado no registro era descartado e substituído pelo e-mail — agora é persistido;
- `UserDetailsService` retornava `null` para usuário inexistente (violava o contrato e causava NPE) — agora lança `UsernameNotFoundException`;
- Recurso inexistente respondia 400 ou 500 (`getReferenceById` sem tratamento) — agora responde 404;
- Webhook do Stripe respondia 200 mesmo com assinatura inválida — agora responde 400;
- Mensagens de exceção internas vazavam para o cliente (`e.getMessage()` cru) — agora há mensagens controladas;
- Suíte de testes antiga apontava para rotas inexistentes e não executava — substituída por suíte nova e funcional.

### Removed

- Código morto: `UserUpdateDTO` e `OrderResponseDTO` (nunca usados);
- Dependência redundante `javax.persistence-api` (já fornecida pelo starter de JPA);
- Comentários redundantes e mensagens não profissionais.

### Security

- Chaves secretas do Stripe e segredo JWT removidos do `application.properties` e do `docker-compose.yml` — agora são lidos exclusivamente de variáveis de ambiente.

## [1.0.0]

- Versão original do projeto (branch `original`): API de estoque com autenticação JWT, produtos, categorias, pedidos e integração com Stripe.

[Unreleased]: https://github.com/arturoburigo/EcomTracker/compare/v2.0.0...HEAD
[2.0.0]: https://github.com/arturoburigo/EcomTracker/compare/original...main
[1.0.0]: https://github.com/arturoburigo/EcomTracker/tree/original
