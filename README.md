Simulador de Criaturas Saltitantes

Alunos: Sarah Tomaz e Thiago Henrique


Visão Geral do Projeto
O Simulador de Criaturas Saltitantes é uma aplicação Java que simula um ecossistema onde criaturas se movem aleatoriamente em um horizonte e roubam moedas umas das outras. Cada criatura começa com 1 milhão de moedas e, a cada iteração, move-se para uma nova posição e rouba metade das moedas da criatura mais próxima.
O projeto foi desenvolvido para a disciplina de Teste de Software e demonstra a implementação de uma interface gráfica usando Swing, lógica de simulação e uma suíte abrangente de testes unitários com JUnit 5.

Arquitetura do Sistema
Classe Principal: SimulacaoCriaturasSaltitantes
Esta classe herda de JFrame e representa tanto a interface gráfica quanto a lógica de simulação.
Constantes e Configurações
private static final int LARGURA_JANELA = 1000;
private static final int ALTURA_JANELA = 600;
private static final int ALTURA_GRAFICO = 400;
private static final int TAMANHO_CRIATURA = 20;
private static final int DELAY_SIMULACAO = 500; 
private static final double FATOR_ESCALA = LARGURA_JANELA * 0.8;
private static final double POSICAO_INICIAL_X = 0.5; 

Atributos Principais
 •	double[] posicoes: Array que armazena a posição de cada criatura no horizonte
 •	int[] moedas: Array que armazena a quantidade de moedas de cada criatura
 •	Color[] cores: Array que armazena a cor única de cada criatura para visualização
 •	Random random: Gerador de números aleatórios para movimento e cores
 •	Timer timer: Controla o timing da simulação (500ms por iteração)

Métodos de Inicialização

inicializarSimulacao()
Propósito: Prepara o estado inicial da simulação com base no número de criaturas especificado pelo usuário.
 •	Valida o número de criaturas (deve ser > 0)
 •	Inicializa todos os arrays com o tamanho correto
 •	Define posição inicial como 0.5 (centro do horizonte)
 •	Atribui 1.000.000 de moedas para cada criatura
 •	Gera cores aleatórias para visualização
 •	Reseta o contador de iterações

Tratamento de Erros:
 •	Lança IllegalArgumentException para números negativos ou zero
 •	Lança NumberFormatException para entradas inválidas

Métodos de Controle da Simulação

iniciarSimulacao()
Propósito: Inicia ou retoma a simulação.
Retornos:
 •	0: Simulação iniciada com sucesso
 •	-1: Falha (timer já está rodando)

Comportamento:
 •	Verifica se o timer não está rodando
 •	Inicializa a simulação se necessário
 •	Atualiza estados dos botões da interface



pausarSimulacao() e reiniciarSimulacao()

Propósito: Controlam o fluxo da simulação.
 •	Pausar: Para o timer e habilita o botão iniciar
 •	Reiniciar: Para o timer, reinicializa todos os dados e reseta a interface

Lógica Central da Simulação
avancarIteracao()
Executa uma iteração completa da simulação.
 Processo:
 1.	Incrementa contador de iterações
 2.	Para cada criatura (em ordem): 
   o	Gera movimento aleatório: r ∈ [-1, 1]
   o	Calcula nova posição: nova_pos = pos_atual + r * (moedas/1.000.000)
   o	Encontra a criatura mais próxima
   o	Rouba metade das moedas da vítima

encontrarCriaturaMaisProxima(int indice)
Encontra a criatura mais próxima de uma criatura específica.
Algoritmo:
  •	Calcula distância euclidiana: |posicao[j] - posicao[i]|
  •	Retorna o índice da criatura com menor distância
  •	Retorna -1 se há apenas uma criatura

Classe Interna PainelGrafico
Renderiza graficamente o estado atual da simulação.

Características visuais:
  •	Linha horizontal: Representa o horizonte
  •	Círculos coloridos: Representam as criaturas
  •	Tamanho variável: Proporcional ao logaritmo das moedas
  •	Números: Identificação de cada criatura
  •	Texto: Quantidade formatada de moedas

Algoritmo de escala:
  •	Calcula min/max das posições atuais
  •	Aplica escala dinâmica para manter todas as criaturas visíveis
  •	Offset garante que as posições sejam exibidas corretamente na tela

Testes:

Classe SimulacaoCriaturasSaltitantesTest
Testes de Validação de Entrada

inicializarComNumeroNegativo() e inicializarComNumeroZero()
  •	Objetivo: Verificar se entradas inválidas são rejeitadas
  •	Expectativa: IllegalArgumentException deve ser lançada
  •	Importância: Garantir robustez na validação de entrada
  
inicializarComNan()
  •	Objetivo: Testar comportamento com entrada não-numérica
  •	Expectativa: NumberFormatException deve ser lançada
  •	Cenário: Usuário digita texto em vez de número

Testes de Inicialização Correta

inicializarComNumeroValido()
  •	Objetivo: Verificar inicialização correta com entrada válida
  •	Validações: 
    o	Número de criaturas corresponde à entrada
    o	Arrays têm tamanho correto
    o	Estado inicial é consistente (iteração = 0)


Testes de Robustez
avancarIteracaoPosicoesNull()
  •	Objetivo: Testar comportamento defensivo
  •	Expectativa: AssertionError deve ser lançada
  •	Cenário: Estado inconsistente onde posições não foram inicializadas

Testes de Comportamento Aleatório

avancarIteracaValoresRandomicosSemSeed()
  •	Objetivo: Verificar que a simulação produz resultados diferentes
  •	Método: Compara estado inicial com estado após execução
  •	Validação: Posições e moedas devem ter mudado
  
avancarIteracaValoresRandomicosComSeed()
  •	Objetivo: Garantir reprodutibilidade para depuração
  •	Método: Usa seed fixa (seed = 1) para resultados determinísticos
  •	Validação: Valores específicos após 2 iterações
    •	Valores esperados: 
      o	Criatura 1: 2.000.000 moedas, posição 1.083...
      o	Criatura 2: 375.000 moedas, posição 0.563...
      o	Criatura 3: 625.000 moedas, posição 0.153...

Testes de Interface e Estados
pauseSimulacaoEstadosEsperados() e reiniciarSimulacaoEstadosEsperados()
  •	Objetivo: Verificar consistência da interface
  •	Validações: 
    o	Estados dos botões (habilitado/desabilitado)
    o	Estado do timer (rodando/parado)
    o	Valores resetados corretamente


Testes de Controle de Fluxo
iniciarSimulacaoComTimerJaRodando() e iniciarSimulacaoCorretamenteEstadosEsperados()
  •	Objetivo: Testar cenários de controle da simulação
    •	Cenários: 
      o	Tentativa de iniciar quando já está rodando (deve falhar)
      o	Início correto da simulação (deve suceder)

Testes de Algoritmo de Proximidade
encontrarCriaturasProximaNumCriaturasInvalido()
  •	Objetivo: Testar casos extremos
  •	Cenário: Zero criaturas
  •	Expectativa: Retorna -1 (sem vizinhos)
  
encontrarCriaturassProximasValido()
  •	Objetivo: Verificar algoritmo de distância
  •	Cenário: 3 criaturas em posições conhecidas [0.5, 1.0, 0.6]
    •	Validações: 
      o	Criatura 0 - mais próxima é 2 (distância 0.1)
      o	Criatura 1 - mais próxima é 2 (distância 0.4)
      o	Criatura 2 - mais próxima é 0 (distância 0.1)
