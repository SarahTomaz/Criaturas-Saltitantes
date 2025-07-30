# Documentação JavaDoc - Simulação de Criaturas Saltitantes

## Visão Geral

Este documento apresenta a documentação JavaDoc completa do projeto "Simulação de Criaturas Saltitantes", um sistema interativo de simulação com gerenciamento de usuários e estatísticas.

## Estrutura do Projeto

### Pacotes Principais

- **org.example.model**: Classes de modelo de domínio
- **org.example.service**: Classes de serviços de negócio
- **org.example.ui**: Classes de interface gráfica
- **org.example**: Classes principais e utilitários

---

## Pacote org.example.model

### Classe Usuario

**Descrição**: Representa um usuário do sistema com autenticação, pontuação e histórico de simulações.

```java
/**
 * Representa um usuário do sistema de simulação de criaturas saltitantes.
 * 
 * <p>Esta classe gerencia informações de usuário incluindo autenticação segura,
 * pontuação baseada em simulações bem-sucedidas e persistência de dados.</p>
 * 
 * <p><b>Invariantes da classe:</b></p>
 * <ul>
 *   <li>login não pode ser nulo ou vazio</li>
 *   <li>senhaHash é sempre uma string SHA-256 válida</li>
 *   <li>pontuacao >= 0 (número de simulações bem-sucedidas)</li>
 *   <li>totalSimulacoes >= pontuacao</li>
 *   <li>taxaSucesso está entre 0.0 e 1.0</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class Usuario implements Serializable
```

**Construtores**:
```java
/**
 * Cria um novo usuário com as credenciais especificadas.
 * 
 * @param login Nome de usuário único (não pode ser nulo ou vazio)
 * @param senha Senha em texto plano (será hasheada internamente)
 * @param avatar Nome do arquivo de avatar (pode ser nulo, padrão: "default.png")
 * @throws IllegalArgumentException se login for nulo/vazio ou senha inválida
 */
public Usuario(String login, String senha, String avatar)
```

**Métodos Principais**:
```java
/**
 * Autentica o usuário com a senha fornecida.
 * 
 * @param senha Senha em texto plano para verificação
 * @return true se a senha for válida, false caso contrário
 */
public boolean autenticar(String senha)

/**
 * Incrementa a pontuação e total de simulações para uma simulação bem-sucedida.
 * 
 * <p>Este método deve ser chamado quando uma simulação é completada com sucesso.</p>
 */
public void incrementarSimulacaoBemSucedida()

/**
 * Incrementa apenas o total de simulações (para simulações não bem-sucedidas).
 */
public void incrementarTotalSimulacoes()

/**
 * Calcula a taxa de sucesso do usuário.
 * 
 * @return Taxa de sucesso entre 0.0 e 1.0, ou 0.0 se nenhuma simulação foi realizada
 */
public double getTaxaSucesso()
```

### Classe Criatura

**Descrição**: Representa uma criatura individual na simulação com posição, moedas e comportamento de movimento.

```java
/**
 * Representa uma criatura na simulação com posição, moedas e estado.
 * 
 * <p>As criaturas se movem aleatoriamente no horizonte e podem formar clusters
 * para transferir moedas ao guardião.</p>
 * 
 * <p><b>Invariantes da classe:</b></p>
 * <ul>
 *   <li>id deve ser positivo e único</li>
 *   <li>posicao deve estar inicialmente entre 0 e 100</li>
 *   <li>moedas deve ser 1.000.000 no início (conforme especificação)</li>
 *   <li>ativa reflete o estado atual da criatura</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class Criatura
```

**Construtores**:
```java
/**
 * Cria uma nova criatura com 1.000.000 de moedas.
 * 
 * @param id Identificador único da criatura (deve ser positivo)
 * @param posicao Posição inicial no horizonte (0-100)
 * @throws IllegalArgumentException se id ≤ 0 ou posicao fora do intervalo [0,100]
 */
public Criatura(int id, double posicao)
```

