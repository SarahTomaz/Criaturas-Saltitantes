package org.example.model;

import java.util.Random;

/**
 * Representa uma criatura na simulação com posição, moedas e estado.
 *
 * <p>
 * <b>Invariantes:</b></p>
 * <ul>
 * <li>id deve ser positivo</li>
 * <li>posicao deve estar entre 0 e 100</li>
 * <li>moedas deve ser 1.000.000 no início (conforme especificação)</li>
 * <li>ativa deve refletir corretamente o estado da criatura</li>
 * </ul>
 */
public class Criatura {

    private final int id;
    private double posicao;
    private int moedas;
    private boolean ativa;
    private static final Random random = new Random();

    /**
     * Cria uma nova criatura com 1.000.000 de moedas (conforme especificação).
     *
     * @param id Identificador único da criatura (deve ser positivo)
     * @param posicao Posição inicial no horizonte (0-100)
     * @throws IllegalArgumentException se id ≤ 0 ou posicao fora do intervalo
     * [0,100]
     */
    public Criatura(int id, double posicao) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo");
        }
        if (posicao < 0 || posicao > 100) {
            throw new IllegalArgumentException("Posição deve estar entre 0 e 100");
        }

        this.id = id;
        this.posicao = posicao;
        this.moedas = 1_000_000; // Corrigido conforme especificação
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
