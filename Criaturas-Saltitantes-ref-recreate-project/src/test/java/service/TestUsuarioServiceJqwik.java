package service;

import java.io.File;
import java.util.List;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Report;
import net.jqwik.api.Reporting;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.api.lifecycle.BeforeProperty;

/**
 * Testes baseados em propriedades usando jqwik Verifica propriedades que devem
 * sempre ser verdadeiras independentemente dos dados de entrada
 */
public class TestUsuarioServiceJqwik {

    private UsuarioService usuarioService;

    @BeforeProperty
    void setUp() {
        // Limpar dados antes de cada propriedade
        limprarDadosPersistentes();
        usuarioService = new UsuarioService();
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

    // ==================== PROPRIEDADES BÁSICAS ====================
    @Property
    @Report(Reporting.GENERATED)
    void propriedade_CadastroAumentaOuMantemTotal(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String login,
            @ForAll @StringLength(min = 4, max = 100) String senha,
            @ForAll @StringLength(max = 100) String avatar) {
        // Arrange
        int totalAntes = usuarioService.getTotalUsuarios();

        try {
            // Act
            boolean resultado = usuarioService.cadastrarUsuario(login, senha, avatar);
            int totalDepois = usuarioService.getTotalUsuarios();

            // Assert
            if (resultado) {
                assertEquals(totalAntes + 1, totalDepois,
                        "Se cadastro teve sucesso, total deve aumentar em 1");
            } else {
                assertEquals(totalAntes, totalDepois,
                        "Se cadastro falhou, total deve permanecer igual");
            }

            // Total nunca deve diminuir
            assertTrue(totalDepois >= totalAntes,
                    "Total de usuários nunca deve diminuir após cadastro");

        } catch (IllegalArgumentException e) {
            // Se lançou exceção, total deve permanecer igual
            assertEquals(totalAntes, usuarioService.getTotalUsuarios(),
                    "Total deve permanecer igual quando exceção é lançada");
        }
    }

    @Property
    void propriedade_AutenticacaoNaoAlteraTotal(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String login,
            @ForAll @StringLength(min = 1, max = 100) String senha) {
        // Arrange
        int totalAntes = usuarioService.getTotalUsuarios();

        // Act
        usuarioService.autenticar(login, senha);
        int totalDepois = usuarioService.getTotalUsuarios();

        // Assert
        assertEquals(totalAntes, totalDepois,
                "Autenticação nunca deve alterar o total de usuários");
    }

    @Property
    void propriedade_RemocaoUsuarioLogadoSempreFalha(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String login,
            @ForAll @StringLength(min = 4, max = 100) String senha,
            @ForAll @StringLength(max = 100) String avatar) {
        // Arrange
        try {
            usuarioService.cadastrarUsuario(login, senha, avatar);
            usuarioService.autenticar(login, senha);

            // Act
            boolean resultado = usuarioService.removerUsuario(login);

            // Assert
            assertFalse(resultado, "Não deve conseguir remover usuário logado");
            assertTrue(usuarioService.temUsuarioLogado(), "Usuário deve permanecer logado");
            assertEquals(1, usuarioService.getTotalUsuarios(), "Total deve permanecer 1");

        } catch (IllegalArgumentException e) {
            // Dados inválidos - ok
        }
    }

    // ==================== PROPRIEDADES DE INVARIANTES ====================
    @Property
    void propriedade_TotalSimulacoesSempreNaoNegativo(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String login,
            @ForAll @StringLength(min = 4, max = 100) String senha,
            @ForAll @StringLength(max = 100) String avatar,
            @ForAll @IntRange(min = -1000, max = 1000) int pontuacao) {
        try {
            // Arrange
            usuarioService.cadastrarUsuario(login, senha, avatar);

            // Act
            usuarioService.atualizarPontuacao(login, pontuacao);
            Usuario usuario = usuarioService.autenticar(login, senha);

            // Assert
            if (usuario != null) {
                assertTrue(usuario.getTotalSimulacoes() >= 0,
                        "Total de simulações deve sempre ser não-negativo");
                assertTrue(usuario.getPontuacao() >= 0,
                        "Pontuação deve sempre ser não-negativa");
                assertTrue(usuario.getTotalSimulacoes() >= usuario.getPontuacao(),
                        "Total de simulações deve sempre ser >= pontuação");
            }

        } catch (IllegalArgumentException e) {
            // Dados inválidos - ok
        }
    }

    @Property
    void propriedade_TaxaSucessoEntre0e1(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String login,
            @ForAll @StringLength(min = 4, max = 100) String senha,
            @ForAll @StringLength(max = 100) String avatar,
            @ForAll @IntRange(min = 0, max = 100) int simulacoesBemSucedidas,
            @ForAll @IntRange(min = 0, max = 100) int simulacoesFalharam) {
        try {
            // Arrange
            usuarioService.cadastrarUsuario(login, senha, avatar);
            Usuario usuario = usuarioService.autenticar(login, senha);

            if (usuario != null) {
                // Simular operações
                for (int i = 0; i < simulacoesBemSucedidas; i++) {
                    usuario.incrementarSimulacaoBemSucedida();
                }
                for (int i = 0; i < simulacoesFalharam; i++) {
                    usuario.incrementarTotalSimulacoes();
                }

                // Act
                double taxa = usuario.getTaxaSucesso();

                // Assert
                assertTrue(taxa >= 0.0, "Taxa de sucesso deve ser >= 0");
                assertTrue(taxa <= 1.0, "Taxa de sucesso deve ser <= 1");

                if (usuario.getTotalSimulacoes() == 0) {
                    assertEquals(0.0, taxa, "Taxa deve ser 0 quando não há simulações");
                }
            }

        } catch (IllegalArgumentException e) {
            // Dados inválidos - ok
        }
    }

    // ==================== PROPRIEDADES DE CONSISTÊNCIA ====================
    @Property
    void propriedade_CadastroSeguido_DeAutenticacao_DeveTerSucesso(@ForAll @AlphaChars @StringLength(min = 1, max = 50) String login,
            @ForAll @StringLength(min = 4, max = 100) String senha,
            @ForAll @StringLength(max = 100) String avatar) {
        try {
            // Act
            boolean cadastroSucesso = usuarioService.cadastrarUsuario(login, senha, avatar);

            if (cadastroSucesso) {
                Usuario usuario = usuarioService.autenticar(login, senha);

                // Assert
                assertNotNull(usuario, "Usuário recém-cadastrado deve conseguir autenticar");
                assertEquals(login, usuario.getLogin(), "Login deve ser preservado");

                if (avatar == null || avatar.trim().isEmpty()) {
                    assertEquals("default.png", usuario.getAvatar(), "Avatar deve ser default.png quando não fornecido");
                } else {
                    assertEquals(avatar.trim(), usuario.getAvatar(), "Avatar deve ser preservado (trimmed)");
                }
            }

        } catch (IllegalArgumentException e) {
            // Dados inválidos - comportamento esperado
        }
    }

    @Property
    void propriedade_LoginCaseInsensitive(@ForAll @AlphaChars @StringLength(min = 1, max = 20) String login,
            @ForAll @StringLength(min = 4, max = 50) String senha) {
        try {
            // Arrange
            usuarioService.cadastrarUsuario(login, senha, "avatar.png");

            // Act & Assert
            Usuario usuario1 = usuarioService.autenticar(login.toLowerCase(), senha);
            Usuario usuario2 = usuarioService.autenticar(login.toUpperCase(), senha);
            Usuario usuario3 = usuarioService.autenticar(login, senha);

            // Todos devem retornar o mesmo usuário (ou todos null se login for inválido)
            if (usuario1 != null) {
                assertNotNull(usuario2, "Login deve ser case insensitive");
                assertNotNull(usuario3, "Login deve ser case insensitive");
                assertEquals(usuario1.getLogin(), usuario2.getLogin());
                assertEquals(usuario1.getLogin(), usuario3.getLogin());
            }

        } catch (IllegalArgumentException e) {
            // Dados inválidos - ok
        }
    }

    // ==================== PROPRIEDADES DE PERSISTÊNCIA ====================
    @Property
    void propriedade_PersistenciaEntreInstancias(@ForAll @AlphaChars @StringLength(min = 1, max = 30) String login,
            @ForAll @StringLength(min = 4, max = 50) String senha,
            @ForAll @StringLength(max = 50) String avatar) {
        try {
            // Arrange & Act
            boolean cadastrado = usuarioService.cadastrarUsuario(login, senha, avatar);

            if (cadastrado) {
                // Criar nova instância para testar persistência
                UsuarioService novaInstancia = new UsuarioService();

                // Assert
                assertEquals(1, novaInstancia.getTotalUsuarios(),
                        "Nova instância deve carregar usuário persistido");

                Usuario usuarioCarregado = novaInstancia.autenticar(login, senha);
                assertNotNull(usuarioCarregado, "Usuário deve estar disponível na nova instância");
                assertEquals(login, usuarioCarregado.getLogin(), "Login deve ser preservado");
            }

        } catch (IllegalArgumentException e) {
            // Dados inválidos - ok
        }
    }

    // ==================== PROPRIEDADES DE LISTA ====================
    @Property
    void propriedade_ListarUsuarios_SempreRetornaCopia(@ForAll @Size(max = 10) List<@AlphaChars @StringLength(min = 1, max = 20) String> logins,
            @ForAll @StringLength(min = 4, max = 20) String senhaBase) {
        // Arrange - cadastrar usuários únicos
        for (int i = 0; i < logins.size(); i++) {
            try {
                usuarioService.cadastrarUsuario(logins.get(i) + "_" + i, senhaBase + i, "avatar" + i + ".png");
            } catch (IllegalArgumentException e) {
                // Login inválido - pular
            }
        }

        // Act
        List<Usuario> lista1 = usuarioService.listarUsuarios();
        List<Usuario> lista2 = usuarioService.listarUsuarios();

        // Assert
        assertNotSame(lista1, lista2, "Cada chamada deve retornar uma nova lista");
        assertEquals(lista1.size(), lista2.size(), "Listas devem ter o mesmo tamanho");

        // Modificar uma lista não deve afetar a outra
        int tamanhoOriginal = lista1.size();
        lista1.clear();
        assertEquals(tamanhoOriginal, lista2.size(), "Modificar uma lista não deve afetar a outra");
        assertEquals(tamanhoOriginal, usuarioService.listarUsuarios().size(), "Lista interna não deve ser afetada");
    }

    // ==================== PROPRIEDADES DE ESTADOS ====================
    @Property
    void propriedade_LogoutSempreValido() {
        // Esta propriedade verifica que logout sempre funciona, independente do estado

        // Act
        usuarioService.logout(); // Deve funcionar mesmo sem usuário logado

        // Assert
        assertFalse(usuarioService.temUsuarioLogado(), "Após logout, não deve ter usuário logado");
        assertNull(usuarioService.getUsuarioLogado(), "Após logout, usuário logado deve ser null");
    }

    @Property
    void propriedade_AlterarAvatarPersiste(@ForAll @AlphaChars @StringLength(min = 1, max = 20) String login,
            @ForAll @StringLength(min = 4, max = 20) String senha,
            @ForAll @StringLength(min = 1, max = 50) String avatarInicial,
            @ForAll @StringLength(min = 1, max = 50) String avatarNovo) {
        try {
            // Arrange
            usuarioService.cadastrarUsuario(login, senha, avatarInicial);

            // Act
            boolean alterado = usuarioService.alterarAvatar(login, avatarNovo);

            if (alterado) {
                // Verificar na mesma instância
                Usuario usuario1 = usuarioService.autenticar(login, senha);
                assertNotNull(usuario1);
                assertEquals(avatarNovo, usuario1.getAvatar(), "Avatar deve ser alterado na mesma instância");

                // Verificar persistência em nova instância
                UsuarioService novaInstancia = new UsuarioService();
                Usuario usuario2 = novaInstancia.autenticar(login, senha);
                assertNotNull(usuario2);
                assertEquals(avatarNovo, usuario2.getAvatar(), "Avatar deve ser persistido");
            }

        } catch (IllegalArgumentException e) {
            // Dados inválidos - ok
        }
    }

    // ==================== GERADORES PERSONALIZADOS ====================
    @Provide
    Arbitrary<String> loginValido() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(30)
                .filter(s -> !s.trim().isEmpty());
    }

