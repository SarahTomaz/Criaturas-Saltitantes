package com.example;

import com.example.ui.SimulacaoCriaturasSaltitantes;
import org.junit.jupiter.api.*;

import javax.swing.*;


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
    public void avancarIteracaoPosicoesNull() {
        this.sim.setPosicoes(null);
        Assertions.assertThrows(AssertionError.class, () -> sim.avancarIteracao());
    }

    // Por ser randomico, o que pode ser feito, é: sem uma seed para o random
    // testar se as posições e moedas estão diferentes
    @Test
    public void avancarIteracaValoresRandomicosSemSeed() {
        int[] moedas = new int[]{1_000_000, 1_000_000, 1_000_000};
        double[] posicoes = new double[]{0.5, 0.5, 0,5};
        var txt = this.sim.getTxtNumCriaturas();
        txt.setText("3");
        this.sim.setTxtNumCriaturas(txt);
        this.sim.iniciarSimulacao();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        this.sim.pausarSimulacao();

        Assertions.assertNotEquals(moedas[0], this.sim.getMoedas()[0]);
        Assertions.assertNotEquals(moedas[1], this.sim.getMoedas()[1]);
        Assertions.assertNotEquals(moedas[2], this.sim.getMoedas()[2]);

        Assertions.assertNotEquals(posicoes[0], this.sim.getPosicoes()[0]);
        Assertions.assertNotEquals(posicoes[1], this.sim.getPosicoes()[1]);
        Assertions.assertNotEquals(posicoes[2], this.sim.getPosicoes()[2]);
    }

    @Test
    public void avancarIteracaValoresRandomicosComSeed() {
        int[] moedas = new int[]{2000000, 375000, 625000};
        double[] posicoes = new double[]{1.0836962141665683, 0.5636866822956048, 0.15312174168903459};
        var txt = this.sim.getTxtNumCriaturas();
        txt.setText("3");
        this.sim.setRadomSeed(1);
        this.sim.setTxtNumCriaturas(txt);
        this.sim.iniciarSimulacao();
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        this.sim.pausarSimulacao();

        Assertions.assertEquals(2, this.sim.getIteracaoAtual());
        Assertions.assertEquals(moedas[0], this.sim.getMoedas()[0]);
        Assertions.assertEquals(moedas[1], this.sim.getMoedas()[1]);
        Assertions.assertEquals(moedas[2], this.sim.getMoedas()[2]);

        Assertions.assertEquals(posicoes[0], this.sim.getPosicoes()[0]);
        Assertions.assertEquals(posicoes[1], this.sim.getPosicoes()[1]);
        Assertions.assertEquals(posicoes[2], this.sim.getPosicoes()[2]);
    }

    @Test
    public void pauseSimulacaoEstadosEsperados() {
        this.sim.pausarSimulacao();
        Assertions.assertTrue(this.sim.getBtnIniciar().isEnabled());
        Assertions.assertFalse(this.sim.getTimer().isRunning());
        Assertions.assertFalse(this.sim.getBtnPausar().isEnabled());
    }

    @Test
    public void reiniciarSimulacaoEstadosEsperados() {
        this.sim.reiniciarSimulacao();
        Assertions.assertEquals(0, this.sim.getIteracaoAtual());
        Assertions.assertTrue(this.sim.getBtnIniciar().isEnabled());
        Assertions.assertFalse(this.sim.getTimer().isRunning());
        Assertions.assertFalse(this.sim.getBtnPausar().isEnabled());
    }

    @Test
    public void iniciarSimulacaoComTimerJaRodando() {
        this.sim.setPosicoes(new double[]{0,0,0,0});
        this.sim.getTimer().start();
        Assertions.assertEquals(-1, this.sim.iniciarSimulacao());
        Assertions.assertTrue(this.sim.getTimer().isRunning());
    }

    @Test
    public void iniciarSimulacaoCorretamenteEstadosEsperados() {
        this.sim.setPosicoes(new double[]{0,0,0,0});
        Assertions.assertEquals(0, this.sim.iniciarSimulacao());
        Assertions.assertTrue(this.sim.getTimer().isRunning());
    }

    @Test
    public void encontrarCriaturasProximaNumCriaturasInvalido() {
        this.sim.setNumCriaturas(0);
        Assertions.assertEquals(-1, this.sim.encontrarCriaturaMaisProxima(0));
    }

    @Test
    public void encontrarCriaturassProximasValido() {
        this.sim.setPosicoes(new double[]{0.5, 1, 0.6});
        this.sim.setNumCriaturas(3);
        Assertions.assertEquals(2, this.sim.encontrarCriaturaMaisProxima(0));
        Assertions.assertEquals(2, this.sim.encontrarCriaturaMaisProxima(1));
        Assertions.assertEquals(0, this.sim.encontrarCriaturaMaisProxima(2));
    }


}