package org.example.model;

import java.util.Random;

public class Criatura {

    private int id;
    private double posicao;
    private int moedas;
    private boolean ativa;
    private static final Random random = new Random();

    public Criatura(int id, double posicao, int moedas) {
        this.id = id;
        this.posicao = posicao;
        this.moedas = moedas;
        this.ativa = true;
    }

    public void mover() {
        if (ativa) {
            double r = (random.nextDouble() * 2) - 1; // Valor entre -1 e 1
            posicao = posicao + (r * moedas);
        }
    }

    public void adicionarMoedas(int quantidade) {
        this.moedas += quantidade;
    }

    public int removerMoedas(int quantidade) {
        int moedasRemovidas = Math.min(quantidade, this.moedas);
        this.moedas -= moedasRemovidas;
        return moedasRemovidas;
    }

    public void desativar() {
        this.ativa = false;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

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

    public boolean isAtiva() {
        return ativa;
    }

    @Override
    public String toString() {
        return String.format("Criatura[id=%d, pos=%.2f, moedas=%d, ativa=%s]",
                id, posicao, moedas, ativa);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Criatura criatura = (Criatura) obj;
        return id == criatura.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
