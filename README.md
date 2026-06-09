# Sistema de Gerenciamento de Biblioteca

Projeto educacional que implementa progressivamente diferentes arquiteturas de software com Java puro (sem frameworks).

## Arquiteturas abordadas

| Etapa | Arquitetura | Status |
|-------|-------------|--------|
| 1 | Camadas (Layered Architecture) | Em andamento |
| 2 | Hexagonal (Ports & Adapters) | Pendente |
| 3 | Comunicação Assíncrona por Eventos | Pendente |

## Etapa 1 — Arquitetura em Camadas

Organização do código em camadas com responsabilidades bem definidas:

```
src/
└── biblioteca/
    ├── dominio/        # Entidades e regras de negócio puras
    ├── aplicacao/      # Casos de uso e lógica de aplicação
    ├── infraestrutura/ # Acesso e persistência de dados
    └── apresentacao/   # Interface com o usuário (console)
```

### Entidades do domínio

| Classe / Enum | Descrição |
|---|---|
| `Livro` | Entidade principal; controla estoque via `realizarEmprestimo()` |
| `Usuario` | Leitor da biblioteca; possui situação ATIVO/SUSPENSO |
| `Emprestimo` | Associação entre usuário e livro com controle de datas |
| `SituacaoEmprestimo` | Enum: `ATIVO`, `DEVOLVIDO`, `ATRASADO` |
| `SituacaoUsuario` | Enum: `ATIVO`, `SUSPENSO` |

### Pré-requisitos

- Java 17+

### Como compilar e executar

```bash
javac -d out src/biblioteca/**/*.java
java -cp out biblioteca.apresentacao.Main
```

## Conceitos aplicados

- Separação de responsabilidades por camada
- Regras de negócio isoladas no domínio (POJOs puros, sem frameworks)
- Dependências sempre apontando para baixo: `apresentacao → aplicacao → infraestrutura → dominio`
