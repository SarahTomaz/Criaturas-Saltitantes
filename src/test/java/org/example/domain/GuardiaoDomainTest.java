package org.example.domain;

import org.example.model.GuardiaoHorizonte;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GuardiaoDomainTest {

    @Test
    @DisplayName("Deve criar guardião com posição e moedas iniciais corretas")
    void criarGuardiao_ValoresIniciais() {
        GuardiaoHorizonte guardiao = new GuardiaoHorizonte(50.0);

        assertAll(
                () -> assertEquals(50.0, guardiao.getPosicao(), 0.001),
                () -> assertEquals(0, guardiao.getMoedas())
        );
    }

    @Test
    @DisplayName("Deve coletar moedas corretamente")
    void coletarMoedas() {
        GuardiaoHorizonte guardiao = new GuardiaoHorizonte(50.0);
        int moedasIniciais = guardiao.getMoedas();

        // Simula coleta de moedas somando ao valor inicial
        guardiao.setMoedas(moedasIniciais + 500_000);

        assertEquals(500_000, guardiao.getMoedas());
    }

    @Test
    @DisplayName("Não deve permitir moedas negativas")
    void setMoedas_Negativas() {
        GuardiaoHorizonte guardiao = new GuardiaoHorizonte(50.0);

        assertThrows(IllegalArgumentException.class,
                () -> guardiao.setMoedas(-100));
    }
}
