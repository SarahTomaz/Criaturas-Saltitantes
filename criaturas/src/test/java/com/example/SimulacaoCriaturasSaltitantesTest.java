package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimulacaoCriaturasSaltitantesTest {
    com.example.SimulacaoCriaturasSaltitantes sim;

    @BeforeEach
    void setUp() throws Exception {
        this.sim = new com.example.SimulacaoCriaturasSaltitantes();
    }
    @Test
    public void inicializarComNumeroNegativo() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {sim.inicializarSimulacao(-1);});
    }

    @Test
    public void avancarIteracaoPosicoesNotNUll() {
        this.sim.setPosicoes(null);
        Assertions.assertThrows(AssertionError.class, () -> sim.avancarIteracao());
    }

}