**Métodos Principais**:
```java
/**
 * Move a criatura aleatoriamente no horizonte.
 * 
 * <p>O movimento é calculado como: posicao = posicao + (r * 100),
 * onde r é um valor aleatório entre -1 e 1.</p>
 * 
 * <p>Só executa se a criatura estiver ativa.</p>
 */
public void mover()

/**
 * Adiciona moedas à criatura.
 * 
 * @param quantidade Quantidade de moedas a adicionar (deve ser positiva)
 */
public void adicionarMoedas(int quantidade)

/**
 * Remove moedas da criatura.
 * 
 * @param quantidade Quantidade máxima de moedas a remover
 * @return Quantidade de moedas efetivamente removidas (limitada pelo saldo atual)
 */
public int removerMoedas(int quantidade)

/**
 * Desativa a criatura, impedindo movimentos futuros.
 */
public void desativar()
```

### Classe Cluster

**Descrição**: Representa um agrupamento de criaturas próximas que podem transferir moedas.

```java
/**
 * Representa um cluster (agrupamento) de criaturas na simulação.
 * 
 * <p>Clusters são formados quando criaturas estão próximas e podem
 * transferir suas moedas ao guardião quando ele passa pela região.</p>
 * 
 * <p><b>Regras de formação:</b></p>
 * <ul>
 *   <li>Mínimo de 2 criaturas para formar um cluster</li>
 *   <li>Criaturas devem estar em posições próximas</li>
 *   <li>Clusters podem crescer ao incorporar criaturas próximas</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class Cluster
```

### Classe GuardiaoHorizonte

**Descrição**: Representa o guardião que coleta moedas dos clusters.

```java
/**
 * Representa o guardião que se move pelo horizonte coletando moedas dos clusters.
 * 
 * <p>O guardião se move de forma determinística e coleta moedas quando
 * passa por clusters de criaturas.</p>
 * 
 * <p><b>Comportamento:</b></p>
 * <ul>
 *   <li>Move-se continuamente pelo horizonte</li>
 *   <li>Coleta moedas de clusters que encontra</li>
 *   <li>O objetivo é coletar todas as moedas das criaturas</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class GuardiaoHorizonte
```

### Classe Simulacao

**Descrição**: Representa uma instância completa de simulação com todas as entidades.

```java
/**
 * Representa uma simulação completa de criaturas saltitantes.
 * 
 * <p>Coordena todas as entidades (criaturas, clusters, guardião) e
 * gerencia o estado e progresso da simulação.</p>
 * 
 * <p><b>Estados da simulação:</b></p>
 * <ul>
 *   <li>INICIADA: simulação em execução</li>
 *   <li>BEM_SUCEDIDA: guardião coletou todas as moedas</li>
 *   <li>FALHADA: simulação não foi concluída com sucesso</li>
 *   <li>INTERROMPIDA: simulação parada pelo usuário</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class Simulacao
```

---

## Pacote org.example.service

### Classe UsuarioService

**Descrição**: Gerencia operações relacionadas a usuários, incluindo autenticação e persistência.

```java
/**
 * Serviço responsável pelo gerenciamento de usuários do sistema.
 * 
 * <p>Fornece funcionalidades completas para:</p>
 * <ul>
 *   <li>Cadastro e autenticação de usuários</li>
 *   <li>Persistência automática em arquivo</li>
 *   <li>Controle de sessão (login/logout)</li>
 *   <li>Validações de negócio</li>
 *   <li>Atualização de pontuações</li>
 * </ul>
 * 
 * <p><b>Arquivo de persistência:</b> data/usuarios.ser</p>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class UsuarioService
```

**Métodos Principais**:
```java
/**
 * Cadastra um novo usuário no sistema.
 * 
 * @param login Nome de usuário único (não pode ser vazio)
 * @param senha Senha com pelo menos 4 caracteres
 * @param avatar Nome do arquivo de avatar (opcional)
 * @return true se o cadastro foi bem-sucedido, false se login já existe
 * @throws IllegalArgumentException se parâmetros inválidos
 */
public boolean cadastrarUsuario(String login, String senha, String avatar)

/**
 * Autentica um usuário e inicia sua sessão.
 * 
 * @param login Nome de usuário
 * @param senha Senha em texto plano
 * @return Usuario autenticado ou null se credenciais inválidas
 */
public Usuario autenticar(String login, String senha)

/**
 * Encerra a sessão do usuário atual.
 */
public void logout()

/**
 * Remove um usuário do sistema.
 * 
 * @param login Nome do usuário a ser removido
 * @return true se removido com sucesso, false se usuário não existe ou está logado
 */
public boolean removerUsuario(String login)

/**
 * Atualiza a pontuação de um usuário específico.
 * 
 * @param login Login do usuário
 * @param incremento Incremento na pontuação (1 para sucesso, 0 para falha)
 * @return true se atualização foi bem-sucedida
 */
public boolean atualizarPontuacao(String login, int incremento)
```

