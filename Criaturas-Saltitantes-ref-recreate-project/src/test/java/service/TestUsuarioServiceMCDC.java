package service;

import java.io.File;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes específicos para garantir 100% de cobertura MC/DC Cada condição
 * booleana complexa é testada com todas as combinações necessárias
 */
public class TestUsuarioServiceMCDC {

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        limprarDadosPersistentes();
        usuarioService = new UsuarioService();
    }

    @AfterEach
    void tearDown() {
        // Limpar dados após cada teste
        limprarDadosPersistentes();
    }

    private void limprarDadosPersistentes() {
        File dataDir = new File("data");
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

    // ==================== MC/DC para cadastrarUsuario ====================
    @Test
    @DisplayName("MC/DC: login == null OU login.trim().isEmpty() - Caso 1: login null")
    void cadastrarUsuario_MCDC_LoginNull() {
        // login == null (TRUE) OU login.trim().isEmpty() (não avaliado)
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario(null, "senha1234", "avatar.png");
        });
    }

    @Test
    @DisplayName("MC/DC: login == null OU login.trim().isEmpty() - Caso 2: login vazio")
    void cadastrarUsuario_MCDC_LoginVazio() {
        // login == null (FALSE) OU login.trim().isEmpty() (TRUE)
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("   ", "senha1234", "avatar.png");
        });
    }

    @Test
    @DisplayName("MC/DC: login == null OU login.trim().isEmpty() - Caso 3: login válido")
    void cadastrarUsuario_MCDC_LoginValido() {
        // login == null (FALSE) OU login.trim().isEmpty() (FALSE)
        assertDoesNotThrow(() -> {
            usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        });
    }

    @Test
    @DisplayName("MC/DC: senha == null OU senha.length() < 4 - Caso 1: senha null")
    void cadastrarUsuario_MCDC_SenhaNull() {
        // senha == null (TRUE) OU senha.length() < 4 (não avaliado)
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("user", null, "avatar.png");
        });
    }

    @Test
    @DisplayName("MC/DC: senha == null OU senha.length() < 4 - Caso 2: senha curta")
    void cadastrarUsuario_MCDC_SenhaCurta() {
        // senha == null (FALSE) OU senha.length() < 4 (TRUE)
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("user", "123", "avatar.png");
        });
    }

    @Test
    @DisplayName("MC/DC: senha == null OU senha.length() < 4 - Caso 3: senha válida")
    void cadastrarUsuario_MCDC_SenhaValida() {
        // senha == null (FALSE) OU senha.length() < 4 (FALSE)
        assertDoesNotThrow(() -> {
            usuarioService.cadastrarUsuario("user", "1234", "avatar.png");
        });
    }

    @Test
    @DisplayName("MC/DC: avatar == null OU avatar.trim().isEmpty() - Caso 1: avatar null")
    void cadastrarUsuario_MCDC_AvatarNull() {
        // avatar == null (TRUE) OU avatar.trim().isEmpty() (não avaliado)
        usuarioService.cadastrarUsuario("user", "senha1234", null);
        Usuario usuario = usuarioService.autenticar("user", "senha1234");
        assertEquals("default.png", usuario.getAvatar());
    }

    @Test
    @DisplayName("MC/DC: avatar == null OU avatar.trim().isEmpty() - Caso 2: avatar vazio")
    void cadastrarUsuario_MCDC_AvatarVazio() {
        // avatar == null (FALSE) OU avatar.trim().isEmpty() (TRUE)
        usuarioService.cadastrarUsuario("user", "senha1234", "   ");
        Usuario usuario = usuarioService.autenticar("user", "senha1234");
        assertEquals("default.png", usuario.getAvatar());
    }

    @Test
    @DisplayName("MC/DC: avatar == null OU avatar.trim().isEmpty() - Caso 3: avatar válido")
    void cadastrarUsuario_MCDC_AvatarValido() {
        // avatar == null (FALSE) OU avatar.trim().isEmpty() (FALSE)
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        Usuario usuario = usuarioService.autenticar("user", "senha1234");
        assertEquals("avatar.png", usuario.getAvatar());
    }

    // ==================== MC/DC para autenticar ====================
    @Test
    @DisplayName("MC/DC: login == null OU senha == null - Caso 1: ambos null")
    void autenticar_MCDC_AmbosNull() {
        // login == null (TRUE) OU senha == null (TRUE)
        Usuario resultado = usuarioService.autenticar(null, null);
        assertNull(resultado);
    }

    @Test
    @DisplayName("MC/DC: login == null OU senha == null - Caso 2: apenas login null")
    void autenticar_MCDC_ApenasLoginNull() {
        // login == null (TRUE) OU senha == null (FALSE)
        Usuario resultado = usuarioService.autenticar(null, "senha");
        assertNull(resultado);
    }

    @Test
    @DisplayName("MC/DC: login == null OU senha == null - Caso 3: apenas senha null")
    void autenticar_MCDC_ApenasSenhaNull() {
        // login == null (FALSE) OU senha == null (TRUE)
        Usuario resultado = usuarioService.autenticar("user", null);
        assertNull(resultado);
    }

    @Test
    @DisplayName("MC/DC: login == null OU senha == null - Caso 4: nenhum null")
    void autenticar_MCDC_NenhumNull() {
        // login == null (FALSE) OU senha == null (FALSE)
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        Usuario resultado = usuarioService.autenticar("user", "senha1234");
        assertNotNull(resultado);
    }

    // ==================== MC/DC para removerUsuario ====================
    @Test
    @DisplayName("MC/DC: usuarioLogado != null E usuarioLogado.getLogin().equals(login) - Caso 1: não logado")
    void removerUsuario_MCDC_NaoLogado() {
        // usuarioLogado != null (FALSE) - segunda condição não é avaliada
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        boolean resultado = usuarioService.removerUsuario("user");
        assertTrue(resultado);
        assertEquals(0, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("MC/DC: usuarioLogado != null E usuarioLogado.getLogin().equals(login) - Caso 2: logado outro usuário")
    void removerUsuario_MCDC_LogadoOutroUsuario() {
        // usuarioLogado != null (TRUE) E usuarioLogado.getLogin().equals(login) (FALSE)
        usuarioService.cadastrarUsuario("user1", "senha1234", "avatar.png");
        usuarioService.cadastrarUsuario("user2", "senha1234", "avatar.png");
        usuarioService.autenticar("user1", "senha1234");

        boolean resultado = usuarioService.removerUsuario("user2");
        assertTrue(resultado);
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("MC/DC: usuarioLogado != null E usuarioLogado.getLogin().equals(login) - Caso 3: logado mesmo usuário")
    void removerUsuario_MCDC_LogadoMesmoUsuario() {
        // usuarioLogado != null (TRUE) E usuarioLogado.getLogin().equals(login) (TRUE)
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        usuarioService.autenticar("user", "senha1234");

        boolean resultado = usuarioService.removerUsuario("user");
        assertFalse(resultado);
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    // ==================== MC/DC para atualizarPontuacao ====================
    @Test
    @DisplayName("MC/DC: usuarioOpt.isPresent() E pontuacao > 0 - Caso 1: usuário não existe")
    void atualizarPontuacao_MCDC_UsuarioNaoExiste() {
        // usuarioOpt.isPresent() (FALSE) - segunda condição não é avaliada
        boolean resultado = usuarioService.atualizarPontuacao("inexistente", 5);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("MC/DC: usuarioOpt.isPresent() E pontuacao > 0 - Caso 2: usuário existe, pontuação negativa")
    void atualizarPontuacao_MCDC_UsuarioExistePontuacaoNegativa() {
        // usuarioOpt.isPresent() (TRUE) E pontuacao > 0 (FALSE)
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        boolean resultado = usuarioService.atualizarPontuacao("user", -1);
        assertTrue(resultado);

        Usuario usuario = usuarioService.autenticar("user", "senha1234");
        assertEquals(0, usuario.getPontuacao()); // Não incrementa pontuação
        assertEquals(1, usuario.getTotalSimulacoes()); // Incrementa total
    }

    @Test
    @DisplayName("MC/DC: usuarioOpt.isPresent() E pontuacao > 0 - Caso 3: usuário existe, pontuação positiva")
    void atualizarPontuacao_MCDC_UsuarioExistePontuacaoPositiva() {
        // usuarioOpt.isPresent() (TRUE) E pontuacao > 0 (TRUE)
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        boolean resultado = usuarioService.atualizarPontuacao("user", 5);
        assertTrue(resultado);

        Usuario usuario = usuarioService.autenticar("user", "senha1234");
        assertEquals(1, usuario.getPontuacao()); // Incrementa pontuação
        assertEquals(1, usuario.getTotalSimulacoes()); // Incrementa total
    }

    @Test
    @DisplayName("MC/DC: pontuacao > 0 com valor zero (fronteira)")
    void atualizarPontuacao_MCDC_PontuacaoZero() {
        // Caso específico para pontuacao == 0
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        boolean resultado = usuarioService.atualizarPontuacao("user", 0);
        assertTrue(resultado);

        Usuario usuario = usuarioService.autenticar("user", "senha1234");
        assertEquals(0, usuario.getPontuacao()); // Não incrementa pontuação
        assertEquals(1, usuario.getTotalSimulacoes()); // Incrementa total
    }

    // ==================== MC/DC para buscarUsuarioPorLogin (via autenticar) ====================
    @Test
    @DisplayName("MC/DC: usuário encontrado E senha correta - Caso 1: usuário não encontrado")
    void autenticar_MCDC_UsuarioNaoEncontrado() {
        // usuarioOpt.isPresent() (FALSE) - não acessa .autenticar()
        Usuario resultado = usuarioService.autenticar("inexistente", "senha");
        assertNull(resultado);
    }

    @Test
    @DisplayName("MC/DC: usuário encontrado E senha correta - Caso 2: usuário encontrado, senha errada")
    void autenticar_MCDC_UsuarioEncontradoSenhaErrada() {
        // usuarioOpt.isPresent() (TRUE) E usuario.autenticar(senha) (FALSE)
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        Usuario resultado = usuarioService.autenticar("user", "senhaerrada");
        assertNull(resultado);
    }

    @Test
    @DisplayName("MC/DC: usuário encontrado E senha correta - Caso 3: usuário encontrado, senha correta")
    void autenticar_MCDC_UsuarioEncontradoSenhaCorreta() {
        // usuarioOpt.isPresent() (TRUE) E usuario.autenticar(senha) (TRUE)
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        Usuario resultado = usuarioService.autenticar("user", "senha1234");
        assertNotNull(resultado);
        assertEquals("user", resultado.getLogin());
    }

    // ==================== MC/DC para carregarUsuarios (via construtor) ====================
    @Test
    @DisplayName("MC/DC: arquivo existe - Caso 1: arquivo não existe")
    void carregarUsuarios_MCDC_ArquivoNaoExiste() {
        // arquivo.exists() (FALSE)
        // Este caso é coberto pelo setUp() que limpa os dados
        UsuarioService service = new UsuarioService();
        assertEquals(0, service.getTotalUsuarios());
    }

    @Test
    @DisplayName("MC/DC: arquivo existe - Caso 2: arquivo existe")
    void carregarUsuarios_MCDC_ArquivoExiste() {
        // arquivo.exists() (TRUE)
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        // Criar nova instância para carregar dados do arquivo
        UsuarioService newService = new UsuarioService();
        assertEquals(1, newService.getTotalUsuarios());
        assertNotNull(newService.autenticar("user", "senha1234"));
    }

    // ==================== MC/DC para getTaxaSucesso (classe Usuario) ====================
    @Test
    @DisplayName("MC/DC: totalSimulacoes == 0 na getTaxaSucesso")
    void getTaxaSucesso_MCDC_TotalZero() {
        // totalSimulacoes == 0 (TRUE)
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        Usuario usuario = usuarioService.autenticar("user", "senha1234");
        assertEquals(0.0, usuario.getTaxaSucesso());
    }

    @Test
    @DisplayName("MC/DC: totalSimulacoes > 0 na getTaxaSucesso")
    void getTaxaSucesso_MCDC_TotalMaiorQueZero() {
        // totalSimulacoes == 0 (FALSE)
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");
        usuarioService.atualizarPontuacao("user", 1); // Incrementa total
        Usuario usuario = usuarioService.autenticar("user", "senha1234");
        assertTrue(usuario.getTaxaSucesso() >= 0.0);
    }
}
