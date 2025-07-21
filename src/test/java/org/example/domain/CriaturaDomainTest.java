package org.example.domain;

import org.example.model.Criatura;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testes de domínio para a classe Criatura.
 *
 * <p>
 * Verifica o comportamento esperado das criaturas de acordo com as regras de
 * negócio.</p>
 *
 * <p>
 * <b>Cenários testados:</b></p>
 * <ul>
 * <li>Criação de criatura com valores válidos</li>
 * <li>Criação de criatura com valores inválidos</li>
 * <li>Movimentação da criatura</li>
 * <li>Gerenciamento de moedas</li>
 * <li>Estado de ativação</li>
 * </ul>
 */
class CriaturaDomainTest {

    /**
     * Testa a criação de uma criatura com parâmetros válidos. Verifica se os
     * valores iniciais estão corretos (1.000.000 de moedas).
     */
    @Test
    @DisplayName("Deve criar criatura com valores válidos e 1.000.000 de moedas")
    void criarCriatura_ValoresValidos() {
        Criatura criatura = new Criatura(1, 50.0);

        assertAll(
                () -> assertEquals(1, criatura.getId()),
                () -> assertEquals(50.0, criatura.getPosicao(), 0.001),
                () -> assertEquals(1_000_000, criatura.getMoedas()), // Verifica correção
                () -> assertTrue(criatura.isAtiva())
        );
    }

    /**
     * Testa a criação de criatura com ID inválido. Deve lançar
     * IllegalArgumentException.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("Deve lançar exceção ao criar criatura com ID inválido")
    void criarCriatura_IdInvalido(int idInvalido) {
        assertThrows(IllegalArgumentException.class,
                () -> new Criatura(idInvalido, 50.0));
    }

    // outros testes de domínios serão adicionados aqui
}