### Classe SimuladorService

**Descrição**: Gerencia a lógica de simulação e execução de iterações.

```java
/**
 * Serviço responsável pela execução e gerenciamento de simulações.
 * 
 * <p>Coordena todas as fases da simulação:</p>
 * <ul>
 *   <li>Criação de criaturas e guardião</li>
 *   <li>Execução de iterações</li>
 *   <li>Formação de clusters</li>
 *   <li>Movimento de entidades</li>
 *   <li>Coleta de moedas</li>
 *   <li>Verificação de condições de vitória</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class SimuladorService
```

**Métodos Principais**:
```java
/**
 * Cria uma nova simulação com parâmetros especificados.
 * 
 * @param usuario Usuário que executa a simulação
 * @param numCriaturas Número de criaturas (2-20)
 * @param limitIteracoes Limite máximo de iterações
 * @throws IllegalArgumentException se parâmetros inválidos
 */
public void criarNovaSimulacao(Usuario usuario, int numCriaturas, int limitIteracoes)

/**
 * Executa uma única iteração da simulação.
 * 
 * @return true se simulação deve continuar, false se finalizada
 */
public boolean executarProximaIteracao()

/**
 * Executa uma simulação completa até o fim.
 * 
 * @param usuario Usuário que executa a simulação
 * @param numCriaturas Número de criaturas
 * @param limitIteracoes Limite de iterações
 * @return Resultado da simulação
 */
public Simulacao executarSimulacaoCompleta(Usuario usuario, int numCriaturas, int limitIteracoes)
```

### Classe EstatisticasService

**Descrição**: Gera relatórios e estatísticas do sistema.

```java
/**
 * Serviço responsável pela geração de estatísticas e relatórios.
 * 
 * <p>Fornece análises detalhadas sobre:</p>
 * <ul>
 *   <li>Desempenho individual de usuários</li>
 *   <li>Rankings globais</li>
 *   <li>Estatísticas de simulações</li>
 *   <li>Relatórios consolidados</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class EstatisticasService
```

**Métodos Principais**:
```java
/**
 * Gera estatísticas detalhadas de um usuário específico.
 * 
 * @param login Nome do usuário
 * @return String formatada com estatísticas ou mensagem de erro
 */
public String getEstatisticasUsuario(String login)

/**
 * Gera relatório completo com ranking de todos os usuários.
 * 
 * @return String formatada com relatório completo
 */
public String gerarRelatorioCompleto()

/**
 * Obtém o ranking de usuários ordenado por pontuação.
 * 
 * @return Lista de usuários ordenada por pontuação decrescente
 */
public List<Usuario> getRankingUsuarios()
```

---

## Pacote org.example.ui

### Classe MainFrame

**Descrição**: Janela principal da aplicação com menu e navegação.

```java
/**
 * Janela principal da aplicação de simulação de criaturas saltitantes.
 * 
 * <p>Fornece interface para:</p>
 * <ul>
 *   <li>Autenticação de usuários</li>
 *   <li>Navegação entre funcionalidades</li>
 *   <li>Acesso a simulações</li>
 *   <li>Visualização de estatísticas</li>
 *   <li>Gerenciamento de usuários</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class MainFrame extends JFrame
```

### Classe SimulacaoPanel

**Descrição**: Interface para configuração e execução de simulações.

```java
/**
 * Painel de interface para execução de simulações interativas.
 * 
 * <p>Permite ao usuário:</p>
 * <ul>
 *   <li>Configurar parâmetros da simulação</li>
 *   <li>Executar e pausar simulações</li>
 *   <li>Visualizar progresso em tempo real</li>
 *   <li>Acompanhar log de eventos</li>
 *   <li>Ver estatísticas atualizadas</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class SimulacaoPanel extends JDialog
```

