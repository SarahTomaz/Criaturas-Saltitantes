package service;

import org.example.model.Usuario;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes específicos para a classe Usuario Focando em cobertura MC/DC e casos
 * extremos
 */
public class TestUsuarioModel {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("testUser", "senha123", "avatar.png");
    }

    @Test
    @DisplayName("Deve calcular taxa de sucesso corretamente quando total é zero")
    void getTaxaSucesso_TotalZero() {
        // Assert inicial
        assertEquals(0.0, usuario.getTaxaSucesso(), "Taxa deve ser 0 quando não há simulações");
    }

    @Test
    @DisplayName("Deve calcular taxa de sucesso corretamente com simulações")
    void getTaxaSucesso_ComSimulacoes() {
        // Arrange
        usuario.incrementarSimulacaoBemSucedida(); // pontuacao=1, total=1
        usuario.incrementarTotalSimulacoes(); // pontuacao=1, total=2
        usuario.incrementarSimulacaoBemSucedida(); // pontuacao=2, total=3

        // Act
        double taxa = usuario.getTaxaSucesso();

        // Assert
        assertEquals(2.0 / 3.0, taxa, 0.0001, "Taxa deve ser 2/3");
    }

    @Test
    @DisplayName("Deve autenticar com senha correta")
    void autenticar_SenhaCorreta() {
        assertTrue(usuario.autenticar("senha123"));
    }

    @Test
    @DisplayName("Deve falhar autenticação com senha incorreta")
    void autenticar_SenhaIncorreta() {
        assertFalse(usuario.autenticar("senhaErrada"));
        assertFalse(usuario.autenticar("SENHA123")); // Case sensitive
        assertFalse(usuario.autenticar("senha1234")); // Tamanho diferente
    }

    @Test
    @DisplayName("Deve falhar autenticação com senha null")
    void autenticar_SenhaNull() {
        assertFalse(usuario.autenticar(null));
    }

    @Test
    @DisplayName("Deve incrementar simulação bem-sucedida corretamente")
    void incrementarSimulacaoBemSucedida() {
        // Estado inicial
        assertEquals(0, usuario.getPontuacao());
        assertEquals(0, usuario.getTotalSimulacoes());

        // Act
        usuario.incrementarSimulacaoBemSucedida();

        // Assert
        assertEquals(1, usuario.getPontuacao());
        assertEquals(1, usuario.getTotalSimulacoes());

        // Mais uma vez
        usuario.incrementarSimulacaoBemSucedida();
        assertEquals(2, usuario.getPontuacao());
        assertEquals(2, usuario.getTotalSimulacoes());
    }

    @Test
    @DisplayName("Deve incrementar total de simulações sem afetar pontuação")
    void incrementarTotalSimulacoes() {
        // Estado inicial
        assertEquals(0, usuario.getPontuacao());
        assertEquals(0, usuario.getTotalSimulacoes());

        // Act
        usuario.incrementarTotalSimulacoes();

        // Assert
        assertEquals(0, usuario.getPontuacao()); // Não deve mudar
        assertEquals(1, usuario.getTotalSimulacoes());

        // Mais uma vez
        usuario.incrementarTotalSimulacoes();
        assertEquals(0, usuario.getPontuacao());
        assertEquals(2, usuario.getTotalSimulacoes());
    }

    @Test
    @DisplayName("Deve manter invariante: pontuação <= total simulações")
    void invariante_PontuacaoMenorIgualTotal() {
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0) {
                usuario.incrementarSimulacaoBemSucedida();
            } else {
                usuario.incrementarTotalSimulacoes();
            }

            assertTrue(usuario.getPontuacao() <= usuario.getTotalSimulacoes(),
                    "Pontuação deve sempre ser <= total de simulações");
        }
    }

    @Test
    @DisplayName("Deve implementar equals corretamente baseado no login")
    void equals_BaseadoNoLogin() {
        Usuario usuario1 = new Usuario("sameLogin", "senha1", "avatar1.png");
        Usuario usuario2 = new Usuario("sameLogin", "senha2", "avatar2.png");
        Usuario usuario3 = new Usuario("differentLogin", "senha1", "avatar1.png");

        // Mesmo login deve ser igual
        assertEquals(usuario1, usuario2);

        // Login diferente deve ser diferente
        assertNotEquals(usuario1, usuario3);

        // Reflexividade
        assertEquals(usuario1, usuario1);

        // Null safety
        assertNotEquals(usuario1, null);

        // Tipo diferente
        assertNotEquals(usuario1, "string");
    }

    @Test
    @DisplayName("Deve implementar hashCode consistente com equals")
    void hashCode_ConsistenteComEquals() {
        Usuario usuario1 = new Usuario("sameLogin", "senha1", "avatar1.png");
        Usuario usuario2 = new Usuario("sameLogin", "senha2", "avatar2.png");

        // Se equals, hashCode deve ser igual
        assertEquals(usuario1, usuario2);
        assertEquals(usuario1.hashCode(), usuario2.hashCode());
    }

    @Test
    @DisplayName("Deve implementar toString com informações relevantes")
    void toString_ComInformacoesRelevantes() {
        usuario.setPontuacao(10);
        usuario.setTotalSimulacoes(20);

        String resultado = usuario.toString();

        assertTrue(resultado.contains("testUser"));
        assertTrue(resultado.contains("avatar.png"));
        assertTrue(resultado.contains("10"));
        assertTrue(resultado.contains("20"));
    }

    @Test
    @DisplayName("Deve permitir modificação através de setters")
    void setters_ModificacaoCorreta() {
        // Act
        usuario.setLogin("novoLogin");
        usuario.setAvatar("novoAvatar.jpg");
        usuario.setPontuacao(50);
        usuario.setTotalSimulacoes(100);

        // Assert
        assertEquals("novoLogin", usuario.getLogin());
        assertEquals("novoAvatar.jpg", usuario.getAvatar());
        assertEquals(50, usuario.getPontuacao());
        assertEquals(100, usuario.getTotalSimulacoes());
    }

    @Test
    @DisplayName("Deve lidar com valores extremos nos setters")
    void setters_ValoresExtremos() {
        // Valores negativos
        usuario.setPontuacao(-1);
        usuario.setTotalSimulacoes(-1);
        assertEquals(-1, usuario.getPontuacao());
        assertEquals(-1, usuario.getTotalSimulacoes());

        // Valores muito grandes
        usuario.setPontuacao(Integer.MAX_VALUE);
        usuario.setTotalSimulacoes(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, usuario.getPontuacao());
        assertEquals(Integer.MAX_VALUE, usuario.getTotalSimulacoes());

        // Strings null/vazias
        usuario.setLogin(null);
        usuario.setAvatar(null);
        assertNull(usuario.getLogin());
        assertNull(usuario.getAvatar());
    }

    @Test
    @DisplayName("Deve criar hash SHA-256 consistente para senhas")
    void hashSenha_Consistencia() {
        Usuario usuario1 = new Usuario("user1", "mesmaSeanha", "avatar.png");
        Usuario usuario2 = new Usuario("user2", "mesmaSeanha", "avatar.png");

        // Senha igual deve gerar hash igual
        assertEquals(usuario1.getSenhaHash(), usuario2.getSenhaHash());

        // Senhas diferentes devem gerar hashes diferentes
        Usuario usuario3 = new Usuario("user3", "senhadiferente", "avatar.png");
        assertNotEquals(usuario1.getSenhaHash(), usuario3.getSenhaHash());
    }

    @Test
    @DisplayName("Deve lidar com senhas com caracteres especiais")
    void hashSenha_CaracteresEspeciais() {
        String senhaEspecial = "!@#$%^&*()_+{}|:<>?[]\\;'\",.//";
        Usuario usuarioEspecial = new Usuario("user", senhaEspecial, "avatar.png");

        assertDoesNotThrow(() -> {
            usuarioEspecial.autenticar(senhaEspecial);
        });

        assertTrue(usuarioEspecial.autenticar(senhaEspecial));
    }

    @Test
    @DisplayName("Deve lidar com senhas unicode")
    void hashSenha_Unicode() {
        String senhaUnicode = "Olá世界🌍";
        Usuario usuarioUnicode = new Usuario("user", senhaUnicode, "avatar.png");

        assertTrue(usuarioUnicode.autenticar(senhaUnicode));
        assertFalse(usuarioUnicode.autenticar("Ola世界🌍")); // Sem acento
    }

    @Test
    @DisplayName("Deve calcular taxa de sucesso com precisão")
    void getTaxaSucesso_Precisao() {
        // Casos que podem ter problemas de precisão
        for (int i = 0; i < 7; i++) {
            usuario.incrementarSimulacaoBemSucedida();
        }
        for (int i = 0; i < 3; i++) {
            usuario.incrementarTotalSimulacoes();
        }

        // 7 sucessos de 10 total = 0.7
        double taxa = usuario.getTaxaSucesso();
        assertEquals(0.7, taxa, 0.0001);
    }

    @Test
    @DisplayName("Deve ser serializável")
    void serializable_Test() {
        // Verificar se a classe implementa Serializable corretamente
        assertTrue(usuario instanceof java.io.Serializable);

        // Verificar se tem serialVersionUID
        assertDoesNotThrow(() -> {
            Usuario.class.getDeclaredField("serialVersionUID");
        });
    }

    @Test
    @DisplayName("Deve manter estado após múltiplas operações")
    void estadoConsistente_MultiplasOperacoes() {
        // Simular um uso real
        usuario.incrementarSimulacaoBemSucedida(); // +1 sucesso, +1 total
        usuario.incrementarTotalSimulacoes(); // +1 total
        usuario.incrementarSimulacaoBemSucedida(); // +1 sucesso, +1 total
        usuario.incrementarTotalSimulacoes(); // +1 total
        usuario.incrementarTotalSimulacoes(); // +1 total

        // Estado final: 2 sucessos, 5 total
        assertEquals(2, usuario.getPontuacao());
        assertEquals(5, usuario.getTotalSimulacoes());
        assertEquals(0.4, usuario.getTaxaSucesso(), 0.0001);

        // Verificar autenticação ainda funciona
        assertTrue(usuario.autenticar("senha123"));
    }
}
