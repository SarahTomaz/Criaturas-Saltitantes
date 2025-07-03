# 🦘 Criaturas Saltitantes - Simulação Interativa v2.0

Uma simulação gráfica interativa de criaturas saltitantes com visualização em tempo real, desenvolvida em Java com interface Swing.

## 🎯 Funcionalidades Principais

### ✨ Visualização Gráfica Completa
- **Criaturas**: Círculos azuis com ID e quantidade de moedas visíveis
- **Clusters**: Hexágonos verdes mostrando número de criaturas e total de moedas
- **Guardião**: Diamante vermelho com moedas destacadas em fundo dourado
- **Horizonte**: Linha graduada de 0 a 100 com marcações visuais
- **Animações**: Sombras, contornos definidos e legendas explicativas

### ⚡ Controle de Velocidade
- **Lenta (1s)** - Para observação detalhada
- **Normal (500ms)** - Velocidade equilibrada
- **Rápida (300ms)** ⭐ **PADRÃO** - Dinâmica e fluida
- **Muito Rápida (100ms)** - Para resultados rápidos

### 📊 Configurações e Estatísticas
- **Número de Criaturas**: Configurável de 2 a 20
- **Estatísticas em Tempo Real**: Contador de elementos e distribuição de moedas
- **Log Detalhado**: Registro de cada iteração com informações completas
- **Sistema de Usuários**: Login, senha, avatar e pontuação

## 🎮 Como Usar

### 1. Executar a Aplicação
```bash
# Compilar
javac -d . src/**/*.java

# Executar
java Main
```

### 2. Fazer Login
- Use as credenciais padrão ou crie uma nova conta
- Escolha um avatar para personalização

### 3. Configurar Simulação
- Selecione o número de criaturas (2-20)
- Escolha a velocidade da simulação
- Clique em "Executar Simulação"

### 4. Acompanhar Execução
- **Visualização Gráfica**: Observe movimento e colisões em tempo real
- **Estatísticas**: Monitore contadores no topo da tela
- **Log**: Acompanhe detalhes de cada iteração
- **Controles**: Use "Parar Simulação" se necessário

## 🏗️ Arquitetura do Sistema

### Estrutura de Pastas
```
src/
├── Main.java              # Ponto de entrada da aplicação
├── model/                 # Modelos de dados
│   ├── Criatura.java      # Entidade criatura
│   ├── Cluster.java       # Agrupamento de criaturas
│   ├── GuardiaoHorizonte.java # Guardião do horizonte
│   ├── Simulacao.java     # Controlador da simulação
│   └── Usuario.java       # Entidade usuário
├── service/               # Lógica de negócio
│   ├── SimuladorService.java    # Serviços da simulação
│   ├── UsuarioService.java      # Gerenciamento de usuários
│   └── EstatisticasService.java # Cálculos e estatísticas
└── ui/                    # Interface gráfica
    ├── MainFrame.java     # Janela principal
    ├── LoginDialog.java   # Tela de login
    ├── SimulacaoPanel.java # Painel da simulação
    └── EstatisticasPanel.java # Painel de estatísticas

test/                      # Testes unitários e integração
├── java/model/           # Testes dos modelos
└── TesteSuiteCompleta.java # Suite de testes completa
```

## 🎯 Regras da Simulação

### Movimento
- **Criaturas**: `posição += (random(-1,1) * moedas)`
- **Clusters**: `posição += (random(-1,1) * totalMoedas)`
- **Guardião**: `posição += (random(-1,1) * moedas)`

### Formação de Clusters
- Criaturas na mesma posição (diferença < 0.01) formam clusters
- Cluster rouba metade das moedas da criatura mais próxima
- Criaturas do cluster são desativadas

### Guardião do Horizonte
- Elimina criaturas e clusters por colisão
- Ganha todas as moedas dos elementos eliminados
- **Moedas atualizadas em tempo real** durante a simulação

### Condições de Sucesso
1. **Apenas guardião sobrevive** (todas criaturas e clusters eliminados)
2. **Guardião + 1 criatura** (guardião com mais moedas que a criatura)
3. **Timeout**: 1000 iterações sem sucesso

## 🧪 Testes

### Executar Suite de Testes
```bash
java TesteSuiteCompleta
```

### Cobertura de Testes
- ✅ Criação e movimento de criaturas
- ✅ Formação e comportamento de clusters
- ✅ Funcionamento do guardião
- ✅ Condições de sucesso da simulação
- ✅ Sistema de usuários e pontuação
- ✅ Serviços e estatísticas

## 🛠️ Tecnologias Utilizadas

- **Java 11+** - Linguagem principal
- **Swing** - Interface gráfica
- **JUnit** - Framework de testes (para testes futuros)
- **Git** - Controle de versão

## 📈 Melhorias Implementadas

### Versão Atual (v2.0)
- ✅ Visualização gráfica completa em tempo real
- ✅ Controle de velocidade customizável
- ✅ Interface modernizada com estatísticas dinâmicas
- ✅ Moedas visíveis para todos os elementos
- ✅ Sombras e melhorias visuais
- ✅ Documentação completa
- ✅ Suite de testes abrangente

### Versão Anterior (v1.0)
- Simulação básica com output texto
- Interface simples
- Velocidade fixa

## 📋 Requisitos do Sistema

- **Java**: Versão 11 ou superior
- **OS**: Windows, macOS ou Linux
- **Memória**: Mínimo 512MB RAM
- **Resolução**: Mínimo 1024x768

## 🤝 Contribuindo

1. Faça fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📜 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👥 Autores

- **Sarah Tomaz** - Desenvolvimento principal - [@SarahTomaz](https://github.com/SarahTomaz)

---

### 🎉 Divirta-se explorando o mundo das Criaturas Saltitantes!
