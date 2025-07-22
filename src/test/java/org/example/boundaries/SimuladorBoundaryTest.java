package org.example.boundaries;

import org.example.model.Usuario;
import org.example.service.SimuladorService;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimuladorBoundaryTest {

    private SimuladorService simuladorService = new SimuladorService();
    private Usuario usuario = new Usuario("teste", "senha", "avatar.png");

    @Test
    @DisplayName("Teste de fronteira - Número mínimo de criaturas (1)")
    void criarSimulacao_MinCriaturas() {
        assertNotNull(simuladorService.criarNovaSimulacao(usuario, 1, 100));
    }

    @Test
    @DisplayName("Teste de fronteira - Número máximo de criaturas (100)")
    void criarSimulacao_MaxCriaturas() {
        assertNotNull(simuladorService.criarNovaSimulacao(usuario, 100, 100));
    }

    @Test
    @DisplayName("Teste de fronteira - Número inválido de criaturas (0)")
    void criarSimulacao_CriaturasInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> simuladorService.criarNovaSimulacao(usuario, 0, 100));
    }

    @Test
    @DisplayName("Teste de fronteira - Iterações mínimas (1)")
    void criarSimulacao_MinIteracoes() {
        assertNotNull(simuladorService.criarNovaSimulacao(usuario, 5, 1));
    }

    @Test
    @DisplayName("Teste de fronteira - Iterações máximas (10000)")
    void criarSimulacao_MaxIteracoes() {
        assertNotNull(simuladorService.criarNovaSimulacao(usuario, 5, 10000));
    }
}
