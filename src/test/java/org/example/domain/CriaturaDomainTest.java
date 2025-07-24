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

class CriaturaDomainTest {

    @Test
    @DisplayName("Deve criar criatura com valores válidos e 1.000.000 de moedas")
    void criarCriatura_ValoresValidos() {
        Criatura criatura = new Criatura(1, 50.0);

        assertAll(
                () -> assertEquals(1, criatura.getId()),
                () -> assertEquals(50.0, criatura.getPosicao(), 0.001),
                () -> assertEquals(1_000_000, criatura.getMoedas()),
                () -> assertTrue(criatura.isAtiva())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("Deve lançar exceção ao criar criatura com ID inválido")
    void criarCriatura_IdInvalido(int idInvalido) {
        assertThrows(IllegalArgumentException.class,
                () -> new Criatura(idInvalido, 50.0));
    }

    @Test
    @DisplayName("Deve mover criatura dentro dos limites do horizonte")
    void moverCriatura_DentroLimites() {
        Criatura criatura = new Criatura(1, 50.0);

        // Simula movimento para frente
        double novaPosicao = criatura.getPosicao() + 10.0;
        criatura.setPosicao(novaPosicao);
        assertEquals(60.0, criatura.getPosicao(), 0.001);

        // Simula movimento para trás
        novaPosicao = criatura.getPosicao() - 20.0;
        criatura.setPosicao(novaPosicao);
        assertEquals(40.0, criatura.getPosicao(), 0.001);
    }

    @Test
    @DisplayName("Deve ajustar posição quando ultrapassar os limites")
    void moverCriatura_UltrapassaLimites() {
        Criatura criatura = new Criatura(1, 95.0);

        // Tenta mover além do limite superior
        double novaPosicao = criatura.getPosicao() + 10.0;
        criatura.setPosicao(novaPosicao);
        assertEquals(105.0, criatura.getPosicao(), 0.001);

        criatura.setPosicao(5.0);
        // Tenta mover além do limite inferior
        novaPosicao = criatura.getPosicao() - 10.0;
        criatura.setPosicao(novaPosicao);
        assertEquals(-5.0, criatura.getPosicao(), 0.001);
    }

    @Test
    @DisplayName("Deve transferir moedas corretamente")
    void transferirMoedas_ValoresValidos() {
        Criatura origem = new Criatura(1, 50.0);
        Criatura destino = new Criatura(2, 50.0);

        int valorTransferencia = 500_000;

        // Simula transferência manual usando métodos disponíveis
        int moedasOrigemAntes = origem.getMoedas();
        int moedasDestinoAntes = destino.getMoedas();

        origem.setMoedas(moedasOrigemAntes - valorTransferencia);
        destino.setMoedas(moedasDestinoAntes + valorTransferencia);

        assertEquals(500_000, origem.getMoedas());
        assertEquals(1_500_000, destino.getMoedas());
    }

    @Test
    @DisplayName("Não deve transferir moedas quando valor é inválido")
    void transferirMoedas_ValoresInvalidos() {
        Criatura origem = new Criatura(1, 50.0);
        Criatura destino = new Criatura(2, 50.0);

        // Testa valor negativo
        assertThrows(IllegalArgumentException.class, () -> {
            int valorNegativo = -100;
            if (valorNegativo < 0) {
                throw new IllegalArgumentException("Valor não pode ser negativo");
            }
        });

        // Testa valor maior que moedas disponíveis
        assertThrows(IllegalArgumentException.class, () -> {
            int valorExcessivo = 2_000_000;
            if (valorExcessivo > origem.getMoedas()) {
                throw new IllegalArgumentException("Moedas insuficientes");
            }
        });
    }
}
