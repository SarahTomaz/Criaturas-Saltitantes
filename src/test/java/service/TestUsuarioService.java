package service;

import org.example.model.Usuario;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.example.service.UsuarioService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

public class TestUsuarioService {
    public UsuarioService usuarioService;
    public static String tempFilePath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        File tempFile = tempDir.resolve("test_usuarios.ser").toFile();
        tempFilePath = tempFile.getAbsolutePath();
        UsuarioService.setArquivoUsuarios(tempFilePath);
        usuarioService = new UsuarioService();
    }

    @AfterEach
    void tearDown() {
        usuarioService = null;
    }

    @AfterAll
    static void tearDownAll() {
        File file = new File(tempFilePath);
        file.delete();
    }


    @Test
    @DisplayName("Deve cadastrar um novo usuário com sucesso")
    void cadastrarUsuario_ComSucesso() {

        boolean resultado = usuarioService.cadastrarUsuario("teste", "senha123", "avatar.png");


        assertTrue(resultado, "O cadastro deveria retornar true");
        assertEquals(1, usuarioService.getTotalUsuarios());

        UsuarioService newService = new UsuarioService();
        assertEquals(1, newService.getTotalUsuarios());
        assertNotNull(newService.autenticar("teste", "senha123"));
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com login já existente")
    public void cadastrarUsuario_LoginJaExistente() {

        usuarioService.cadastrarUsuario("existente", "senha123", "avatar.png");


        boolean resultado = usuarioService.cadastrarUsuario("existente", "outrasenha", "outro.png");


        assertFalse(resultado, "Cadastro com login repetido deveria retornar false");
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("Deve usar avatar padrão se avatar for nulo ou vazio")
    void cadastrarUsuario_ComAvatarPadrao() {

        usuarioService.cadastrarUsuario("usuario1", "senha123", null);
        usuarioService.cadastrarUsuario("usuario2", "senha123", "  ");


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

        usuarioService.cadastrarUsuario("user", "pass", "avatar.png");


        Usuario usuario = usuarioService.autenticar("user", "pass");


        assertNotNull(usuario);
        assertEquals("user", usuario.getLogin());
        assertTrue(usuarioService.temUsuarioLogado());
        assertEquals(usuario, usuarioService.getUsuarioLogado());
    }

    @Test
    @DisplayName("Não deve autenticar com senha incorreta")
    void autenticar_ComSenhaIncorreta() {

        usuarioService.cadastrarUsuario("user", "pass", "avatar.png");


        Usuario usuario = usuarioService.autenticar("user", "wrongpass");


        assertNull(usuario);
        assertFalse(usuarioService.temUsuarioLogado());
    }

    @Test
    @DisplayName("Não deve autenticar usuário inexistente")
    void autenticar_UsuarioInexistente() {

        Usuario usuario = usuarioService.autenticar("nonexistent", "pass");


        assertNull(usuario);
        assertFalse(usuarioService.temUsuarioLogado());
    }

    @Test
    @DisplayName("Deve fazer logout de usuário logado")
    void logout_DeveLimparUsuarioLogado() {

        usuarioService.cadastrarUsuario("user", "pass", "avatar.png");
        usuarioService.autenticar("user", "pass");
        assertTrue(usuarioService.temUsuarioLogado());


        usuarioService.logout();


        assertFalse(usuarioService.temUsuarioLogado());
        assertNull(usuarioService.getUsuarioLogado());
    }

    @Test
    @DisplayName("Deve remover usuário com sucesso")
    void removerUsuario_ComSucesso() {

        usuarioService.cadastrarUsuario("aRemover", "senha", "avatar.png");
        assertEquals(1, usuarioService.getTotalUsuarios());


        boolean resultado = usuarioService.removerUsuario("aRemover");


        assertTrue(resultado);
        assertEquals(0, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("Não deve remover usuário inexistente")
    void removerUsuario_Inexistente() {

        boolean resultado = usuarioService.removerUsuario("inexistente");


        assertFalse(resultado);
    }

    @Test
    @DisplayName("Não deve remover usuário logado")
    void removerUsuario_Logado() {

        usuarioService.cadastrarUsuario("logado", "senha", "avatar.png");
        usuarioService.autenticar("logado", "senha");


        boolean resultado = usuarioService.removerUsuario("logado");


        assertFalse(resultado);
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("Deve alterar o avatar de um usuário")
    void alterarAvatar_ComSucesso() {

        String login = "user";
        String novoAvatar = "novo_avatar.jpg";
        usuarioService.cadastrarUsuario(login, "senha", "antigo.png");


        boolean resultado = usuarioService.alterarAvatar(login, novoAvatar);


        assertTrue(resultado);


        UsuarioService newService = new UsuarioService();
        Usuario usuario = newService.autenticar(login, "senha");
        assertNotNull(usuario);
        assertEquals(novoAvatar, usuario.getAvatar());
    }

    @Test
    @DisplayName("Não deve alterar o avatar de um usuário inexistente")
    void alterarAvatar_UsuarioInexistente() {

        boolean resultado = usuarioService.alterarAvatar("inexistente", "novo_avatar.jpg");


        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void listarUsuarios_DeveRetornarListaCorreta() {

        usuarioService.cadastrarUsuario("user1", "s1aaa", "a1.png");
        usuarioService.cadastrarUsuario("user2", "s2aaa", "a2.png");


        List<Usuario> usuarios = usuarioService.listarUsuarios();


        assertEquals(2, usuarios.size());

        assertTrue(usuarios.stream().anyMatch(u -> u.getLogin().equals("user1")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getLogin().equals("user2")));
    }

    @Test
    @DisplayName("A lista de usuários retornada deve ser uma cópia")
    void listarUsuarios_DeveRetornarCopia() {

        usuarioService.cadastrarUsuario("user1", "s1aaa", "a1.png");
        List<Usuario> usuarios = usuarioService.listarUsuarios();


        usuarios.add(new Usuario("temp", "temp", "temp.png"));


        assertEquals(1, usuarioService.getTotalUsuarios(), "A lista interna do serviço não deve ser modificada");
    }

    @Test
    @DisplayName("Deve atualizar os dados de um usuário")
    void atualizarUsuario_ComSucesso() {

        String login = "user";
        usuarioService.cadastrarUsuario(login, "senha", "avatar.png");
        Usuario usuario = usuarioService.autenticar(login, "senha");
        assertNotNull(usuario);
        assertEquals(0, usuario.getTotalSimulacoes());


        usuario.incrementarTotalSimulacoes();
        usuarioService.atualizarUsuario(usuario);
        UsuarioService newService = new UsuarioService();
        Usuario usuarioAtualizado = newService.autenticar(login, "senha");
        assertNotNull(usuarioAtualizado);
        assertEquals(1, usuarioAtualizado.getTotalSimulacoes());
    }

    @Test
    @DisplayName("Deve atualizar a pontuação com valor positivo")
    void atualizarPontuacao_ComValorPositivo() {

        String login = "user";
        usuarioService.cadastrarUsuario(login, "senha", "avatar.png");


        boolean resultado = usuarioService.atualizarPontuacao(login, 1);


        assertTrue(resultado);
        UsuarioService newService = new UsuarioService();
        Usuario usuario = newService.autenticar(login, "senha");
        assertNotNull(usuario);
        assertEquals(1, usuario.getTotalSimulacoes());
    }

    @Test
    @DisplayName("Deve atualizar a pontuação com valor zero ou negativo")
    void atualizarPontuacao_ComValorZero() {

        String login = "user";
        usuarioService.cadastrarUsuario(login, "senha", "avatar.png");


        boolean resultado = usuarioService.atualizarPontuacao(login, 0);


        assertTrue(resultado);
        UsuarioService newService = new UsuarioService();
        Usuario usuario = newService.autenticar(login, "senha");
        assertNotNull(usuario);
        assertEquals(1, usuario.getTotalSimulacoes());
    }
}