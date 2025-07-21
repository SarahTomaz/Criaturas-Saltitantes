package org.example.system;

import org.example.model.Usuario;
import org.example.service.SimuladorService;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Teste de user journey para o fluxo completo de simulação.
 *
 * <p>
 * Simula a jornada completa do usuário:</p>
 * <ol>
 * <li>Cadastro de usuário</li>
 * <li>Login</li>
 * <li>Configuração da simulação</li>
 * <li>Execução da simulação</li>
 * <li>Verificação dos resultados</li>
 * </ol>
 */
class SimulacaoJourneyTest {

    @Test
    @DisplayName("User Journey Completa - Cadastro, Login e Simulação Bem-sucedida")
    void userJourneyCompleta() {
        // 1. Cadastro
        UsuarioService usuarioService = new UsuarioService();
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
}

    // Outros testes de jornada do usuário podem ser adicionados aqui
