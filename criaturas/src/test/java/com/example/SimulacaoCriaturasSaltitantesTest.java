package com.example;

import com.example.ui.SimulacaoCriaturasSaltitantes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class SimulacaoCriaturasSaltitantesTest {
    SimulacaoCriaturasSaltitantes sim;

    @BeforeEach
    void setUp() throws Exception {
        this.sim = new SimulacaoCriaturasSaltitantes();
    }


    @Test
    public void inicializarComNumeroNegativo() {
        JTextField field = this.sim.getTxtNumCriaturas();
        field.setText("-1");
        this.sim.setTxtNumCriaturas(field);
        Assertions.assertThrows(IllegalArgumentException.class, () -> sim.inicializarSimulacao());
    }

    @Test
    public void inicializarComNumeroZero() {
        JTextField field = this.sim.getTxtNumCriaturas();
        field.setText("0");
        this.sim.setTxtNumCriaturas(field);
        Assertions.assertThrows(IllegalArgumentException.class, () -> sim.inicializarSimulacao());
    }

    @Test
    public void inicializarComNan() {
        JTextField field = this.sim.getTxtNumCriaturas();
        field.setText("NaN");
        this.sim.setTxtNumCriaturas(field);
        Assertions.assertThrows(NumberFormatException.class, () -> sim.inicializarSimulacao());
    }

    @Test
    public void inicializarComNumeroValido() {
        JTextField field = this.sim.getTxtNumCriaturas();
        field.setText("10");
        this.sim.setTxtNumCriaturas(field);
        this.sim.inicializarSimulacao();

        // cada array deve ter tamanho igual ao numero de criaturas
        // já que cada criatura tem uma cor, numero de moedas e posicoes.
        Assertions.assertEquals(10, sim.getNumCriaturas());
        Assertions.assertEquals(10, sim.getCores().length);
        Assertions.assertEquals(10, sim.getPosicoes().length);
        Assertions.assertEquals(10, sim.getMoedas().length);
        Assertions.assertEquals(0, sim.getIteracaoAtual());
    }

    @Test
    public void avancarIteracaoPosicoesNotNUll() {
        this.sim.setPosicoes(null);
        Assertions.assertThrows(AssertionError.class, () -> sim.avancarIteracao());
    }


    @Test
    public void avancarIteracaoPosicoes() {
        this.sim.setPosicoes(null);
        Assertions.assertThrows(AssertionError.class, () -> sim.avancarIteracao());
    }

    @Test
    public void avancarIteracaZeroPosicoes() {
   //     double[] posicoes;

    }

    @Test
    public void pauseExecucaoEstadosEsperados() {
        this.sim.pausarSimulacao();
        assertTrue(this.sim.getBtnIniciar().isEnabled());
        assertFalse(this.sim.getTimer().isRunning());
        assertFalse(this.sim.getBtnPausar().isEnabled());
    }

}