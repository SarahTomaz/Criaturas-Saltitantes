package service;
import org.example.model.Usuario;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

import org.example.service.UsuarioService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

public class TestUsuarioService {
    public UsuarioService usuarioService;
    public static String tempFilePath;



    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
    }

    // Helper method to modify a `static final` field.
    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        // This line is often needed to remove the 'final' modifier at runtime.
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
        field.set(null, newValue);
    }

    @Test
    @DisplayName("Deve cadastrar um novo usuário com sucesso")
    void cadastrarUsuario_ComSucesso() {
        // Act
        boolean resultado = usuarioService.cadastrarUsuario("teste", "senha123", "avatar.png");

        // Assert
        assertTrue(resultado, "O cadastro deveria retornar true");
        assertEquals(1, usuarioService.getTotalUsuarios());

        // Verify persistence by creating a new service instance
        UsuarioService newService = new UsuarioService();
        assertEquals(1, newService.getTotalUsuarios());
        assertNotNull(newService.autenticar("teste", "senha123"));
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com login já existente")
    public void cadastrarUsuario_LoginJaExistente() {
        // Arrange
        usuarioService.cadastrarUsuario("existente", "senha123", "avatar.png");

        // Act
        boolean resultado = usuarioService.cadastrarUsuario("existente", "outrasenha", "outro.png");

        // Assert
        assertFalse(resultado, "Cadastro com login repetido deveria retornar false");
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("Deve usar avatar padrão se avatar for nulo ou vazio")
    void cadastrarUsuario_ComAvatarPadrao() {
        // Act
        usuarioService.cadastrarUsuario("usuario1", "senha123", null);
        usuarioService.cadastrarUsuario("usuario2", "senha123", "  ");

        // Assert
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        assertEquals("default.png", usuarios.get(0).getAvatar());
        assertEquals("default.png", usuarios.get(1).getAvatar());
    }

    @Test
    @DisplayName("Deve lançar exceção para login inválido")
    void cadastrarUsuario_LoginInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario(null, "senha123", "avatar.png");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("  ", "senha123", "avatar.png");
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para senha inválida")
    void cadastrarUsuario_SenhaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("teste", "123", "avatar.png");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("teste", null, "avatar.png");
        });
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais corretas")
    void autenticar_ComCredenciaisCorretas() {
        // Arrange
        usuarioService.cadastrarUsuario("user", "pass", "avatar.png");

        // Act
        Usuario usuario = usuarioService.autenticar("user", "pass");

        // Assert
        assertNotNull(usuario);
        assertEquals("user", usuario.getLogin());
        assertTrue(usuarioService.temUsuarioLogado());
        assertEquals(usuario, usuarioService.getUsuarioLogado());
    }

    @Test
    @DisplayName("Não deve autenticar com senha incorreta")
    void autenticar_ComSenhaIncorreta() {
        // Arrange
        usuarioService.cadastrarUsuario("user", "pass", "avatar.png");

        // Act
        Usuario usuario = usuarioService.autenticar("user", "wrongpass");

        // Assert
        assertNull(usuario);
        assertFalse(usuarioService.temUsuarioLogado());
    }

    @Test
    @DisplayName("Não deve autenticar usuário inexistente")
    void autenticar_UsuarioInexistente() {
        // Act
        Usuario usuario = usuarioService.autenticar("nonexistent", "pass");

        // Assert
        assertNull(usuario);
        assertFalse(usuarioService.temUsuarioLogado());
    }

    @Test
    @DisplayName("Deve fazer logout de usuário logado")
    void logout_DeveLimparUsuarioLogado() {
        // Arrange
        usuarioService.cadastrarUsuario("user", "pass", "avatar.png");
        usuarioService.autenticar("user", "pass");
        assertTrue(usuarioService.temUsuarioLogado());

        // Act
        usuarioService.logout();

        // Assert
        assertFalse(usuarioService.temUsuarioLogado());
        assertNull(usuarioService.getUsuarioLogado());
    }

    @Test
    @DisplayName("Deve remover usuário com sucesso")
    void removerUsuario_ComSucesso() {
        // Arrange
        usuarioService.cadastrarUsuario("aRemover", "senha", "avatar.png");
        assertEquals(1, usuarioService.getTotalUsuarios());

        // Act
        boolean resultado = usuarioService.removerUsuario("aRemover");

        // Assert
        assertTrue(resultado);
        assertEquals(0, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("Não deve remover usuário inexistente")
    void removerUsuario_Inexistente() {
        // Act
        boolean resultado = usuarioService.removerUsuario("inexistente");

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Não deve remover usuário logado")
    void removerUsuario_Logado() {
        // Arrange
        usuarioService.cadastrarUsuario("logado", "senha", "avatar.png");
        usuarioService.autenticar("logado", "senha");

        // Act
        boolean resultado = usuarioService.removerUsuario("logado");

        // Assert
        assertFalse(resultado);
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("Deve alterar o avatar de um usuário")
    void alterarAvatar_ComSucesso() {
        // Arrange
        String login = "user";
        String novoAvatar = "novo_avatar.jpg";
        usuarioService.cadastrarUsuario(login, "senha", "antigo.png");

        // Act
        boolean resultado = usuarioService.alterarAvatar(login, novoAvatar);

        // Assert
        assertTrue(resultado);

        // Verify by checking the user's data
        UsuarioService newService = new UsuarioService(); // Reloads from file
        Usuario usuario = newService.autenticar(login, "senha");
        assertNotNull(usuario);
        assertEquals(novoAvatar, usuario.getAvatar());
    }

    @Test
    @DisplayName("Não deve alterar o avatar de um usuário inexistente")
    void alterarAvatar_UsuarioInexistente() {
        // Act
        boolean resultado = usuarioService.alterarAvatar("inexistente", "novo_avatar.jpg");

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void listarUsuarios_DeveRetornarListaCorreta() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "s1", "a1.png");
        usuarioService.cadastrarUsuario("user2", "s2", "a2.png");

        // Act
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        // Assert
        assertEquals(2, usuarios.size());
        // Verify that the list contains the expected users (order is not guaranteed)
        assertTrue(usuarios.stream().anyMatch(u -> u.getLogin().equals("user1")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getLogin().equals("user2")));
    }

    @Test
    @DisplayName("A lista de usuários retornada deve ser uma cópia")
    void listarUsuarios_DeveRetornarCopia() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "s1", "a1.png");
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        // Act
        usuarios.add(new Usuario("temp", "temp", "temp.png"));

        // Assert
        assertEquals(1, usuarioService.getTotalUsuarios(), "A lista interna do serviço não deve ser modificada");
    }

    @Test
    @DisplayName("Deve atualizar os dados de um usuário")
    void atualizarUsuario_ComSucesso() {
        // Arrange
        String login = "user";
        usuarioService.cadastrarUsuario(login, "senha", "avatar.png");
        Usuario usuario = usuarioService.autenticar(login, "senha");
        assertNotNull(usuario);
        assertEquals(0, usuario.getTotalSimulacoes());

        // Act
        usuario.incrementarTotalSimulacoes(); // Simulate a change
        usuarioService.atualizarUsuario(usuario);

        // Assert by reloading
        UsuarioService newService = new UsuarioService();
        Usuario usuarioAtualizado = newService.autenticar(login, "senha");
        assertNotNull(usuarioAtualizado);
        assertEquals(1, usuarioAtualizado.getTotalSimulacoes());
    }

    @Test
    @DisplayName("Deve atualizar a pontuação com valor positivo")
    void atualizarPontuacao_ComValorPositivo() {
        // Arrange
        String login = "user";
        usuarioService.cadastrarUsuario(login, "senha", "avatar.png");

        // Act
        boolean resultado = usuarioService.atualizarPontuacao(login, 1);

        // Assert
        assertTrue(resultado);
        UsuarioService newService = new UsuarioService();
        Usuario usuario = newService.autenticar(login, "senha");
        assertNotNull(usuario);
        assertEquals(1, usuario.getTotalSimulacoes());
    }

    @Test
    @DisplayName("Deve atualizar a pontuação com valor zero ou negativo")
    void atualizarPontuacao_ComValorZero() {
        // Arrange
        String login = "user";
        usuarioService.cadastrarUsuario(login, "senha", "avatar.png");

        // Act
        boolean resultado = usuarioService.atualizarPontuacao(login, 0);

        // Assert
        assertTrue(resultado);
        UsuarioService newService = new UsuarioService();
        Usuario usuario = newService.autenticar(login, "senha");
        assertNotNull(usuario);
        assertEquals(1, usuario.getTotalSimulacoes());
    }
}