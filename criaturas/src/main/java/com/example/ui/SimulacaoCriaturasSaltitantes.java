package com.example.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SimulacaoCriaturasSaltitantes extends JFrame {

    private static final int LARGURA_JANELA = 1000;
    private static final int ALTURA_JANELA = 600;
    private static final int ALTURA_GRAFICO = 400;
    private static final int TAMANHO_CRIATURA = 20;
    private static final int DELAY_SIMULACAO = 500;
    private static final double FATOR_ESCALA = LARGURA_JANELA * 0.8;
    private static final double POSICAO_INICIAL_X = 0.5;

    private int numCriaturas;
    private int iteracaoAtual = 0;
    private double[] posicoes; // xi - posição no horizonte
    private int[] moedas; // gi - quantidade de moedas
    private Color[] cores;
    private Random random = new Random();
    private Timer timer;
    private JButton btnIniciar, btnPausar, btnReiniciar;
    private JLabel lblInfo;
    private JPanel painelControle, painelGrafico;
    private JTextField txtNumCriaturas;

    public SimulacaoCriaturasSaltitantes() {
        setTitle("Simulação de Criaturas Saltitantes");
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de controle
        painelControle = new JPanel();
        painelControle.setLayout(new FlowLayout());

        JLabel lblNumCriaturas = new JLabel("Número de criaturas:");
        txtNumCriaturas = new JTextField("10", 5);
        btnIniciar = new JButton("Iniciar");
        btnPausar = new JButton("Pausar");
        btnReiniciar = new JButton("Reiniciar");
        lblInfo = new JLabel("Iteração: 0");

        btnPausar.setEnabled(false);

        painelControle.add(lblNumCriaturas);
        painelControle.add(txtNumCriaturas);
        painelControle.add(btnIniciar);
        painelControle.add(btnPausar);
        painelControle.add(btnReiniciar);
        painelControle.add(lblInfo);

        painelGrafico = new PainelGrafico();
        painelGrafico.setPreferredSize(new Dimension(LARGURA_JANELA, ALTURA_GRAFICO));
        painelGrafico.setBackground(Color.WHITE);

        add(painelControle, BorderLayout.NORTH);
        add(painelGrafico, BorderLayout.CENTER);

        btnIniciar.addActionListener(e -> iniciarSimulacao());
        btnPausar.addActionListener(e -> pausarSimulacao());
        btnReiniciar.addActionListener(e -> reiniciarSimulacao());

        // Timer para a simulação
        timer = new Timer(DELAY_SIMULACAO, e -> avancarIteracao());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void inicializarSimulacao() {
        try {
            numCriaturas = Integer.parseInt(txtNumCriaturas.getText().trim());
            if (numCriaturas <= 0) {
                JOptionPane.showMessageDialog(this, "O número de criaturas deve ser positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um número válido.");
            return;
        }

        posicoes = new double[numCriaturas];
        moedas = new int[numCriaturas];
        cores = new Color[numCriaturas];

        // Inicializar criaturas
        for (int i = 0; i < numCriaturas; i++) {
            posicoes[i] = POSICAO_INICIAL_X;
            moedas[i] = 1_000_000;
            cores[i] = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }

        iteracaoAtual = 0;
        lblInfo.setText("Iteração: " + iteracaoAtual);
        repaint();
    }

    private void iniciarSimulacao() {
        if (!timer.isRunning()) {
            if (posicoes == null) {
                inicializarSimulacao();
            }
            timer.start();
            btnIniciar.setEnabled(false);
            btnPausar.setEnabled(true);
        }
    }

    private void pausarSimulacao() {
        timer.stop();
        btnIniciar.setEnabled(true);
        btnPausar.setEnabled(false);
    }

    private void reiniciarSimulacao() {
        timer.stop();
        inicializarSimulacao();
        btnIniciar.setEnabled(true);
        btnPausar.setEnabled(false);
    }

    private void avancarIteracao() {
        assert posicoes != null;

        iteracaoAtual++;
        lblInfo.setText("Iteração: " + iteracaoAtual);

        // Processar cada criatura na ordem
        for (int i = 0; i < numCriaturas; i++) {
            // Computar novo lugar no horizonte
            double r = random.nextDouble() * 2 - 1; // Número aleatório entre -1 e 1
            posicoes[i] = posicoes[i] + r * moedas[i] / 1_000_000.0; // Normalizando o salto para evitar valores muito grandes

            // Encontrar a criatura mais próxima
            int indiceProximo = encontrarCriaturaMaisProxima(i);

            // Roubar metade das moedas
            if (indiceProximo != -1) {
                int moedasRoubadas = moedas[indiceProximo] / 2;
                moedas[indiceProximo] -= moedasRoubadas;
                moedas[i] += moedasRoubadas;
            }
        }

        repaint();
    }

    private int encontrarCriaturaMaisProxima(int indice) {
        if (numCriaturas <= 1) {
            return -1;
        }

        double posicaoAtual = posicoes[indice];
        double menorDistancia = Double.MAX_VALUE;
        int indiceMaisProximo = -1;

        for (int j = 0; j < numCriaturas; j++) {
            if (j != indice) {
                double distancia = Math.abs(posicoes[j] - posicaoAtual);
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    indiceMaisProximo = j;
                }
            }
        }

        return indiceMaisProximo;
    }

    private class PainelGrafico extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (posicoes == null) {
                return;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.BLACK);
            g2d.drawLine(50, ALTURA_GRAFICO - 50, LARGURA_JANELA - 50, ALTURA_GRAFICO - 50);

            // Obter limites mínimo e máximo das posições
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (double pos : posicoes) {
                min = Math.min(min, pos);
                max = Math.max(max, pos);
            }

            double escala = FATOR_ESCALA / Math.max(1.0, max - min);
            double offset = -min * escala + 50;

            for (int i = 0; i < numCriaturas; i++) {
                g2d.setColor(cores[i]);
                int x = (int) (posicoes[i] * escala + offset);
                int y = ALTURA_GRAFICO - 50 - TAMANHO_CRIATURA;

                int tamanho = TAMANHO_CRIATURA + (int) (10 * Math.log10(moedas[i]) / 6.0);

                g2d.fillOval(x - tamanho / 2, y - tamanho / 2, tamanho, tamanho);
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(i + 1), x - 5, y - tamanho / 2 - 5);

                String infoMoedas = String.format("%,d", moedas[i]);
                g2d.drawString(infoMoedas, x - 30, y + tamanho / 2 + 15);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimulacaoCriaturasSaltitantes::new);
    }
}
