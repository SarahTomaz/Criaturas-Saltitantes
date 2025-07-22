package org.example.structural;

import java.util.List;

import org.example.model.Simulacao;
import org.example.model.Usuario;
import org.example.service.SimuladorService;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testes estruturais para SimuladorService com 100% de cobertura MC/DC.
 *
 * <p>
 * Verifica todos os caminhos lógicos e condições dos métodos principais:</p>
 * <ul>
 * <li>criarNovaSimulacao</li>
 * <li>executarProximaIteracao</li>
 * <li>executarSimulacaoCompleta</li>
 * <li>obterEstadoAtual</li>
 * </ul>
 *
 * <p>
 * <b>Critérios MC/DC:</b></p>
 * <ul>
 * <li>Todas as entradas devem ser testadas</li>
 * <li>Todas as saídas devem ser testadas</li>
 * <li>Todos os caminhos de decisão devem ser testados</li>
 * <li>Cada condição deve influenciar independentemente o resultado</li>
 * </ul>
 */
class SimuladorStructuralTest {

    private SimuladorService simuladorService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        simuladorService = new SimuladorService();
        usuario = new Usuario("test_user", "password123", "avatar.png");
    }

    // ========== TESTES PARA criarNovaSimulacao ==========
    @Test
    @DisplayName("MC/DC criarNovaSimulacao - Condições válidas")
    void criarNovaSimulacao_CondicoesValidas() {
        assertDoesNotThrow(() -> simuladorService.criarNovaSimulacao(usuario, 5, 100));
        assertTrue(simuladorService.temSimulacaoAtiva());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 101})
    @DisplayName("MC/DC criarNovaSimulacao - Número inválido de criaturas (condição 1)")
    void criarNovaSimulacao_NumCriaturasInvalido(int numCriaturas) {
        assertThrows(IllegalArgumentException.class,
                () -> simuladorService.criarNovaSimulacao(usuario, numCriaturas, 100));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 10001})
    @DisplayName("MC/DC criarNovaSimulacao - Número inválido de iterações (condição 2)")
    void criarNovaSimulacao_MaxIteracoesInvalido(int maxIteracoes) {
        assertThrows(IllegalArgumentException.class,
                () -> simuladorService.criarNovaSimulacao(usuario, 5, maxIteracoes));
    }

    @Test
    @DisplayName("MC/DC criarNovaSimulacao - Usuário nulo (condição 3)")
    void criarNovaSimulacao_UsuarioNulo() {
        assertThrows(NullPointerException.class,
                () -> simuladorService.criarNovaSimulacao(null, 5, 100));
    }

    // ========== TESTES PARA executarProximaIteracao ==========
    @Test
    @DisplayName("MC/DC executarProximaIteracao - Sem simulação ativa (condição 1)")
    void executarProximaIteracao_SemSimulacaoAtiva() {
        assertThrows(IllegalStateException.class,
                () -> simuladorService.executarProximaIteracao());
    }

    @Test
    @DisplayName("MC/DC executarProximaIteracao - Simulação deve continuar")
    void executarProximaIteracao_DeveContinuar() {
        simuladorService.criarNovaSimulacao(usuario, 5, 100);
        assertTrue(simuladorService.executarProximaIteracao());
    }

    @Test
    @DisplayName("MC/DC executarProximaIteracao - Simulação deve terminar")
    void executarProximaIteracao_DeveTerminar() {
        simuladorService.criarNovaSimulacao(usuario, 1, 1);
        assertFalse(simuladorService.executarProximaIteracao());
        assertFalse(simuladorService.temSimulacaoAtiva());
    }

    // ========== TESTES PARA executarSimulacaoCompleta ==========
    @Test
    @DisplayName("MC/DC executarSimulacaoCompleta - Execução bem-sucedida")
    void executarSimulacaoCompleta_Sucesso() {
        assertDoesNotThrow(() -> simuladorService.executarSimulacaoCompleta(usuario, 5, 100));
        assertEquals(1, simuladorService.getHistoricoSimulacoes().size());
    }

    @Test
    @DisplayName("MC/DC executarSimulacaoCompleta - Adiciona ao histórico mesmo quando falha")
    void executarSimulacaoCompleta_AdicionaAoHistoricoQuandoFalha() {
        simuladorService.executarSimulacaoCompleta(usuario, 1, 1); // Simulação rápida que pode falhar
        assertEquals(1, simuladorService.getHistoricoSimulacoes().size());
    }

    // ========== TESTES PARA obterEstadoAtual ==========
    @Test
    @DisplayName("MC/DC obterEstadoAtual - Sem simulação ativa")
    void obterEstadoAtual_SemSimulacaoAtiva() {
        assertEquals("Nenhuma simulação ativa", simuladorService.obterEstadoAtual());
    }

    @Test
    @DisplayName("MC/DC obterEstadoAtual - Com simulação ativa")
    void obterEstadoAtual_ComSimulacaoAtiva() {
        simuladorService.criarNovaSimulacao(usuario, 5, 100);
        String estado = simuladorService.obterEstadoAtual();

        assertAll(
                () -> assertTrue(estado.contains("ESTADO DA SIMULAÇÃO")),
                () -> assertTrue(estado.contains("Criaturas Ativas")),
                () -> assertTrue(estado.contains("Guardião"))
        );
    }

    // ========== TESTES PARA temSimulacaoAtiva ==========
    @Test
    @DisplayName("MC/DC temSimulacaoAtiva - Todos os caminhos")
    void temSimulacaoAtiva_Caminhos() {
        assertFalse(simuladorService.temSimulacaoAtiva());

        simuladorService.criarNovaSimulacao(usuario, 5, 100);
        assertTrue(simuladorService.temSimulacaoAtiva());

        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);
        assertFalse(simuladorService.temSimulacaoAtiva());
    }

    // ========== TESTES PARA getHistoricoSimulacoes ==========
    @Test
    @DisplayName("MC/DC getHistoricoSimulacoes - Proteção contra modificação externa")
    void getHistoricoSimulacoes_ProtecaoContraModificacao() {
        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);

        List<Simulacao> historico = simuladorService.getHistoricoSimulacoes();
        assertThrows(UnsupportedOperationException.class,
                () -> historico.add(null));
    }

    // ========== TESTES PARA getTaxaSucessoGeral ==========
    @Test
    @DisplayName("MC/DC getTaxaSucessoGeral - Com histórico vazio")
    void getTaxaSucessoGeral_HistoricoVazio() {
        assertEquals(0.0, simuladorService.getTaxaSucessoGeral());
    }

    @Test
    @DisplayName("MC/DC getTaxaSucessoGeral - Com histórico")
    void getTaxaSucessoGeral_ComHistorico() {
        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);
        double taxa = simuladorService.getTaxaSucessoGeral();

        assertTrue(taxa >= 0.0 && taxa <= 1.0);
    }

    // ========== TESTES DE COMBINAÇÃO DE CONDIÇÕES ==========
    @Test
    @DisplayName("MC/DC Combinação - Simulação com múltiplas condições")
    void combinacaoMultiplasCondicoes() {
        // Cria simulação válida
        simuladorService.criarNovaSimulacao(usuario, 5, 100);

        // Executa até a metade
        for (int i = 0; i < 50; i++) {
            simuladorService.executarProximaIteracao();
        }

        // Verifica estado intermediário
        assertAll(
                () -> assertTrue(simuladorService.temSimulacaoAtiva()),
                () -> assertFalse(simuladorService.getSimulacaoAtual().isConcluida()),
                () -> assertTrue(simuladorService.obterEstadoAtual().contains("Iteração"))
        );

        // Completa a simulação
        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);

        // Verifica estado final
        assertAll(
                () -> assertFalse(simuladorService.temSimulacaoAtiva()),
                () -> assertEquals(2, simuladorService.getHistoricoSimulacoes().size()),
                () -> assertTrue(simuladorService.getTaxaSucessoGeral() >= 0)
        );
    }
}
