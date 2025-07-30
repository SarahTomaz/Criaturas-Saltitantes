package org.example.boundaries;

import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioBoundaryTest {

    private UsuarioService usuarioService = new UsuarioService();

    @Test
    @DisplayName("Teste de fronteira - Login com tamanho mínimo (4 caracteres)")
    void autenticar_LoginTamanhoMinimo() {
        usuarioService.limparUsuarios(); // Limpar dados persistidos
        usuarioService.cadastrarUsuario("user", "senha123", "avatar.png");
        assertNotNull(usuarioService.autenticar("user", "senha123"));
    }

    @Test
    @DisplayName("Teste de fronteira - Senha com tamanho mínimo (4 caracteres)")
    void autenticar_SenhaTamanhoMinimo() {
        usuarioService.limparUsuarios(); // Limpar dados persistidos
        usuarioService.cadastrarUsuario("user2", "1234", "avatar.png");
        assertNotNull(usuarioService.autenticar("user2", "1234"));
    }

    @Test
    @DisplayName("Teste de fronteira - Login com tamanho máximo (20 caracteres)")
    void autenticar_LoginTamanhoMaximo() {
        usuarioService.limparUsuarios(); // Limpar dados persistidos
        String longLogin = "a".repeat(20);
        usuarioService.cadastrarUsuario(longLogin, "senha123", "avatar.png");
        assertNotNull(usuarioService.autenticar(longLogin, "senha123"));
    }

    @Test
    @DisplayName("Teste de fronteira - Login vazio deve falhar")
    void autenticar_LoginVazio() {
        usuarioService.limparUsuarios(); // Limpar dados persistidos
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.autenticar("", "senha123"));
    }

    @Test
    @DisplayName("Teste de fronteira - Senha vazia deve falhar")
    void autenticar_SenhaVazia() {
        usuarioService.limparUsuarios(); // Limpar dados persistidos
        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.autenticar("user3", ""));
    }
}