    @Provide
    Arbitrary<String> senhaValida() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(4)
                .ofMaxLength(50);
    }

    @Property
    void propriedade_ComGeradores_CadastroSempreValido(@ForAll("loginValido") String login,
            @ForAll("senhaValida") String senha,
            @ForAll @StringLength(max = 50) String avatar) {
        // Act
        boolean resultado = usuarioService.cadastrarUsuario(login, senha, avatar);

        // Assert
        assertTrue(resultado, "Cadastro com dados válidos deve sempre ter sucesso");

        Usuario usuario = usuarioService.autenticar(login, senha);
        assertNotNull(usuario, "Usuário cadastrado deve conseguir autenticar");
    }

    // ==================== PROPRIEDADE SIMPLES DE VALIDAÇÃO ====================
    @Property
    void propriedade_ValidacaoSemEstatisticas(@ForAll @StringLength(max = 50) String login,
            @ForAll @StringLength(max = 50) String senha) {
        boolean loginValido = login != null && !login.trim().isEmpty();
        boolean senhaValida = senha != null && senha.length() >= 4;

        try {
            boolean resultado = usuarioService.cadastrarUsuario(login, senha, "avatar.png");

            if (loginValido && senhaValida) {
                assertTrue(resultado, "Cadastro com dados válidos deve ter sucesso");
            }

        } catch (IllegalArgumentException e) {
            // Deve acontecer quando dados são inválidos
            assertFalse(loginValido && senhaValida, "Exceção só deve ocorrer com dados inválidos");
        }
    }
}
