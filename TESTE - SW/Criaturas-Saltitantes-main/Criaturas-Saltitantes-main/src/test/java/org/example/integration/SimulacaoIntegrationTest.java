package org.example.integration;

import org.example.model.Simulacao;
import org.example.model.Usuario;
import org.example.service.SimuladorService;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        int totalSimulacoesInicial = usuario.getTotalSimulacoes();

        Simulacao resultado = simuladorService.executarSimulacaoCompleta(usuario, 5, 100);

        // Verificar se o total de simulações sempre aumenta
        assertTrue(usuario.getTotalSimulacoes() > totalSimulacoesInicial,
                "Total de simulações deve ter aumentado");

        // Se a simulação foi bem-sucedida, a pontuação deve ter aumentado
        if (resultado.isBemSucedida()) {
            assertTrue(usuario.getPontuacao() > pontuacaoInicial,
                    "Pontuação deve ter aumentado para simulação bem-sucedida");
        }
    }

    @Test
    @DisplayName("Integração Criatura-Cluster - Criaturas devem formar clusters quando próximas")
    void criaturasProximas_DevemFormarCluster() {
        simuladorService.criarNovaSimulacao(usuario, 5, 100);

        // Forçar posições exatamente iguais para garantir formação de cluster
        double posicaoComum = 50.0;
        simuladorService.getSimulacaoAtual().getCriaturas().forEach(c -> {
            c.setPosicao(posicaoComum);
        });

        // Verificar que não há clusters inicialmente
        assertTrue(simuladorService.getSimulacaoAtual().getClusters().isEmpty(),
                "Não deve haver clusters inicialmente");

        // Executar uma iteração para processar formação de clusters
        simuladorService.executarProximaIteracao();

        // Agora deve haver pelo menos um cluster
        assertTrue(!simuladorService.getSimulacaoAtual().getClusters().isEmpty(),
                "Criaturas na mesma posição devem formar clusters");
    }
}
