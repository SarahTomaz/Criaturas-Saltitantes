package org.example.system;

import org.example.model.Usuario;
import org.example.service.SimuladorService;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimulacaoJourneyTest {

    @Test
    @DisplayName("User Journey Completa - Cadastro, Login e Simulação Bem-sucedida")
    void userJourneyCompleta() {


        // 1. Cadastro
        UsuarioService usuarioService = new UsuarioService();
        usuarioService.removerUsuario("journey_user");
        boolean cadastroSucesso = usuarioService.cadastrarUsuario("journey_user", "senha123", "avatar.png");
        assertTrue(cadastroSucesso);

        // 2. Login
        Usuario usuario = usuarioService.autenticar("journey_user", "senha123");
        assertNotNull(usuario);

        // 3. Configuração e execução da simulação
        SimuladorService simuladorService = new SimuladorService();
        int numCriaturas = 5;
        int maxIteracoes = 100;

        // 4. Execução completa
        simuladorService.executarSimulacaoCompleta(usuario, numCriaturas, maxIteracoes);

        // 5. Verificação dos resultados
        assertTrue(usuario.getTotalSimulacoes() > 0);
        assertTrue(usuario.getPontuacao() >= 0);
    }

    @Test
    @DisplayName("User Journey - Simulação com Parâmetros Diferentes")
    void userJourneyParametrosDiferentes() {
        UsuarioService usuarioService = new UsuarioService();
        usuarioService.cadastrarUsuario("journey_user2", "senha123", "avatar.png");
        Usuario usuario = usuarioService.autenticar("journey_user2", "senha123");

        SimuladorService simuladorService = new SimuladorService();

        // Testar com diferentes números de criaturas
        for (int i = 1; i <= 5; i++) {
            simuladorService.executarSimulacaoCompleta(usuario, i, 50);
            assertTrue(simuladorService.getHistoricoSimulacoes().size() >= i);
        }
    }
}
