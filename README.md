# Simulador de Criaturas Saltitantes v2.0

## Descrição
Sistema de simulação de criaturas saltitantes com formação de clusters, guardião do horizonte, sistema de usuários e estatísticas completas.

## Funcionalidades Implementadas

### ✅ Requisitos Atendidos
- [x] **Formação de Clusters**: Criaturas na mesma posição se unem e somam moedas
- [x] **Roubo de Moedas**: Clusters roubam metade das moedas da criatura mais próxima
- [x] **Guardião do Horizonte**: Criatura especial que elimina clusters
- [x] **Condições de Sucesso**: Simulação bem-sucedida quando resta apenas guardião ou guardião + 1 criatura com mais moedas
- [x] **Sistema de Usuários**: Login, senha, avatar e pontuação
- [x] **Estatísticas Completas**: Pontuação por usuário, simulações executadas, médias de sucesso
- [x] **Interface Gráfica**: Sistema completo com login, simulação e estatísticas
- [x] **Testes Unitários**: Suite completa de 19 testes (100% de sucesso)

## Como Executar

### Pré-requisitos
- Java 8 ou superior
- Sistema operacional Windows/Linux/macOS

### Executar os Testes
```bash
cd "c:\Users\Sarah Tomaz\Teste de Software\teste\src"
javac TesteSuiteCompleta.java
java TesteSuiteCompleta
```

### Executar a Aplicação
```bash
cd "c:\Users\Sarah Tomaz\Teste de Software\teste\src"
javac Main.java
java Main
```

## Estrutura do Projeto

```
src/
├── Main.java                     # Ponto de entrada da aplicação
├── TesteSuiteCompleta.java       # Suite de testes completa (19 testes)
├── model/                        # Classes de domínio
│   ├── Criatura.java            # Criatura saltitante
│   ├── Cluster.java             # Agrupamento de criaturas  
│   ├── GuardiaoHorizonte.java   # Guardião especial
│   ├── Simulacao.java           # Lógica da simulação
│   └── Usuario.java             # Modelo de usuário
├── service/                      # Camada de serviços
│   ├── UsuarioService.java      # Gerenciamento de usuários
│   ├── SimuladorService.java    # Execução de simulações
│   └── EstatisticasService.java # Cálculo de estatísticas
└── ui/                          # Interface gráfica
    ├── MainFrame.java           # Janela principal
    ├── LoginDialog.java         # Tela de login/cadastro
    ├── SimulacaoPanel.java      # Painel de simulação
    └── EstatisticasPanel.java   # Painel de estatísticas
```

## Algoritmos Implementados

### Movimento das Criaturas
- **Fórmula**: `xi ← xi + rgi` onde r ∈ [-1, 1] é um número aleatório
- Aplicado a criaturas, clusters e guardião

### Formação de Clusters
1. Verificar colisões entre criaturas (mesma posição)
2. Criar cluster somando moedas: `gij = gi + gj`
3. Roubar metade das moedas da criatura mais próxima
4. Desativar criaturas originais

### Guardião do Horizonte
- Índice n+1 para n criaturas
- Moedas iniciais = 0
- Elimina clusters ao colidir: `gn+1 = gn+1 + gij`

### Condições de Sucesso
- **Sucesso**: Apenas guardião OU guardião + 1 criatura com `gn+1 > gi`
- **Falha**: Timeout (máximo de iterações atingido)

## Funcionalidades da Interface

### 1. Sistema de Login
- Cadastro de novos usuários
- Autenticação com senha criptografada (SHA-256)
- Gestão de avatares

### 2. Simulação Interativa
- Execução de simulações em tempo real
- Visualização do estado atual
- Log de eventos da simulação

### 3. Estatísticas Avançadas
- Ranking de usuários por pontuação
- Média de simulações bem-sucedidas
- Relatórios detalhados de desempenho

### 4. Gerenciamento de Usuários
- Listar todos os usuários
- Remover usuários
- Cadastrar novos usuários

## Testes Implementados

### Cobertura: 100% (19/19 testes passando)

#### Testes Unitários
- **Criatura**: Criação, movimento, gestão de moedas, estados
- **Cluster**: Formação, soma de moedas, roubo, movimento  
- **GuardiaoHorizonte**: Criação, movimento, eliminação
- **Usuario**: Criação, autenticação, simulações
- **Simulacao**: Criação, execução, iterações

#### Testes de Serviços
- **UsuarioService**: CRUD de usuários
- **SimuladorService**: Execução de simulações
- **EstatisticasService**: Geração de relatórios

#### Testes de Integração
- **Fluxo Completo**: Usuario → Simulação → Estatísticas

## Qualidade do Código

### Padrões Implementados
- **Service Layer Pattern**: Separação clara de responsabilidades
- **Repository Pattern**: Persistência de usuários
- **MVC Pattern**: Model-View-Controller na interface
- **Strategy Pattern**: Diferentes tipos de movimento

### Boas Práticas
- Encapsulamento rigoroso
- Validação de entrada
- Tratamento de exceções
- Documentação JavaDoc
- Testes unitários abrangentes

## Dados Persistidos
- **Usuários**: Salvos em `data/usuarios.dat` (serialização Java)
- **Configurações**: Mantidas em memória durante execução

## Autores
Implementação completa dos requisitos do Simulador de Criaturas Saltitantes v2.0

## Licença
Projeto acadêmico - Teste de Software
