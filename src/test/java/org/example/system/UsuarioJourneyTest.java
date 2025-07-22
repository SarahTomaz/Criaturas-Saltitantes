package org.example.system;

import java.util.List;

import org.example.model.Usuario;
import org.example.service.EstatisticasService;
import org.example.service.SimuladorService;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioJourneyTest {

    @Test
    @DisplayName("User Journey Completa - Cadastro, Login e Atividades")
    void userJourneyCompleta() {
        // 1. Cadastro do usuário
        UsuarioService usuarioService = new UsuarioService();
        boolean cadastroSucesso = usuarioService.cadastrarUsuario("journey_user", "senha123", "avatar.png");
        assertTrue(cadastroSucesso);

        // 2. Tentativa de login com credenciais incorretas
        assertNull(usuarioService.autenticar("journey_user", "senha_errada"));

        // 3. Login bem-sucedido
        Usuario usuario = usuarioService.autenticar("journey_user", "senha123");
        assertNotNull(usuario);

        // 4. Execução de simulação
        SimuladorService simuladorService = new SimuladorService();
        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);

        // 5. Verificação de estatísticas
        EstatisticasService estatisticasService = new EstatisticasService(usuarioService, simuladorService);
        String estatisticas = estatisticasService.getEstatisticasUsuario("journey_user");

        assertAll(
                () -> assertTrue(estatisticas.contains("journey_user")),
                () -> assertTrue(estatisticas.contains("Total de simulações: 1")),
                () -> assertTrue(usuario.getTotalSimulacoes() > 0)
        );

        // 6. Logout implícito ao fechar a aplicação
        // (testado indiretamente pela criação de novos serviços)
    }

    @Test
    @DisplayName("User Journey - Cadastro Duplicado Deve Falhar")
    void cadastroDuplicadoDeveFalhar() {
        UsuarioService usuarioService = new UsuarioService();

        // 1. Primeiro cadastro deve ter sucesso
        assertTrue(usuarioService.cadastrarUsuario("duplicate_user", "senha123", "avatar.png"));

        // 2. Segundo cadastro com mesmo login deve falhar
        assertFalse(usuarioService.cadastrarUsuario("duplicate_user", "outrasenha", "avatar2.png"));

        // 3. Login ainda funciona com credenciais originais
        assertNotNull(usuarioService.autenticar("duplicate_user", "senha123"));
    }

    @Test
    @DisplayName("User Journey - Múltiplas Simulações Acumulam Pontuação")
    void multiplasSimulacoesAcumulamPontuacao() {
        // 1. Configuração inicial
        UsuarioService usuarioService = new UsuarioService();
        usuarioService.cadastrarUsuario("multi_user", "senha123", "avatar.png");
        Usuario usuario = usuarioService.autenticar("multi_user", "senha123");
        SimuladorService simuladorService = new SimuladorService();

        int pontuacaoInicial = usuario.getPontuacao();

        // 2. Executar múltiplas simulações
        for (int i = 0; i < 3; i++) {
            simuladorService.executarSimulacaoCompleta(usuario, 5, 100);
        }

        // 3. Verificar acumulação
        assertTrue(usuario.getPontuacao() > pontuacaoInicial);
        assertEquals(3, usuario.getTotalSimulacoes());

        // 4. Verificar no ranking
        EstatisticasService estatisticasService = new EstatisticasService(usuarioService, simuladorService);
        List<Usuario> ranking = estatisticasService.getRankingUsuarios();
        assertTrue(ranking.stream().anyMatch(u -> u.getLogin().equals("multi_user")));
    }
}
