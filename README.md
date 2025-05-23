#  Simulador de Criaturas Saltitantes

**Alunos:** Sarah Tomaz e Thiago Henrique  
**Disciplina:** Teste de Software  

##  Visão Geral
Simulação de um ecossistema onde criaturas se movem aleatoriamente e roubam moedas entre si. Desenvolvido em Java com:
- Interface gráfica Swing
- Lógica de simulação baseada em movimentos aleatórios
- Testes unitários com JUnit 5

##  Como Executar
1. Compile o projeto:
```bash
javac SimulacaoCriaturasSaltitantes.java
```

**Atributos:**
- `double[] posicoes`: Posições no horizonte (0.0 a 1.0)
- `int[] moedas`: Saldo de cada criatura (inicial: 1.000.000)
- `Color[] cores`: Cores aleatórias para visualização
- `Timer timer`: Controla iterações

---

##  Funcionalidades

###  Métodos de Controle
| Método                | Ação                                                                 |
|-----------------------|----------------------------------------------------------------------|
| `inicializarSimulacao()` | Valida entrada, cria arrays e define estado inicial (moedas/cores)   |
| `iniciarSimulacao()`    | Inicia timer (retorna `0` se sucesso, `-1` se já estiver rodando)    |
| `pausarSimulacao()`     | Pausa a simulação                                                    |
| `reiniciarSimulacao()`  | Reseta todos os dados e a interface                                  |

###  Lógica da Simulação
1. **Movimento Aleatório**:  
   Cada criatura move-se com `nova_posição = pos_atual + r * (moedas/1.000.000)`  
   (onde `r ∈ [-1, 1]` é um valor aleatório)

2. **Roubo de Moedas**:  
   - Identifica a criatura mais próxima (`encontrarCriaturaMaisProxima()`)
   - Rouba **metade** das moedas da vítima

---

##  Renderização Gráfica
**Painel Gráfico (JPanel):**
- **Horizonte**: Linha horizontal central
- **Criaturas**: Círculos coloridos com:
  - Tamanho proporcional a `log(moedas)`
  - ID e saldo exibidos
- **Escala Dinâmica**: Ajusta posições para caber na tela

---

##  Testes Unitários (JUnit 5)

###  Validação de Entrada
```java
@Test
void inicializarComNumeroNegativo() {
    assertThrows(IllegalArgumentException.class, () -> simulacao.inicializarSimulacao(-1));
}
```

###  Comportamento Aleatório
```java
@Test
void avancarIteracaoComSeedFixa() {
    Random seededRandom = new Random(1); // Resultados determinísticos
    // Verifica posições/moedas após 2 iterações
    assertEquals(2_000_000, simulacao.getMoedas()[0]);
}
```

###  Algoritmo de Proximidade
```java
@Test
void encontrarCriaturaMaisProxima() {
    double[] posicoesTeste = {0.5, 1.0, 0.6};
    assertEquals(2, simulacao.encontrarCriaturaMaisProxima(0)); // Distância 0.1
}
```

---

##  Como Executar
1. Compile o projeto Java:
   ```bash
   javac SimulacaoCriaturasSaltitantes.java
   ```
2. Execute:
   ```bash
   java SimulacaoCriaturasSaltitantes
   ```
3. Insira o número de criaturas e clique em **Iniciar**!

---

##  Observações
- **Reprodutibilidade**: Use `Random` com seed para depuração.
- **Extensibilidade**: Pode ser adaptado para simulações mais complexas (ex.: adicionar predadores).

---
> *(Código-fonte disponível no repositório)*
