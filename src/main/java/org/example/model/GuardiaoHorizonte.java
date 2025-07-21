package org.example.model;

import java.util.Random;

public class GuardiaoHorizonte {

    private double posicao;
    private int moedas;
    private static final Random random = new Random();

    public GuardiaoHorizonte(double posicaoInicial) {
        this.posicao = posicaoInicial;
        this.moedas = 0;
    }

    public void mover() {
        double r = (random.nextDouble() * 2) - 1; // Valor entre -1 e 1
        posicao = posicao + (r * moedas);
    }

    public void eliminarCluster(Cluster cluster) {
        this.moedas += cluster.getTotalMoedas();
    }

    public void eliminarCriatura(Criatura criatura) {
        this.moedas += criatura.getMoedas();
        criatura.desativar();
    }

    public boolean temMaisMoedasQue(Criatura criatura) {
        return this.moedas > criatura.getMoedas();
    }

    public boolean temMaisMoedasQue(Cluster cluster) {
        return this.moedas > cluster.getTotalMoedas();
    }

    // Getters e Setters
    public double getPosicao() {
        return posicao;
    }

    public void setPosicao(double posicao) {
        this.posicao = posicao;
    }

    public int getMoedas() {
        return moedas;
    }

    public void setMoedas(int moedas) {
        this.moedas = moedas;
    }

    @Override
    public String toString() {
        return String.format("Guardião[pos=%.2f, moedas=%d]", posicao, moedas);
    }
}
