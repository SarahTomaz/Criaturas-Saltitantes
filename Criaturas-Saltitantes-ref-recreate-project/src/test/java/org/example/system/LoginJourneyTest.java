package org.example.system;

import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginJourneyTest {

    @Test
    @DisplayName("User Journey - Cadastro e Login Bem-sucedido")
    void journeyCadastroELogin() {
        UsuarioService usuarioService = new UsuarioService();

        // 1. Cadastro
        boolean cadastroSucesso = usuarioService.cadastrarUsuario("journey_user1", "senha123", "avatar.png");
        assertTrue(cadastroSucesso);

        // 2. Tentativa de login com credenciais incorretas
        assertNull(usuarioService.autenticar("journey_user1", "senhaerrada"));

        // 3. Login bem-sucedido
        assertNotNull(usuarioService.autenticar("journey_user1", "senha123"));

        // 4. Tentativa de cadastro com login existente
        assertFalse(usuarioService.cadastrarUsuario("journey_user1", "outrasenha", "avatar2.png"));
    }
}
