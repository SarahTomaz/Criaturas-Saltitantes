# Manual de Uso - Simulação de Criaturas Saltitantes

## Sobre o Programa

O programa é uma simulação interativa de criaturas saltitantes com sistema de usuários e pontuação. Os usuários podem criar simulações, acompanhar estatísticas e gerenciar contas.

## Requisitos

- Java 11 ou superior
- Maven para compilação

## Como Executar

1. Compile o projeto:
   ```
   mvn clean compile
   ```

2. Execute o programa:
   ```
   java -cp target/classes org.example.Main
   ```

## Primeiros Passos

### 1. Login Inicial
- Ao iniciar o programa, será exibida uma tela de login
- Se é a primeira vez utilizando, clique em "Cadastrar"
- Caso já tenha conta, insira login e senha

### 2. Cadastro de Usuário
- Login: deve ser único no sistema
- Senha: mínimo de 4 caracteres
- Avatar: nome do arquivo de imagem (opcional, padrão: default.png)
- Confirme a senha digitando-a novamente

### 3. Tela Principal
Após o login, você terá acesso ao menu principal com as opções:
- **Nova Simulação**: criar e executar simulações
- **Estatísticas**: visualizar desempenho e rankings
- **Gerenciar Usuários**: cadastrar, listar ou remover usuários
- **Logout**: sair da conta atual

## Como Usar as Simulações

### Configurando uma Simulação
1. Clique em "Nova Simulação"
2. Configure os parâmetros:
   - **Número de Criaturas**: entre 2 e 20 criaturas
   - **Velocidade**: escolha entre Lenta, Normal, Rápida ou Muito Rápida
3. Clique em "Iniciar Simulação"

### Acompanhando a Simulação
- **Visualização Gráfica**: mostra criaturas, clusters e guardião em tempo real
- **Log de Eventos**: registra cada iteração e eventos importantes
- **Estatísticas**: exibe informações sobre moedas e formação de clusters
- **Controles**: use "Parar Simulação" para interromper antes do fim

### Resultados
- **Simulação Bem-sucedida**: quando o guardião coleta todas as moedas
- **Pontuação**: ganhe 1 ponto por simulação bem-sucedida
- **Total de Simulações**: contabiliza todas as tentativas

## Gerenciamento de Usuários

### Opções Disponíveis
- **Cadastrar Usuário**: criar nova conta
- **Listar Usuários**: ver todos os usuários cadastrados com suas pontuações
- **Remover Usuário**: excluir conta (não é possível remover usuário logado)

## Estatísticas

### Visualizar Desempenho
- Acesse "Estatísticas" no menu principal
- Veja informações sobre:
  - Total de simulações realizadas
  - Simulações bem-sucedidas
  - Taxa de sucesso
  - Posição no ranking geral

### Relatório Completo
- Lista todos os usuários ordenados por pontuação
- Mostra estatísticas detalhadas de cada usuário

## Persistência de Dados

- Os dados dos usuários são salvos automaticamente
- Arquivo de dados: `data/usuarios.ser`
- As informações persistem entre execuções do programa

## Dicas de Uso

1. **Estratégia**: simulações com menos criaturas tendem a ser mais rápidas
2. **Velocidade**: use velocidade alta para simulações rápidas
3. **Pontuação**: foque em completar simulações para aumentar sua pontuação
4. **Backup**: mantenha backup do arquivo `data/usuarios.ser` se necessário

## Resolução de Problemas

### Erros Comuns
- **Login já existe**: escolha um nome de usuário diferente
- **Senha muito curta**: use pelo menos 4 caracteres
- **Erro de autenticação**: verifique login e senha
- **Problema na simulação**: reinicie o programa se necessário

### Limpeza de Dados
- Para resetar todos os usuários, exclua o arquivo `data/usuarios.ser`
- O programa criará um novo arquivo na próxima execução

## Funcionalidades Técnicas

- Interface gráfica em Java Swing
- Salvamento automático em arquivo serializado
- Simulação em tempo real com timer configurável
- Sistema de hash para senhas
- Validação de entrada de dados

---
