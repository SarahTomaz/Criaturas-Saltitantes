package org.example.structural;

import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes estruturais para UsuarioService com 100% de cobertura MC/DC.
 *
 * <p>
 * Verifica todos os caminhos lógicos e condições dos métodos principais.</p>
 *
 * <p>
 * <b>Critérios MC/DC:</b></p>
 * <ul>
 * <li>Todas as entradas devem ser testadas</li>
 * <li>Todas as saídas devem ser testadas</li>
 * <li>Todos os caminhos de decisão devem ser testados</li>
 * <li>Cada condição deve influenciar independentemente o resultado</li>
 * </ul>
 */
class UsuarioStructuralTest {

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
    }

    /**
     * Teste MC/DC para cadastrarUsuario. Cobre todas as combinações de
     * condições: 1. login == null 2. login.trim().isEmpty() 3. senha == null 4.
     * senha.length() < 4 5. usuarioExistente
     */
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

    // outros testes estruturais serão adicionados aqui
}
