package org.example.domain;

import org.example.model.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.List;

/**
 * Testes de domínio para a classe Simulacao.
 *
 * <p>
 * Verifica as regras de negócio relacionadas a:</p>
 * <ul>
 * <li>Criação de simulações</li>
 * <li>Execução de iterações</li>
 * <li>Formação de clusters</li>
 * <li>Interação com o guardião</li>
 * <li>Critérios de sucesso</li>
 * </ul>
 */
class SimulacaoDomainTest {

    private Simulacao simulacao;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("test_user", "password123", "avatar.png");
        simulacao = new Simulacao(usuario, 5, 100);
    }

    @Test
    @DisplayName("Deve criar simulação com parâmetros válidos")
    void criarSimulacao_ParametrosValidos() {
        assertAll(
                () -> assertEquals(usuario, simulacao.getUsuario()),
                () -> assertEquals(5, simulacao.getCriaturas().size()),
                () -> assertEquals(0, simulacao.getIteracoes()),
                () -> assertEquals(100, simulacao.getMaxIteracoes()),
                () -> assertFalse(simulacao.isConcluida()),
                () -> assertNotNull(simulacao.getGuardiao())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    @DisplayName("Deve lançar exceção com número inválido de criaturas")
    void criarSimulacao_NumeroInvalidoCriaturas(int numCriaturas) {
        assertThrows(IllegalArgumentException.class,
                () -> new Simulacao(usuario, numCriaturas, 100));
    }

    @Test
    @DisplayName("Deve executar iteração corretamente")
    void executarIteracao() {
        boolean continuar = simulacao.executarIteracao();

        assertAll(
                () -> assertEquals(1, simulacao.getIteracoes()),
                () -> assertTrue(continuar),
                () -> assertFalse(simulacao.isConcluida())
        );
    }

    @Test
    @DisplayName("Deve finalizar ao atingir máximo de iterações")
    void finalizarAoAtingirMaxIteracoes() {
        for (int i = 0; i < 100; i++) {
            simulacao.executarIteracao();
        }

        assertAll(
                () -> assertEquals(100, simulacao.getIteracoes()),
                () -> assertTrue(simulacao.isConcluida()),
                () -> assertEquals("Simulação concluída - máximo de iterações atingido", simulacao.getStatus())
        );
    }

    @Test
    @DisplayName("Deve formar clusters quando criaturas estão próximas")
    void formarClusters() {
        // Posiciona todas as criaturas no mesmo lugar
        simulacao.getCriaturas().forEach(c -> c.setPosicao(50.0));

        simulacao.executarIteracao();

        assertFalse(simulacao.getClusters().isEmpty());
    }

    @Test
    @DisplayName("Deve ser bem-sucedida quando guardião coleta todas as moedas")
    void simulacaoBemSucedida() {
        // Configura todas as criaturas para perderem moedas para o guardião
        simulacao.getCriaturas().forEach(c -> {
            c.setPosicao(50.0);
            c.setMoedas(1000);
        });
        simulacao.getGuardiao().setPosicao(50.0);

        // Executa até concluir
        while (!simulacao.isConcluida()) {
            simulacao.executarIteracao();
        }

        assertTrue(simulacao.isBemSucedida());
    }

    @Test
    @DisplayName("Deve manter total de moedas constante")
    void conservacaoDeMoedas() {
        long totalInicial = calcularTotalMoedas(simulacao);

        for (int i = 0; i < 10; i++) {
            simulacao.executarIteracao();
            long totalAtual = calcularTotalMoedas(simulacao);
            assertEquals(totalInicial, totalAtual);
        }
    }

    private long calcularTotalMoedas(Simulacao sim) {
        return sim.getCriaturas().stream()
                .filter(Criatura::isAtiva)
                .mapToLong(Criatura::getMoedas)
                .sum()
                + sim.getClusters().stream()
                        .mapToLong(Cluster::getTotalMoedas)
                        .sum()
                + sim.getGuardiao().getMoedas();
    }

    @Test
    @DisplayName("Deve finalizar quando todas as criaturas são desativadas")
    void finalizarQuandoTodasCriaturasDesativadas() {
        // Desativa todas as criaturas
        simulacao.getCriaturas().forEach(c -> c.setAtiva(false));

        boolean continuar = simulacao.executarIteracao();

        assertAll(
                () -> assertFalse(continuar),
                () -> assertTrue(simulacao.isConcluida()),
                () -> assertEquals("Simulação concluída - todas criaturas desativadas", simulacao.getStatus())
        );
    }
}
