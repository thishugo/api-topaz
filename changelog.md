# Changelog

Todas as alterações notáveis deste projeto serão documentadas neste arquivo.

O formato segue [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/), e o projeto adota [Semantic Versioning](https://semver.org/lang/pt-BR/).

## [1.0.0] - 2026-09-30

### Adicionado

- API REST para criação, redirecionamento, listagem recente e métricas de URLs curtas.
- Persistência JPA de URLs e histórico de cliques com H2 no WildFly.
- Geração de códigos Base62 com serialização por EJB Singleton e aliases personalizados.
- Registro assíncrono de cliques e erros de negócio no formato RFC 7807.
- Empacotamento WAR, imagem Docker WildFly 10 e testes unitários iniciais.
- SPA Angular para criação de links, cópia da URL curta, histórico e consulta de métricas.
