# Sistema de Gerenciamento de Biblioteca

Projeto educacional que implementa progressivamente diferentes arquiteturas de software com Java puro (sem frameworks).

## Arquiteturas abordadas

| Etapa | Arquitetura | Status |
|-------|-------------|--------|
| 1 | Camadas (Layered Architecture) | Concluída |
| 2 | Hexagonal (Ports & Adapters) | Concluída |
| 3 | Comunicação Assíncrona por Eventos | Concluída |

## Estrutura do projeto

```
src/
└── biblioteca/
    ├── dominio/                  # Entidades, enums, portas e eventos — núcleo puro
    │   ├── porta/
    │   │   ├── entrada/          # Interfaces que o mundo externo usa para acionar o sistema
    │   │   └── saida/            # Interfaces que o sistema usa para acessar recursos externos
    │   └── evento/               # Records de eventos e EventBus genérico
    ├── aplicacao/                # Serviços de caso de uso (implementam as portas de entrada)
    ├── infraestrutura/
    │   └── adaptador/            # Implementações concretas das portas de saída
    └── apresentacao/             # Ponto de entrada e composição do sistema (Main)
```

### Entidades do domínio

| Classe / Enum | Descrição |
|---|---|
| `Livro` | Entidade principal; controla estoque via `realizarEmprestimo()` |
| `Usuario` | Leitor da biblioteca; possui situação `ATIVO`/`SUSPENSO` |
| `Emprestimo` | Associação entre usuário e livro com controle de datas e situação |
| `SituacaoEmprestimo` | Enum: `ATIVO`, `DEVOLVIDO`, `ATRASADO` |
| `SituacaoUsuario` | Enum: `ATIVO`, `SUSPENSO` |
| `EmprestimoRealizadoEvento` | Record imutável publicado após cada novo empréstimo |
| `DevolucaoRegistradaEvento` | Record imutável publicado após cada devolução |
| `EventBus<T>` | Barramento genérico Publisher/Subscriber baseado em `Consumer<T>` |

### Adaptadores disponíveis

| Adaptador | Porta implementada | Mecanismo |
|---|---|---|
| `LivroRepositorioMemoria` | `PortaLivroRepositorio` | `HashMap` em memória |
| `UsuarioRepositorioMemoria` | `PortaUsuarioRepositorio` | `HashMap` em memória |
| `EmprestimoRepositorioMemoria` | `PortaEmprestimoRepositorio` | `HashMap` em memória |
| `LivroRepositorioCsv` | `PortaLivroRepositorio` | Arquivo `.csv` via `java.nio.file` |
| `NotificacaoConsole` | `PortaNotificacao` | `System.out` (legado, etapa 1-2) |
| `ServicoDeNotificacao` | `Consumer<EmprestimoRealizadoEvento>` | Aviso no console |
| `ServicoDeLog` | `Consumer<*Evento>` | Arquivo `biblioteca.log` com timestamp |

## Como compilar e executar

**Pré-requisitos:** Java 17+

```bash
# Compilar (Windows PowerShell)
javac -d out -encoding UTF-8 $(Get-ChildItem src -Recurse -Filter "*.java" | % FullName)

# Compilar (Linux / macOS)
find src -name "*.java" | xargs javac -d out -encoding UTF-8

# Executar
java -cp out biblioteca.apresentacao.Main
```

Após a execução, o arquivo `biblioteca.log` conterá o histórico completo das operações com timestamp.

## Decisões de Design

### Etapa 1 — Arquitetura em Camadas

O projeto começou com uma separação clássica em quatro camadas — `dominio`, `aplicacao`, `infraestrutura` e `apresentacao` — com dependências fluindo sempre de cima para baixo. Essa estrutura organizou o código e isolou as regras de negócio dos detalhes de persistência, mas as classes de serviço ainda importavam diretamente as implementações concretas dos repositórios, criando acoplamento com a infraestrutura.

### Etapa 2 — Arquitetura Hexagonal (Ports & Adapters)

Para eliminar o acoplamento, o domínio passou a declarar o que precisa por meio de interfaces (portas de saída: `PortaLivroRepositorio`, `PortaUsuarioRepositorio`, `PortaEmprestimoRepositorio`, `PortaNotificacao`) e o que oferece por meio de interfaces de entrada (`PortaEmprestimo`). As implementações concretas — repositórios em memória e em CSV — passaram a viver exclusivamente no pacote `infraestrutura.adaptador` e foram conectadas ao núcleo apenas na classe `Main` (composição). O `EmprestimoServico` passou a depender somente de abstrações, tornando possível trocar `LivroRepositorioMemoria` por `LivroRepositorioCsv` com uma única linha alterada, sem tocar em nenhuma regra de negócio.

### Etapa 3 — Comunicação Assíncrona por Eventos (Publisher/Subscriber)

Mesmo com a arquitetura hexagonal, efeitos colaterais como notificações e logs ainda precisavam ser injetados como dependências diretas no serviço. Para desacoplar completamente esses efeitos colaterais, foram criados dois barramentos de eventos genéricos (`EventBus<EmprestimoRealizadoEvento>` e `EventBus<DevolucaoRegistradaEvento>`). Os eventos são modelados como `record`s imutáveis do Java 17, garantindo que os dados publicados não possam ser modificados por nenhum consumidor. O `EmprestimoServico` apenas publica nos barramentos ao final de cada operação; quem reage — `ServicoDeNotificacao` (console) e `ServicoDeLog` (arquivo físico) — é registrado externamente via `bus.assinar(handler)`. O serviço não conhece nenhum consumidor, e novos comportamentos podem ser adicionados sem alterar uma linha do núcleo.

## Dificuldades e Soluções

**Consistência do estoque no adaptador CSV**

Na arquitetura hexagonal, o domínio muta o estado das entidades internamente (por exemplo, `Livro.realizarEmprestimo()` decrementa `quantidadeDisponivel` diretamente no objeto). Adaptadores baseados em referência em memória refletem essa mutação automaticamente, pois operam sobre o mesmo objeto. O `LivroRepositorioCsv`, porém, lê e escreve no arquivo apenas quando os métodos `salvar` ou `listarTodos` são chamados explicitamente, sem visibilidade das mutações ocorridas em memória.

A solução foi chamar `livroRepositorio.salvar(livro)` no `EmprestimoServico` imediatamente após cada mutação da entidade — tanto no `realizarEmprestimo` (estoque decrementado) quanto no `registrarDevolucao` (estoque restaurado). Esse padrão preserva o isolamento do domínio: a entidade `Livro` não sabe nada sobre persistência, e o serviço cumpre sua responsabilidade de coordenar domínio e infraestrutura por meio das portas.
