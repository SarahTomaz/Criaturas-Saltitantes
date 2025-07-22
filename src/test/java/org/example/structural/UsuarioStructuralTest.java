package org.example.structural;

import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioStructuralTest {

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
    }

    @Test
    @DisplayName("MC/DC para cadastrarUsuario - Todas combinações de condições")
    void cadastrarUsuario_MCDC() {
        // Caso 1: login == null
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrarUsuario(null, "senha123", "avatar.png"));

        // Caso 2: login vazio
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrarUsuario("   ", "senha123", "avatar.png"));

        // Caso 3: senha == null
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrarUsuario("user1", null, "avatar.png"));

        // Caso 4: senha curta
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrarUsuario("user1", "123", "avatar.png"));

        // Caso 5: usuário já existe (condição independente)
        usuarioService.cadastrarUsuario("user1", "senha123", "avatar.png");
        assertFalse(usuarioService.cadastrarUsuario("user1", "outrasenha", "avatar2.png"));
    }

    @Test
    @DisplayName("Cobertura de caminhos para autenticar")
    void autenticar_CoberturaCaminhos() {
        // Caminho 1: login inválido
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.autenticar(null, "senha"));

        // Caminho 2: senha inválida
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.autenticar("user", null));

        // Caminho 3: usuário não existe
        assertNull(usuarioService.autenticar("inexistente", "senha"));

        // Caminho 4: senha incorreta
        usuarioService.cadastrarUsuario("user2", "senha123", "avatar.png");
        assertNull(usuarioService.autenticar("user2", "senhaerrada"));

        // Caminho 5: autenticação bem-sucedida
        assertNotNull(usuarioService.autenticar("user2", "senha123"));
    }

    @Test
    @DisplayName("Cobertura de condições para removerUsuario")
    void removerUsuario_CoberturaCondicoes() {
        // Condição 1: login inválido
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.removerUsuario(null));

        // Condição 2: usuário não existe
        assertFalse(usuarioService.removerUsuario("inexistente"));

        // Condição 3: usuário existe
        usuarioService.cadastrarUsuario("user3", "senha123", "avatar.png");
        assertTrue(usuarioService.removerUsuario("user3"));
    }
}
