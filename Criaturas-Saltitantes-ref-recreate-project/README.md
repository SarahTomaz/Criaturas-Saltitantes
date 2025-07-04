# Criaturas Saltitantes

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)

**Criaturas Saltitantes** é um sistema de simulação e gerenciamento de usuários desenvolvido em Java, com foco em testes abrangentes e qualidade de software. O projeto implementa um simulador de criaturas com sistema de autenticação, estatísticas detalhadas e interface gráfica.

## 📋 Índice

- [Características](#-características)
- [Tecnologias](#-tecnologias)
- [Instalação](#-instalação)
- [Como Usar](#-como-usar)
- [Arquitetura](#-arquitetura)
- [Testes](#-testes)
- [Documentação](#-documentação)
- [Contribuição](#-contribuição)
- [Licença](#-licença)

## 🚀 Características

### Funcionalidades Principais
- **Sistema de Usuários**: Cadastro, autenticação e gerenciamento completo
- **Simulador de Criaturas**: Sistema de simulação com lógica personalizada
- **Estatísticas Detalhadas**: Relatórios completos de performance e rankings
- **Interface Gráfica**: GUI intuitiva desenvolvida com Swing
- **Persistência de Dados**: Armazenamento seguro usando serialização
- **Sistema de Segurança**: Hash de senhas com SHA-256

### Características Técnicas
- **Cobertura de Testes**: 100% MC/DC coverage
- **Arquitetura Limpa**: Separação clara entre camadas (Model-View-Service)
- **Testes Abrangentes**: Unitários, integração, propriedades e performance
- **Documentação Completa**: JavaDoc e documentação técnica
- **Padrões de Qualidade**: Seguindo boas práticas de desenvolvimento

## 🛠 Tecnologias

### Core
- **Java 17+**: Linguagem principal
- **Maven**: Gerenciamento de dependências e build
- **Swing**: Interface gráfica

### Testes
- **JUnit 5**: Framework de testes unitários
- **Mockito**: Mocking e dublês de teste
- **jqwik**: Testes baseados em propriedades
- **AssertJ**: Assertions fluentes e expressivas

### Qualidade
- **Cobertura MC/DC**: Testes estruturais completos
- **Testes de Propriedades**: Verificação de invariantes
- **Testes de Concorrência**: Verificação de thread-safety
- **Testes de Performance**: Análise de desempenho

## 📦 Instalação

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+
- Git

### Clonando o Repositório
```bash
git clone https://github.com/SarahTomaz/Criaturas-Saltitantes.git
cd Criaturas-Saltitantes
```

### Compilação
```bash
# Compilar o projeto
mvn clean compile

# Executar testes
mvn test

# Gerar JAR executável
mvn clean package
```

### Execução
```bash
# Executar a aplicação
java -jar target/CriatV2-1.0-SNAPSHOT.jar

# Ou usando Maven
mvn exec:java -Dexec.mainClass="org.example.Main"
```

## 💻 Como Usar

### Interface Gráfica
1. **Inicialização**: Execute o JAR ou use o comando Maven
2. **Login/Cadastro**: Crie uma conta ou faça login
3. **Simulações**: Execute simulações de criaturas
4. **Estatísticas**: Visualize seus resultados e rankings

### Funcionalidades do Sistema

#### Gerenciamento de Usuários
```java
UsuarioService service = new UsuarioService();

// Cadastrar usuário
service.cadastrarUsuario("usuario", "senha123", "avatar.png");

// Autenticar
Usuario usuario = service.autenticar("usuario", "senha123");

// Atualizar dados
service.alterarAvatar("usuario", "novo_avatar.png");
```

#### Sistema de Estatísticas
```java
EstatisticasService stats = new EstatisticasService(usuarioService, simuladorService);

// Relatório completo
String relatorio = stats.gerarRelatorioCompleto();

// Estatísticas específicas
String userStats = stats.getEstatisticasUsuario("usuario");

// Rankings
List<Usuario> ranking = stats.getRankingUsuarios();
```

## 🏗 Arquitetura

### Estrutura do Projeto
```
src/
├── main/java/org/example/
│   ├── model/          # Modelos de domínio
│   │   ├── Usuario.java
│   │   ├── Criatura.java
│   │   ├── Simulacao.java
│   │   └── ...
│   ├── service/        # Lógica de negócio
│   │   ├── UsuarioService.java
│   │   ├── SimuladorService.java
│   │   └── EstatisticasService.java
│   ├── ui/            # Interface gráfica
│   │   ├── MainFrame.java
│   │   ├── LoginDialog.java
│   │   └── ...
│   └── Main.java      # Ponto de entrada
└── test/java/service/ # Testes abrangentes
    ├── TestUsuarioService.java
    ├── TestUsuarioServiceMCDC.java
    ├── TestUsuarioServiceProperties.java
    └── ...
```

### Padrões Utilizados
- **MVC (Model-View-Controller)**: Separação clara de responsabilidades
- **Service Layer**: Encapsulamento da lógica de negócio
- **Repository Pattern**: Abstração do acesso a dados
- **Observer Pattern**: Notificações de eventos na UI

## 🧪 Testes

O projeto possui uma suite de testes abrangente com diferentes tipos de cobertura:

### Tipos de Teste
- **Testes Unitários**: Cobertura básica de funcionalidades
- **Testes MC/DC**: Cobertura estrutural completa (100%)
- **Testes de Propriedades**: Verificação de invariantes do sistema
- **Testes de Concorrência**: Thread-safety e condições de corrida
- **Testes de Performance**: Análise de desempenho e escalabilidade
- **Testes de Integração**: Interação entre componentes

### Executando Testes
```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=TestUsuarioService

# Com relatório de cobertura
mvn test jacoco:report
```

### Estrutura de Testes
```
test/java/service/
├── TestUsuarioService.java           # Testes principais
├── TestUsuarioServiceMCDC.java       # Cobertura MC/DC
├── TestUsuarioServiceProperties.java # Testes de propriedades
├── TestUsuarioServiceConcorrencia.java # Testes de concorrência
├── TestUsuarioServicePerformance.java # Testes de performance
├── TestUsuarioServiceMocks.java      # Testes com mocks
├── TestUsuarioServiceIsolated.java   # Testes isolados
└── TestUsuarioServiceExceptions.java # Testes de exceções
```

## 📚 Documentação

### Documentação Técnica
- [Documentação Completa](DOCUMENTATION.md) - Guia técnico detalhado
- [JavaDoc](target/site/apidocs/) - Documentação da API
- [Relatórios de Teste](target/site/surefire-report.html) - Resultados dos testes

### Guias
- [Guia de Contribuição](CONTRIBUTING.md)
- [Guia de Testes](docs/TESTING.md)
- [Arquitetura do Sistema](docs/ARCHITECTURE.md)

## 🤝 Contribuição

Contribuições são bem-vindas! Siga estes passos:

1. **Fork** o projeto
2. **Crie** uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. **Push** para a branch (`git push origin feature/AmazingFeature`)
5. **Abra** um Pull Request

### Diretrizes
- Mantenha a cobertura de testes em 100%
- Siga os padrões de código existentes
- Adicione documentação para novas funcionalidades
- Execute todos os testes antes de enviar

## 📋 Roadmap

- [ ] Implementar sistema de multiplayer
- [ ] Adicionar mais tipos de criaturas
- [ ] Melhorar interface gráfica
- [ ] Implementar sistema de conquistas
- [ ] Adicionar suporte a plugins
- [ ] Migrar para base de dados relacional

## 🐛 Problemas Conhecidos

Consulte as [Issues](https://github.com/SarahTomaz/Criaturas-Saltitantes/issues) para problemas conhecidos e solicitações de funcionalidades.

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👩‍💻 Autores

- **Sarah Tomaz** - *Desenvolvimento Principal* - [@SarahTomaz](https://github.com/SarahTomaz)

## 🙏 Agradecimentos

- Comunidade Java pela documentação excelente
- Contribuidores do JUnit e Mockito
- Equipe do jqwik pelo framework de property-based testing

---

**⭐ Se este projeto foi útil para você, considere dar uma estrela!**

Para mais informações, consulte a [documentação completa](DOCUMENTATION.md) ou entre em contato através das [Issues](https://github.com/SarahTomaz/Criaturas-Saltitantes/issues).
