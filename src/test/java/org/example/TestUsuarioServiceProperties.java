package service;

import java.util.Random;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes baseados em propriedades para UsuarioService Estes testes verificam
 * propriedades que devem sempre ser verdadeiras independentemente dos dados de
 * entrada
 */
public class TestUsuarioServiceProperties {

    private UsuarioService usuarioService;
    private Random random;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
        random = new Random(42); // Seed fixo para reproduzibilidade
    }

    @Test
    @DisplayName("Propriedade: Cadastrar usuário sempre aumenta o total ou mantém igual")
    void propriedade_CadastroAumentaOuMantemTotal() {
        for (int i = 0; i < 100; i++) {
            int totalAntes = usuarioService.getTotalUsuarios();
            String login = gerarLoginAleatorio();
            String senha = gerarSenhaAleatoria();
            String avatar = gerarAvatarAleatorio();

            usuarioService.cadastrarUsuario(login, senha, avatar);

            int totalDepois = usuarioService.getTotalUsuarios();
            assertTrue(totalDepois >= totalAntes,
                    "Total de usuários deve aumentar ou manter após cadastro");
        }
    }

    @Test
    @DisplayName("Propriedade: Remover usuário sempre diminui o total ou mantém igual")
    void propriedade_RemocaoImpliesEmDiminuicaoOuMesmtoTotal() {
        // Primeiro, cadastrar alguns usuários
        for (int i = 0; i < 10; i++) {
            usuarioService.cadastrarUsuario("user" + i, "senha123" + i, "avatar" + i + ".png");
        }

        for (int i = 0; i < 20; i++) { // Mais tentativas que usuários existentes
            int totalAntes = usuarioService.getTotalUsuarios();
            String login = "user" + random.nextInt(15); // Alguns existentes, alguns não

            usuarioService.removerUsuario(login);

            int totalDepois = usuarioService.getTotalUsuarios();
            assertTrue(totalDepois <= totalAntes,
                    "Total de usuários deve diminuir ou manter após remoção");
        }
    }

    @Test
    @DisplayName("Propriedade: Autenticação bem-sucedida implica em usuário logado")
    void propriedade_AutenticacaoImplicaUsuarioLogado() {
        for (int i = 0; i < 50; i++) {
            String login = "user" + i;
            String senha = "senha" + i;
            usuarioService.cadastrarUsuario(login, senha, "avatar.png");

            Usuario usuario = usuarioService.autenticar(login, senha);

            if (usuario != null) {
                assertTrue(usuarioService.temUsuarioLogado(),
                        "Se autenticação retorna usuário, deve estar logado");
                assertEquals(login, usuarioService.getUsuarioLogado().getLogin(),
                        "Usuário logado deve ser o mesmo que foi autenticado");
            }

            usuarioService.logout();
        }
    }

    @Test
    @DisplayName("Propriedade: Logout sempre resulta em nenhum usuário logado")
    void propriedade_LogoutImplicaSemUsuarioLogado() {
        for (int i = 0; i < 30; i++) {
            String login = "user" + i;
            String senha = "senha" + i;
            usuarioService.cadastrarUsuario(login, senha, "avatar.png");
            usuarioService.autenticar(login, senha);

            usuarioService.logout();

            assertFalse(usuarioService.temUsuarioLogado(),
                    "Após logout, não deve haver usuário logado");
            assertNull(usuarioService.getUsuarioLogado(),
                    "Após logout, getUsuarioLogado deve retornar null");
        }
    }

    @Test
    @DisplayName("Propriedade: Tamanho da lista é sempre igual ao total de usuários")
    void propriedade_TamanhoListaIgualTotalUsuarios() {
        for (int i = 0; i < 50; i++) {
            int totalEsperado = usuarioService.getTotalUsuarios();
            int tamanhoLista = usuarioService.listarUsuarios().size();

            assertEquals(totalEsperado, tamanhoLista,
                    "Tamanho da lista deve sempre ser igual ao total de usuários");

            // Adicionar um usuário e verificar novamente
            if (i < 25) {
                usuarioService.cadastrarUsuario("user" + i, "senha" + i, "avatar.png");
            }
        }
    }

    @Test
    @DisplayName("Propriedade: Cadastro com login existente sempre retorna false")
    void propriedade_CadastroComLoginExistenteRetornaFalse() {
        String loginFixo = "loginTeste";
        usuarioService.cadastrarUsuario(loginFixo, "senha123", "avatar.png");

        for (int i = 0; i < 30; i++) {
            String senhaAleatoria = gerarSenhaAleatoria();
            String avatarAleatorio = gerarAvatarAleatorio();

            boolean resultado = usuarioService.cadastrarUsuario(loginFixo, senhaAleatoria, avatarAleatorio);

            assertFalse(resultado,
                    "Cadastro com login existente deve sempre retornar false");
        }
    }

    @Test
    @DisplayName("Propriedade: Autenticação com credenciais incorretas sempre retorna null")
    void propriedade_AutenticacaoIncorretaRetornaNulo() {
        usuarioService.cadastrarUsuario("usuario", "senhaCorreta", "avatar.png");

        for (int i = 0; i < 50; i++) {
            String senhaIncorreta = "senhaErrada" + i;
            Usuario resultado = usuarioService.autenticar("usuario", senhaIncorreta);

            assertNull(resultado,
                    "Autenticação com senha incorreta deve sempre retornar null");
        }

        for (int i = 0; i < 50; i++) {
            String loginInexistente = "usuarioInexistente" + i;
            String senhaQualquer = "senhaQualquer" + i;
            Usuario resultado = usuarioService.autenticar(loginInexistente, senhaQualquer);

            assertNull(resultado,
                    "Autenticação com login inexistente deve sempre retornar null");
        }
    }

    @Test
    @DisplayName("Propriedade: Atualização de pontuação sempre mantém ou aumenta simulações")
    void propriedade_AtualizacaoPontuacaoMantemOuAumentaSimulacoes() {
        usuarioService.cadastrarUsuario("usuario", "senha123", "avatar.png");

        for (int i = 0; i < 30; i++) {
            Usuario usuarioAntes = usuarioService.autenticar("usuario", "senha123");
            int simulacoesAntes = usuarioAntes.getTotalSimulacoes();

            int pontuacao = random.nextInt(3) - 1; // -1, 0, ou 1
            usuarioService.atualizarPontuacao("usuario", pontuacao);

            Usuario usuarioDepois = usuarioService.autenticar("usuario", "senha123");
            int simulacoesDepois = usuarioDepois.getTotalSimulacoes();

            assertTrue(simulacoesDepois >= simulacoesAntes,
                    "Total de simulações deve sempre aumentar ou manter após atualização");
        }
    }

    @Test
    @DisplayName("Propriedade: Alteração de avatar sempre mantém outros dados")
    void propriedade_AlteracaoAvatarMantemOutrosDados() {
        usuarioService.cadastrarUsuario("usuario", "senha123", "avatar.png");
        Usuario usuarioOriginal = usuarioService.autenticar("usuario", "senha123");

        for (int i = 0; i < 20; i++) {
            String novoAvatar = "avatar" + i + ".png";
            usuarioService.alterarAvatar("usuario", novoAvatar);

            Usuario usuarioAtualizado = usuarioService.autenticar("usuario", "senha123");

            assertEquals(usuarioOriginal.getLogin(), usuarioAtualizado.getLogin(),
                    "Login deve permanecer inalterado");
            assertEquals(usuarioOriginal.getPontuacao(), usuarioAtualizado.getPontuacao(),
                    "Pontuação deve permanecer inalterada");
            assertEquals(usuarioOriginal.getTotalSimulacoes(), usuarioAtualizado.getTotalSimulacoes(),
                    "Total de simulações deve permanecer inalterado");
            assertEquals(novoAvatar, usuarioAtualizado.getAvatar(),
                    "Avatar deve ser atualizado para o novo valor");
        }
    }

    @Test
    @DisplayName("Propriedade: Idempotência - operações repetidas não alteram resultado")
    void propriedade_Idempotencia() {
        // Logout múltiplo
        usuarioService.logout();
        usuarioService.logout();
        usuarioService.logout();
        assertFalse(usuarioService.temUsuarioLogado());

        // Remoção de usuário inexistente
        boolean resultado1 = usuarioService.removerUsuario("inexistente");
        boolean resultado2 = usuarioService.removerUsuario("inexistente");
        assertEquals(resultado1, resultado2);
        assertFalse(resultado1);

        // Alteração de avatar de usuário inexistente
        boolean altAvatar1 = usuarioService.alterarAvatar("inexistente", "avatar.png");
        boolean altAvatar2 = usuarioService.alterarAvatar("inexistente", "avatar.png");
        assertEquals(altAvatar1, altAvatar2);
        assertFalse(altAvatar1);
    }

    // Métodos auxiliares para gerar dados aleatórios
    private String gerarLoginAleatorio() {
        String[] prefixos = {"user", "admin", "test", "player", "guest"};
        String prefixo = prefixos[random.nextInt(prefixos.length)];
        return prefixo + random.nextInt(10000);
    }

    private String gerarSenhaAleatoria() {
        String caracteres = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder senha = new StringBuilder();
        int tamanho = 4 + random.nextInt(16); // Entre 4 e 20 caracteres

        for (int i = 0; i < tamanho; i++) {
            senha.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }

        return senha.toString();
    }

    private String gerarAvatarAleatorio() {
        String[] avatares = {"default.png", "user.jpg", "avatar.gif", "profile.png", null, "  "};
        return avatares[random.nextInt(avatares.length)];
    }
}
