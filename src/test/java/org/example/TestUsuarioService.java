package service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TestUsuarioService {

    private UsuarioService usuarioService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
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

    // ==================== TESTES DE FRONTEIRA ====================
    @Test
    @DisplayName("Deve aceitar senha com exatamente 4 caracteres (fronteira mínima)")
    void cadastrarUsuario_SenhaLimiteMinimoExato() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            usuarioService.cadastrarUsuario("user", "1234", "avatar.png");
        });
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("Deve rejeitar senha com 3 caracteres (abaixo da fronteira)")
    void cadastrarUsuario_SenhaAbaixoLimiteMinimo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("user", "123", "avatar.png");
        });
        assertTrue(exception.getMessage().contains("pelo menos 4 caracteres"));
    }

    @Test
    @DisplayName("Deve aceitar login com caracteres especiais")
    void cadastrarUsuario_LoginComCaracteresEspeciais() {
        // Act
        boolean resultado = usuarioService.cadastrarUsuario("user@123_test", "senha123", "avatar.png");

        // Assert
        assertTrue(resultado);
        Usuario usuario = usuarioService.autenticar("user@123_test", "senha123");
        assertNotNull(usuario);
    }

    @Test
    @DisplayName("Deve lidar com avatar muito longo")
    void cadastrarUsuario_AvatarMuitoLongo() {
        // Arrange
        String avatarLongo = "a".repeat(1000) + ".png";

        // Act
        boolean resultado = usuarioService.cadastrarUsuario("user", "senha123", avatarLongo);

        // Assert
        assertTrue(resultado);
        Usuario usuario = usuarioService.autenticar("user", "senha123");
        assertEquals(avatarLongo, usuario.getAvatar());
    }

    @Test
    @DisplayName("Deve lidar com login muito longo")
    void cadastrarUsuario_LoginMuitoLongo() {
        // Arrange
        String loginLongo = "u".repeat(1000);

        // Act
        boolean resultado = usuarioService.cadastrarUsuario(loginLongo, "senha123", "avatar.png");

        // Assert
        assertTrue(resultado);
        Usuario usuario = usuarioService.autenticar(loginLongo, "senha123");
        assertNotNull(usuario);
    }

    // ==================== TESTES ESTRUTURAIS/MC/DC ====================
    @Test
    @DisplayName("Deve retornar null quando login E senha são null (MC/DC)")
    void autenticar_LoginESenhaNulos_MCDC() {
        // Act
        Usuario resultado = usuarioService.autenticar(null, null);

        // Assert
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve retornar null quando apenas login é null (MC/DC)")
    void autenticar_ApenasLoginNulo_MCDC() {
        // Act
        Usuario resultado = usuarioService.autenticar(null, "senha");

        // Assert
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve retornar null quando apenas senha é null (MC/DC)")
    void autenticar_ApenasSenhaNula_MCDC() {
        // Act
        Usuario resultado = usuarioService.autenticar("login", null);

        // Assert
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve buscar usuário ignorando case sensitivity")
    void buscarUsuario_CaseSensitivity() {
        // Arrange
        usuarioService.cadastrarUsuario("TestUser", "senha123", "avatar.png");

        // Act & Assert
        Usuario usuario1 = usuarioService.autenticar("TestUser", "senha123");
        Usuario usuario2 = usuarioService.autenticar("testuser", "senha123");
        Usuario usuario3 = usuarioService.autenticar("TESTUSER", "senha123");

        assertNotNull(usuario1);
        assertNotNull(usuario2);
        assertNotNull(usuario3);
        assertEquals("TestUser", usuario1.getLogin());
        assertEquals("TestUser", usuario2.getLogin());
        assertEquals("TestUser", usuario3.getLogin());
    }

    @Test
    @DisplayName("Deve alterar avatar com valor null")
    void alterarAvatar_ComValorNull() {
        // Arrange
        usuarioService.cadastrarUsuario("user", "senha123", "avatar.png");

        // Act
        boolean resultado = usuarioService.alterarAvatar("user", null);

        // Assert
        assertTrue(resultado);
        UsuarioService newService = new UsuarioService();
        Usuario usuario = newService.autenticar("user", "senha123");
        assertNull(usuario.getAvatar());
    }

    @Test
    @DisplayName("Deve alterar avatar com string vazia")
    void alterarAvatar_ComStringVazia() {
        // Arrange
        usuarioService.cadastrarUsuario("user", "senha123", "avatar.png");

        // Act
        boolean resultado = usuarioService.alterarAvatar("user", "");

        // Assert
        assertTrue(resultado);
        UsuarioService newService = new UsuarioService();
        Usuario usuario = newService.autenticar("user", "senha123");
        assertEquals("", usuario.getAvatar());
    }

    // ==================== TESTES DE PERSISTÊNCIA COM ARQUIVOS TEMPORÁRIOS ====================
    @Test
    @DisplayName("Deve criar diretório de dados se não existir")
    void criarDiretorioDados_SeNaoExistir(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path novoArquivo = tempDir.resolve("novadata").resolve("usuarios.ser");
        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        setFinalStatic(field, novoArquivo.toString());

        // Act
        UsuarioService service = new UsuarioService();
        service.cadastrarUsuario("test", "senha123", "avatar.png");

        // Assert
        assertTrue(Files.exists(novoArquivo.getParent()));
        assertTrue(Files.exists(novoArquivo));

        // Cleanup
        setFinalStatic(field, "data/usuarios.ser");
    }

    @Test
    @DisplayName("Deve lidar com arquivo de dados corrompido")
    void carregarUsuarios_ArquivoCorrempido(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path arquivoCorrempido = tempDir.resolve("usuarios_corrompidos.ser");
        Files.write(arquivoCorrempido, "dados corrompidos".getBytes());

        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        setFinalStatic(field, arquivoCorrempido.toString());

        // Act
        UsuarioService service = new UsuarioService();

        // Assert
        assertEquals(0, service.getTotalUsuarios()); // Deve iniciar com lista vazia

        // Cleanup
        setFinalStatic(field, "data/usuarios.ser");
    }

    @Test
    @DisplayName("Deve persistir dados entre instâncias diferentes")
    void persistencia_EntreInstancias(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path arquivoTemp = tempDir.resolve("usuarios_temp.ser");
        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        setFinalStatic(field, arquivoTemp.toString());

        // Act
        UsuarioService service1 = new UsuarioService();
        service1.cadastrarUsuario("user1", "senha1", "avatar1.png");
        service1.cadastrarUsuario("user2", "senha2", "avatar2.png");

        UsuarioService service2 = new UsuarioService();

        // Assert
        assertEquals(2, service2.getTotalUsuarios());
        assertNotNull(service2.autenticar("user1", "senha1"));
        assertNotNull(service2.autenticar("user2", "senha2"));

        // Cleanup
        setFinalStatic(field, "data/usuarios.ser");
    }

    // ==================== TESTES DE ISOLAMENTO E ESTADO ====================
    @Test
    @DisplayName("Instâncias diferentes devem ser isoladas em termos de usuário logado")
    void isolamento_UsuarioLogado() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");

        UsuarioService service2 = new UsuarioService();

        // Act
        usuarioService.autenticar("user1", "senha1");
        service2.autenticar("user2", "senha2");

        // Assert
        assertEquals("user1", usuarioService.getUsuarioLogado().getLogin());
        assertEquals("user2", service2.getUsuarioLogado().getLogin());

        usuarioService.logout();
        assertTrue(service2.temUsuarioLogado()); // service2 não deve ser afetado
    }

    @Test
    @DisplayName("Lista retornada deve ser independente da lista interna")
    void listarUsuarios_ListaIndependente() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        List<Usuario> lista1 = usuarioService.listarUsuarios();
        List<Usuario> lista2 = usuarioService.listarUsuarios();

        // Act
        lista1.clear();

        // Assert
        assertEquals(1, lista2.size()); // lista2 não deve ser afetada
        assertEquals(1, usuarioService.getTotalUsuarios()); // lista interna não deve ser afetada
    }

    // ==================== TESTES DE INVARIANTES ====================
    @Test
    @DisplayName("Total de simulações deve sempre ser >= pontuação")
    void invariante_TotalSimulacoesVsPontuacao() {
        // Arrange
        usuarioService.cadastrarUsuario("user", "senha123", "avatar.png");

        // Act & Assert
        usuarioService.atualizarPontuacao("user", 1); // Bem-sucedida
        Usuario usuario = usuarioService.autenticar("user", "senha123");
        assertTrue(usuario.getTotalSimulacoes() >= usuario.getPontuacao());

        usuarioService.atualizarPontuacao("user", 0); // Falhou
        usuario = usuarioService.autenticar("user", "senha123");
        assertTrue(usuario.getTotalSimulacoes() >= usuario.getPontuacao());
    }

    @Test
    @DisplayName("Login deve ser único na lista de usuários")
    void invariante_LoginUnico() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");

        // Act
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        // Assert
        assertEquals(2, usuarios.size());
        assertNotEquals(usuarios.get(0).getLogin(), usuarios.get(1).getLogin());
    }

    // ==================== TESTES DE CASOS EXTREMOS ====================
    @Test
    @DisplayName("Deve lidar com muitos usuários")
    void performance_MuitosUsuarios() {
        // Act
        for (int i = 0; i < 1000; i++) {
            usuarioService.cadastrarUsuario("user" + i, "senha" + i, "avatar" + i + ".png");
        }

        // Assert
        assertEquals(1000, usuarioService.getTotalUsuarios());

        // Teste de busca
        Usuario usuario = usuarioService.autenticar("user500", "senha500");
        assertNotNull(usuario);
        assertEquals("user500", usuario.getLogin());
    }

    @Test
    @DisplayName("Deve manter consistência após múltiplas operações")
    void consistencia_MultiplasOperacoes() {
        // Arrange & Act
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");
        usuarioService.cadastrarUsuario("user3", "senha3", "avatar3.png");

        usuarioService.autenticar("user2", "senha2");
        usuarioService.alterarAvatar("user1", "novo_avatar.png");
        usuarioService.removerUsuario("user3");
        usuarioService.logout();

        // Assert
        assertEquals(2, usuarioService.getTotalUsuarios());
        assertFalse(usuarioService.temUsuarioLogado());

        Usuario user1 = usuarioService.autenticar("user1", "senha1");
        assertNotNull(user1);
        assertEquals("novo_avatar.png", user1.getAvatar());

        Usuario user3 = usuarioService.autenticar("user3", "senha3");
        assertNull(user3);
    }

    // ==================== TESTES DE EDGE CASES PARA ATUALIZAÇÃO ====================
    @Test
    @DisplayName("Deve atualizar usuário com dados modificados externamente")
    void atualizarUsuario_DadosModificadosExternamente() {
        // Arrange
        usuarioService.cadastrarUsuario("user", "senha123", "avatar.png");
        Usuario usuario = usuarioService.autenticar("user", "senha123");

        // Modificar dados do usuário
        usuario.setPontuacao(50);
        usuario.setTotalSimulacoes(100);
        usuario.setAvatar("novo_avatar.png");

        // Act
        usuarioService.atualizarUsuario(usuario);

        // Assert
        UsuarioService newService = new UsuarioService();
        Usuario usuarioAtualizado = newService.autenticar("user", "senha123");
        assertNotNull(usuarioAtualizado);
        assertEquals(50, usuarioAtualizado.getPontuacao());
        assertEquals(100, usuarioAtualizado.getTotalSimulacoes());
        assertEquals("novo_avatar.png", usuarioAtualizado.getAvatar());
    }

    @Test
    @DisplayName("Não deve atualizar usuário inexistente")
    void atualizarUsuario_UsuarioInexistente() {
        // Arrange
        Usuario usuarioFalso = new Usuario("inexistente", "senha123", "avatar.png");
        usuarioFalso.setPontuacao(10);

        // Act
        usuarioService.atualizarUsuario(usuarioFalso);

        // Assert
        assertEquals(0, usuarioService.getTotalUsuarios());
        Usuario usuario = usuarioService.autenticar("inexistente", "senha123");
        assertNull(usuario);
    }

    @Test
    @DisplayName("Deve manter referência correta após atualização")
    void atualizarUsuario_ManterReferenciaCorreta() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");

        Usuario user1 = usuarioService.autenticar("user1", "senha1");
        user1.setPontuacao(20);

        // Act
        usuarioService.atualizarUsuario(user1);

        // Assert
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        assertTrue(usuarios.size() >= 2); // Verifica que tem pelo menos os 2 usuários que criamos

        Usuario user1Atualizado = usuarios.stream()
                .filter(u -> u.getLogin().equals("user1"))
                .findFirst()
                .orElse(null);

        assertNotNull(user1Atualizado);
        assertEquals(20, user1Atualizado.getPontuacao());

        // user2 não deve ter sido afetado
        Usuario user2 = usuarios.stream()
                .filter(u -> u.getLogin().equals("user2"))
                .findFirst()
                .orElse(null);

        assertNotNull(user2);
        assertEquals(0, user2.getPontuacao());
    }
}