### Classe LoginDialog

**Descrição**: Dialog para autenticação e cadastro de usuários.

```java
/**
 * Dialog de autenticação de usuários.
 * 
 * <p>Funcionalidades:</p>
 * <ul>
 *   <li>Login com validação de credenciais</li>
 *   <li>Acesso ao cadastro de novos usuários</li>
 *   <li>Validação de entrada</li>
 *   <li>Feedback de erros</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class LoginDialog extends JDialog
```

### Classe EstatisticasPanel

**Descrição**: Interface para visualização de estatísticas e relatórios.

```java
/**
 * Painel para exibição de estatísticas e relatórios do sistema.
 * 
 * <p>Apresenta:</p>
 * <ul>
 *   <li>Estatísticas individuais do usuário</li>
 *   <li>Rankings globais</li>
 *   <li>Histórico de simulações</li>
 *   <li>Gráficos de desempenho</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class EstatisticasPanel extends JPanel
```

---

## Pacote org.example

### Classe Main

**Descrição**: Classe principal que inicia a aplicação.

```java
/**
 * Classe principal da aplicação de simulação de criaturas saltitantes.
 * 
 * <p>Responsável por:</p>
 * <ul>
 *   <li>Inicialização da interface gráfica</li>
 *   <li>Configuração do Look and Feel</li>
 *   <li>Tratamento de exceções globais</li>
 * </ul>
 * 
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class Main
```

**Método Principal**:
```java
/**
 * Método principal que inicia a aplicação.
 * 
 * <p>Configura o ambiente Swing e exibe a janela principal.</p>
 * 
 * @param args Argumentos da linha de comando (não utilizados)
 */
public static void main(String[] args)
```

---

## Convenções de Documentação

### Tags JavaDoc Utilizadas

- **@author**: Identificação do autor/sistema
- **@version**: Versão da classe
- **@since**: Versão inicial da classe
- **@param**: Descrição de parâmetros
- **@return**: Descrição do valor de retorno
- **@throws**: Exceções que podem ser lançadas
- **@deprecated**: Marca métodos obsoletos

### Padrões de Nomenclatura

- **Classes**: PascalCase (ex: UsuarioService)
- **Métodos**: camelCase (ex: cadastrarUsuario)
- **Constantes**: UPPER_SNAKE_CASE (ex: ARQUIVO_USUARIOS)
- **Variáveis**: camelCase (ex: usuarioLogado)

### Estrutura dos Comentários

1. **Descrição breve**: Uma linha resumindo a funcionalidade
2. **Descrição detalhada**: Explicação completa do comportamento
3. **Invariantes**: Condições que devem sempre ser verdadeiras
4. **Pré-condições**: Requisitos para execução
5. **Pós-condições**: Estado após execução
6. **Parâmetros**: Descrição de cada parâmetro
7. **Retorno**: Descrição do valor retornado
8. **Exceções**: Condições que causam exceções

---

## Compilação da Documentação

Para gerar a documentação JavaDoc HTML:

```bash
# Gerar documentação completa
mvn javadoc:javadoc

# Documentação será gerada em: target/site/apidocs/
```

### Configuração Maven para JavaDoc

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.4.1</version>
    <configuration>
        <source>11</source>
        <target>11</target>
        <encoding>UTF-8</encoding>
        <charset>UTF-8</charset>
        <docencoding>UTF-8</docencoding>
        <windowTitle>Simulação de Criaturas Saltitantes</windowTitle>
        <doctitle>Simulação de Criaturas Saltitantes v2.0</doctitle>
        <author>true</author>
        <version>true</version>
        <use>true</use>
        <splitindex>true</splitindex>
    </configuration>
</plugin>
```

---

## Resumo das Responsabilidades

| Pacote | Responsabilidade |
|--------|------------------|
| model | Entidades de domínio e regras de negócio |
| service | Lógica de aplicação e coordenação |
| ui | Interface gráfica e interação com usuário |
| root | Inicialização e configuração |

Esta documentação JavaDoc fornece uma visão completa da arquitetura e funcionalidades do sistema de simulação de criaturas saltitantes, servindo como referência para desenvolvedores e mantenedores do projeto.
