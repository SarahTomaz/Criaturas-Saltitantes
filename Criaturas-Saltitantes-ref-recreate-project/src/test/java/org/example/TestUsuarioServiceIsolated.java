package service;

import java.io.File;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Testes com isolamento adequado e cobertura MC/DC completa
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUsuarioServiceIsolated {

    private UsuarioService usuarioService;
    private static final String DATA_DIR = "data";
    private static final String USUARIOS_FILE = "usuarios.ser";

    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        File dataDir = new File(DATA_DIR);
        if (dataDir.exists()) {
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            dataDir.delete();
        }

        usuarioService = new UsuarioService();
    }

    @AfterEach
    void tearDown() {
        // Limpar dados após cada teste
        File dataDir = new File(DATA_DIR);
        if (dataDir.exists()) {
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            dataDir.delete();
        }
    }

    // ==================== TESTES DE DOMÍNIO ====================
    @Test
    @Order(1)
    @DisplayName("Deve cadastrar um novo usuário com sucesso")
    void cadastrarUsuario_ComSucesso() {
        // Act
        boolean resultado = usuarioService.cadastrarUsuario("teste", "senha123", "avatar.png");

        // Assert
        assertTrue(resultado, "O cadastro deveria retornar true");
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    @Test
    @Order(2)
    @DisplayName("Não deve cadastrar usuário com login já existente")
    void cadastrarUsuario_LoginJaExistente() {
        // Arrange
        usuarioService.cadastrarUsuario("existente", "senha123", "avatar.png");

        // Act
        boolean resultado = usuarioService.cadastrarUsuario("existente", "outrasenha", "outro.png");

        // Assert
        assertFalse(resultado, "Cadastro com login repetido deveria retornar false");
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    // ==================== TESTES DE FRONTEIRA ====================
    @Test
    @Order(3)
    @DisplayName("Deve aceitar senha com exatamente 4 caracteres (fronteira mínima)")
    void cadastrarUsuario_SenhaLimiteMinimoExato() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            boolean resultado = usuarioService.cadastrarUsuario("user", "1234", "avatar.png");
            assertTrue(resultado);
        });
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    @Test
    @Order(4)
    @DisplayName("Deve rejeitar senha com 3 caracteres (abaixo da fronteira)")
    void cadastrarUsuario_SenhaAbaixoLimiteMinimo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("user", "123", "avatar.png");
        });
        assertTrue(exception.getMessage().contains("pelo menos 4 caracteres"));
        assertEquals(0, usuarioService.getTotalUsuarios());
    }

    @Test
    @Order(5)
    @DisplayName("Deve usar avatar padrão se avatar for nulo ou vazio")
    void cadastrarUsuario_ComAvatarPadrao() {
        // Act
        usuarioService.cadastrarUsuario("usuario1", "senha123", null);
        usuarioService.cadastrarUsuario("usuario2", "senha123", "  ");

        // Assert
        Usuario usuario1 = usuarioService.autenticar("usuario1", "senha123");
        Usuario usuario2 = usuarioService.autenticar("usuario2", "senha123");

        assertEquals("default.png", usuario1.getAvatar());
        assertEquals("default.png", usuario2.getAvatar());
    }

    // ==================== TESTES ESTRUTURAIS/MC/DC ====================
    @Test
    @Order(6)
    @DisplayName("Deve retornar null quando login E senha são null (MC/DC)")
    void Autenticar_LoginESenhaNulos_MCDC() {
        // Act
        Usuario resultado = usuarioService.autenticar(null, null);

        // Assert
        assertNull(resultado);
        assertFalse(usuarioService.temUsuarioLogado());
    }

    @Test
    @Order(7)
    @DisplayName("Deve retornar null quando apenas login é null (MC/DC)")
    void autenticar_ApenasLoginNulo_MCDC() {
        // Act
        Usuario resultado = usuarioService.autenticar(null, "senha");

        // Assert
        assertNull(resultado);
        assertFalse(usuarioService.temUsuarioLogado());
    }

    @Test
    @Order(8)
    @DisplayName("Deve retornar null quando apenas senha é null (MC/DC)")
    void autenticar_ApenasSenhaNula_MCDC() {
        // Act
        Usuario resultado = usuarioService.autenticar("login", null);

        // Assert
        assertNull(resultado);
        assertFalse(usuarioService.temUsuarioLogado());
    }

    @Test
    @Order(9)
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

    // ==================== TESTES DE VALIDAÇÃO ROBUSTA ====================
    @Test
    @Order(10)
    @DisplayName("Deve lançar exceção para login inválido")
    void cadastrarUsuario_LoginInvalido() {
        // Login null
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario(null, "senha123", "avatar.png");
        });
        assertTrue(exception1.getMessage().contains("Login não pode ser vazio"));

        // Login vazio
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("  ", "senha123", "avatar.png");
        });
        assertTrue(exception2.getMessage().contains("Login não pode ser vazio"));

        assertEquals(0, usuarioService.getTotalUsuarios());
    }

    @Test
    @Order(11)
    @DisplayName("Deve lançar exceção para senha inválida")
    void cadastrarUsuario_SenhaInvalida() {
        // Senha muito curta
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("teste", "123", "avatar.png");
        });
        assertTrue(exception1.getMessage().contains("pelo menos 4 caracteres"));

        // Senha null
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("teste", null, "avatar.png");
        });
        assertTrue(exception2.getMessage().contains("pelo menos 4 caracteres"));

        assertEquals(0, usuarioService.getTotalUsuarios());
    }

    // ==================== TESTES DE AUTENTICAÇÃO ====================
    @Test
    @Order(12)
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
    @Order(13)
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
    @Order(14)
    @DisplayName("Não deve autenticar usuário inexistente")
    void autenticar_UsuarioInexistente() {
        // Act
        Usuario usuario = usuarioService.autenticar("nonexistent", "pass");

        // Assert
        assertNull(usuario);
        assertFalse(usuarioService.temUsuarioLogado());
    }

    // ==================== TESTES DE OPERAÇÕES ====================
    @Test
    @Order(15)
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
    @Order(16)
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
    @Order(17)
    @DisplayName("Não deve remover usuário inexistente")
    void removerUsuario_Inexistente() {
        // Act
        boolean resultado = usuarioService.removerUsuario("inexistente");

        // Assert
        assertFalse(resultado);
        assertEquals(0, usuarioService.getTotalUsuarios());
    }

    @Test
    @Order(18)
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

    // ==================== TESTES DE ALTERAÇÃO ====================
    @Test
    @Order(19)
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

        // Verify by re-authenticating
        Usuario usuario = usuarioService.autenticar(login, "senha");
        assertNotNull(usuario);
        assertEquals(novoAvatar, usuario.getAvatar());
    }

    @Test
    @Order(20)
    @DisplayName("Não deve alterar o avatar de um usuário inexistente")
    void alterarAvatar_UsuarioInexistente() {
        // Act
        boolean resultado = usuarioService.alterarAvatar("inexistente", "novo_avatar.jpg");

        // Assert
        assertFalse(resultado);
    }

    // ==================== TESTES DE LISTAGEM ====================
    @Test
    @Order(21)
    @DisplayName("Deve listar todos os usuários")
    void listarUsuarios_DeveRetornarListaCorreta() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "senha1", "a1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "a2.png");

        // Act
        var usuarios = usuarioService.listarUsuarios();

        // Assert
        assertEquals(2, usuarios.size());
        assertTrue(usuarios.stream().anyMatch(u -> u.getLogin().equals("user1")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getLogin().equals("user2")));
    }

    @Test
    @Order(22)
    @DisplayName("A lista de usuários retornada deve ser uma cópia")
    void listarUsuarios_DeveRetornarCopia() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "senha1", "a1.png");
        var usuarios = usuarioService.listarUsuarios();

        // Act
        usuarios.add(new Usuario("temp", "temp", "temp.png"));

        // Assert
        assertEquals(1, usuarioService.getTotalUsuarios(), "A lista interna do serviço não deve ser modificada");
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================
    @Test
    @Order(23)
    @DisplayName("Deve atualizar os dados de um usuário")
    void atualizarUsuario_ComSucesso() {
        // Arrange
        String login = "user";
        usuarioService.cadastrarUsuario(login, "senha", "avatar.png");
        Usuario usuario = usuarioService.autenticar(login, "senha");
        assertNotNull(usuario);
        assertEquals(0, usuario.getTotalSimulacoes());

        // Act
        usuario.incrementarTotalSimulacoes();
        usuarioService.atualizarUsuario(usuario);

        // Assert by re-authenticating
        Usuario usuarioAtualizado = usuarioService.autenticar(login, "senha");
        assertNotNull(usuarioAtualizado);
        assertEquals(1, usuarioAtualizado.getTotalSimulacoes());
    }

    @Test
    @Order(24)
    @DisplayName("Deve atualizar a pontuação com valor positivo")
    void atualizarPontuacao_ComValorPositivo() {
        // Arrange
        String login = "user";
        usuarioService.cadastrarUsuario(login, "senha123", "avatar.png");

        // Act
        boolean resultado = usuarioService.atualizarPontuacao(login, 1);

        // Assert
        assertTrue(resultado);
        Usuario usuario = usuarioService.autenticar(login, "senha123");
        assertNotNull(usuario);
        assertEquals(1, usuario.getTotalSimulacoes());
    }

    @Test
    @Order(25)
    @DisplayName("Deve atualizar a pontuação com valor zero ou negativo")
    void atualizarPontuacao_ComValorZero() {
        // Arrange
        String login = "user";
        usuarioService.cadastrarUsuario(login, "senha123", "avatar.png");

        // Act
        boolean resultado = usuarioService.atualizarPontuacao(login, 0);

        // Assert
        assertTrue(resultado);
        Usuario usuario = usuarioService.autenticar(login, "senha123");
        assertNotNull(usuario);
        assertEquals(1, usuario.getTotalSimulacoes());
    }

    // ==================== TESTES DE INVARIANTES ====================
    @Test
    @Order(26)
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
    @Order(27)
    @DisplayName("Login deve ser único na lista de usuários")
    void invariante_LoginUnico() {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");

        // Act
        var usuarios = usuarioService.listarUsuarios();

        // Assert
        assertEquals(2, usuarios.size());
        assertNotEquals(usuarios.get(0).getLogin(), usuarios.get(1).getLogin());
    }

    // ==================== TESTES DE PERSISTÊNCIA ====================
    @Test
    @Order(28)
    @DisplayName("Deve persistir dados entre instâncias diferentes")
    void persistencia_EntreInstancias() {
        // Act
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");

        UsuarioService service2 = new UsuarioService();

        // Assert
        assertEquals(2, service2.getTotalUsuarios());
        assertNotNull(service2.autenticar("user1", "senha1"));
        assertNotNull(service2.autenticar("user2", "senha2"));
    }

    // ==================== TESTES DE CASOS EXTREMOS ====================
    @Test
    @Order(29)
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

    // ==================== TESTES DE ISOLAMENTO ====================
    @Test
    @Order(30)
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
}
