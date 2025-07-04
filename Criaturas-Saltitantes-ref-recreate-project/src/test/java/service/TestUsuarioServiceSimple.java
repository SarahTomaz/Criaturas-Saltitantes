package service;

import java.util.List;
import org.example.model.Usuario;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestUsuarioService {

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
    }

    @Test
    @DisplayName("Cadastrar usuário com dados válidos")
    void cadastrarUsuario_DadosValidos() {
        // Act
        boolean resultado = usuarioService.cadastrarUsuario("testUser", "password123", "avatar.png");
        
        // Assert
        assertTrue(resultado);
        Usuario usuario = usuarioService.autenticar("testUser", "password123");
        assertNotNull(usuario);
        assertEquals("testUser", usuario.getLogin());
        assertEquals("avatar.png", usuario.getAvatar());
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com login já existente")
    void cadastrarUsuario_LoginJaExistente() {
        // Arrange
        usuarioService.cadastrarUsuario("testUser", "password123", "avatar.png");
        
        // Act
        boolean resultado = usuarioService.cadastrarUsuario("testUser", "newPassword", "newAvatar.png");
        
        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com senha inválida")
    void cadastrarUsuario_SenhaInvalida() {
        // Act & Assert
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("teste", "123", "avatar.png");
        });
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("teste", "", "avatar.png");
        });
        
        assertNotNull(exception1);
        assertNotNull(exception2);
    }

    @Test
    @DisplayName("Autenticar com credenciais corretas")
    void autenticar_ComCredenciaisCorretas() {
        // Arrange
        usuarioService.cadastrarUsuario("testUser", "password123", "avatar.png");
        
        // Act
        Usuario usuario = usuarioService.autenticar("testUser", "password123");
        
        // Assert
        assertNotNull(usuario);
        assertEquals("testUser", usuario.getLogin());
    }

    @Test
    @DisplayName("Falhar autenticação com credenciais incorretas")
    void autenticar_ComCredenciaisIncorretas() {
        // Arrange
        usuarioService.cadastrarUsuario("testUser", "password123", "avatar.png");
        
        // Act & Assert
        assertNull(usuarioService.autenticar("testUser", "wrongPassword"));
        assertNull(usuarioService.autenticar("wrongUser", "password123"));
    }

    @Test
    @DisplayName("Listar usuários")
    void listarUsuarios() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "password123", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "password456", "avatar2.png");
        
        // Act
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        
        // Assert
        assertNotNull(usuarios);
        assertTrue(usuarios.size() >= 2);
        assertTrue(usuarios.stream().anyMatch(u -> u.getLogin().equals("user1")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getLogin().equals("user2")));
    }

    @Test
    @DisplayName("Atualizar usuário mantendo referência correta")
    void atualizarUsuario_ManterReferenciaCorreta() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "password123", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "password456", "avatar2.png");
        
        Usuario usuarioOriginal = usuarioService.autenticar("user1", "password123");
        assertNotNull(usuarioOriginal);
        
        // Act
        usuarioOriginal.setAvatar("novoAvatar.png");
        usuarioService.atualizarUsuario(usuarioOriginal);
        
        // Assert
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        assertTrue(usuarios.size() >= 2);

        Usuario user1Atualizado = usuarios.stream()
                .filter(u -> u.getLogin().equals("user1"))
                .findFirst()
                .orElse(null);

        assertNotNull(user1Atualizado);
        assertEquals("novoAvatar.png", user1Atualizado.getAvatar());
        
        // Verificar que user2 não foi afetado
        Usuario user2 = usuarios.stream()
                .filter(u -> u.getLogin().equals("user2"))
                .findFirst()
                .orElse(null);
        
        assertNotNull(user2);
        assertEquals("avatar2.png", user2.getAvatar());
    }

    @Test
    @DisplayName("Remover usuário")
    void removerUsuario() {
        // Arrange
        usuarioService.cadastrarUsuario("userToRemove", "password123", "avatar.png");
        usuarioService.cadastrarUsuario("userToKeep", "password456", "avatar2.png");
        
        // Act
        boolean resultado = usuarioService.removerUsuario("userToRemove");
        
        // Assert
        assertTrue(resultado);
        assertNull(usuarioService.autenticar("userToRemove", "password123"));
        assertNotNull(usuarioService.autenticar("userToKeep", "password456"));
    }

    @Test
    @DisplayName("Falhar ao remover usuário inexistente")
    void removerUsuario_UsuarioInexistente() {
        // Act
        boolean resultado = usuarioService.removerUsuario("inexistente");
        
        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Verificar gerenciamento de usuário logado")
    void gerenciarUsuarioLogado() {
        // Arrange
        usuarioService.cadastrarUsuario("testUser", "password123", "avatar.png");
        
        // Act & Assert - Login através de autenticação
        Usuario usuario = usuarioService.autenticar("testUser", "password123");
        assertEquals(usuario, usuarioService.getUsuarioLogado());
        
        // Act & Assert - Logout
        usuarioService.logout();
        assertNull(usuarioService.getUsuarioLogado());
    }
}
