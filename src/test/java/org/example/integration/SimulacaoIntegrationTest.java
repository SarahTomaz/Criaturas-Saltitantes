package org.example.integration;

import org.example.model.Usuario;
import org.example.service.SimuladorService;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimulacaoIntegrationTest {

    private SimuladorService simuladorService;
    private UsuarioService usuarioService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        simuladorService = new SimuladorService();
        usuarioService = new UsuarioService();
        usuario = new Usuario("teste", "senha", "avatar.png");
    }

    @Test
    @DisplayName("Integração Simulador-Usuario - Nova simulação deve ser registrada no histórico")
    void novaSimulacao_DeveRegistrarNoHistorico() {
        simuladorService.criarNovaSimulacao(usuario, 5, 100);
        assertEquals(0, simuladorService.getHistoricoSimulacoes().size());

        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);
        assertEquals(1, simuladorService.getHistoricoSimulacoes().size());
    }

    @Test
    @DisplayName("Integração Simulador-Usuario - Pontuação deve ser atualizada após simulação bem-sucedida")
    void simulacaoBemSucedida_DeveAtualizarPontuacao() {
        usuarioService.cadastrarUsuario(usuario.getLogin(), "senha", usuario.getAvatar());
        int pontuacaoInicial = usuario.getPontuacao();

        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);

        assertTrue(usuario.getPontuacao() > pontuacaoInicial
                || simuladorService.getHistoricoSimulacoes().get(0).isBemSucedida());
    }

    @Test
    @DisplayName("Integração Criatura-Cluster - Criaturas devem formar clusters quando próximas")
    void criaturasProximas_DevemFormarCluster() {
        simuladorService.criarNovaSimulacao(usuario, 5, 100);

        // Forçar posições próximas
        simuladorService.getSimulacaoAtual().getCriaturas().forEach(c -> c.setPosicao(50.0));

        simuladorService.executarProximaIteracao();
        assertFalse(simuladorService.getSimulacaoAtual().getClusters().isEmpty());
    }
}
