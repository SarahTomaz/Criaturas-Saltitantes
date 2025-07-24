package org.example.integration;

import org.example.model.Usuario;
import org.example.service.EstatisticasService;
import org.example.service.SimuladorService;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioIntegrationTest {

    private UsuarioService usuarioService;
    private EstatisticasService estatisticasService;
    private SimuladorService simuladorService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
        simuladorService = new SimuladorService();
        estatisticasService = new EstatisticasService(usuarioService, simuladorService);
    }

    @Test
    @DisplayName("Integração Usuario-Simulador - Pontuação deve ser atualizada após simulação")
    void pontuacaoAtualizadaAposSimulacao() {
        // 1. Cadastrar usuário
        usuarioService.cadastrarUsuario("test_user", "senha123", "avatar.png");
        Usuario usuario = usuarioService.autenticar("test_user", "senha123");

        int pontuacaoInicial = usuario.getPontuacao();

        // 2. Executar simulação
        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);

        // 3. Verificar se a pontuação foi atualizada
        assertNotEquals(pontuacaoInicial, usuario.getPontuacao());
    }

    @Test
    @DisplayName("Integração Usuario-Estatisticas - Estatísticas devem refletir atividades do usuário")
    void estatisticasRefletemAtividadesUsuario() {
        // 1. Cadastrar usuário
        usuarioService.cadastrarUsuario("stats_user", "senha123", "avatar.png");
        Usuario usuario = usuarioService.autenticar("stats_user", "senha123");

        // 2. Executar simulação
        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);

        // 3. Verificar estatísticas
        String estatisticas = estatisticasService.getEstatisticasUsuario("stats_user");

        assertAll(
                () -> assertTrue(estatisticas.contains("Total de simulações: 1")),
                () -> assertTrue(estatisticas.contains("Posição no ranking"))
        );
    }

    @Test
    @DisplayName("Integração Completa - Fluxo de cadastro, login e simulação")
    void fluxoCompletoCadastroLoginSimulacao() {
        // 1. Cadastrar novo usuário
        usuarioService.removerUsuario("full_user");
        boolean cadastrado = usuarioService.cadastrarUsuario("full_user", "senha123", "avatar.png");
        assertTrue(cadastrado);

        // 2. Autenticar usuário
        Usuario usuario = usuarioService.autenticar("full_user", "senha123");
        assertNotNull(usuario);

        // 3. Executar simulação
        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);

        // 4. Verificar resultados
        assertTrue(usuario.getTotalSimulacoes() > 0);

        // 5. Verificar estatísticas
        String relatorio = estatisticasService.gerarRelatorioCompleto();
        assertTrue(relatorio.contains("full_user"));
    }
}
