package org.example.domain;

import org.example.model.Usuario;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testes de domínio para a classe Usuario.
 *
 * <p>
 * Verifica as regras de negócio relacionadas a:</p>
 * <ul>
 * <li>Criação de usuários</li>
 * <li>Validação de credenciais</li>
 * <li>Atualização de pontuação</li>
 * <li>Taxa de sucesso</li>
 * </ul>
 */
class UsuarioDomainTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("test_user", "password123", "avatar.png");
    }

    @Test
    @DisplayName("Deve criar usuário com dados válidos")
    void criarUsuario_DadosValidos() {
        assertAll(
                () -> assertEquals("test_user", usuario.getLogin()),
                () -> assertEquals("avatar.png", usuario.getAvatar()),
                () -> assertEquals(0, usuario.getPontuacao()),
                () -> assertEquals(0, usuario.getTotalSimulacoes()),
                () -> assertEquals(0.0, usuario.getTaxaSucesso())
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("Deve lançar exceção com login inválido")
    void criarUsuario_LoginInvalido(String loginInvalido) {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario(loginInvalido, "password123", "avatar.png"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n", "123", "abc"})
    @DisplayName("Deve lançar exceção com senha inválida")
    void criarUsuario_SenhaInvalida(String senhaInvalida) {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("valid_user", senhaInvalida, "avatar.png"));
    }

    @Test
    @DisplayName("Deve incrementar pontuação quando simulação é bem-sucedida")
    void incrementarPontuacao_SimulacaoBemSucedida() {
        usuario.incrementarPontuacao();
        assertEquals(1, usuario.getPontuacao());
        assertEquals(1, usuario.getTotalSimulacoes());
    }

    @Test
    @DisplayName("Deve incrementar apenas total de simulações quando não bem-sucedida")
    void incrementarTotalSimulacoes_NaoBemSucedida() {
        usuario.incrementarTotalSimulacoes();
        assertEquals(0, usuario.getPontuacao());
        assertEquals(1, usuario.getTotalSimulacoes());
    }

    @ParameterizedTest
    @CsvSource({
        "5, 10, 0.5",
        "3, 3, 1.0",
        "0, 5, 0.0",
        "0, 0, 0.0"
    })
    @DisplayName("Deve calcular corretamente a taxa de sucesso")
    void calcularTaxaSucesso(int bemSucedidas, int total, double taxaEsperada) {
        Usuario user = new Usuario("user1", "senha", "avatar.png");
        // Adiciona simulações bem-sucedidas
        for (int i = 0; i < bemSucedidas; i++) {
            user.incrementarPontuacao(); // incrementa pontuação e total
        }
        // Adiciona simulações falhadas (apenas incrementa o total)
        for (int i = bemSucedidas; i < total; i++) {
            user.incrementarTotalSimulacoes();
        }

        assertEquals(taxaEsperada, user.getTaxaSucesso(), 0.001);
    }

    @Test
    @DisplayName("Deve validar credenciais corretamente")
    void validarCredenciais() {
        assertTrue(usuario.validarCredenciais("password123"));
        assertFalse(usuario.validarCredenciais("senha_errada"));
        assertFalse(usuario.validarCredenciais(null));
    }
}
