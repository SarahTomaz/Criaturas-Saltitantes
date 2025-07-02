package com.example.model.entities;

import java.awt.Color;
import java.io.Serializable;

/**
 * Representa uma criatura na simulação Cada criatura tem posição, moedas e
 * comportamentos específicos
 */
public class Criatura implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private double posicao;
    private int moedas;
    private Color cor;
    private boolean ativa;
    private int geracaoCluster;

    public Criatura(int id, double posicao, int moedas) {
        this.id = id;
        this.posicao = posicao;
        this.moedas = moedas;
        this.ativa = true;
        this.geracaoCluster = 0;
        this.cor = gerarCorAleatoria();
    }

    /**
     * Move a criatura baseado no fator de movimento
     *
     * @param fatorMovimento valor que influencia o movimento
     */
    public void mover(double fatorMovimento) {
        if (!ativa) {
            return;
        }

        // Lógica de movimento: criatura se move baseada em suas moedas e fator
        double movimento = (Math.random() - 0.5) * fatorMovimento * (1 + moedas * 0.1);
        posicao += movimento;

        // Garantir que não saia dos limites (0 a 100)
        posicao = Math.max(0, Math.min(100, posicao));
    }

    /**
     * Adiciona moedas à criatura
     *
     * @param quantidade número de moedas a serem adicionadas
     */
    public void receberMoedas(int quantidade) {
        if (quantidade > 0) {
            this.moedas += quantidade;
            atualizarCor();
        }
    }

    /**
     * Remove moedas da criatura
     *
     * @param quantidade número de moedas a serem removidas
     * @return quantidade efetivamente removida
     */
    public int perderMoedas(int quantidade) {
        if (quantidade <= 0) {
            return 0;
        }

        int moedasPerdidas = Math.min(this.moedas, quantidade);
        this.moedas -= moedasPerdidas;
        atualizarCor();

        // Se perdeu todas as moedas, criatura pode ficar inativa
        if (this.moedas == 0) {
            this.ativa = Math.random() > 0.3; // 30% chance de ficar inativa
        }

        return moedasPerdidas;
    }

    /**
     * Verifica se a criatura está próxima de outra posição
     *
     * @param outraPosicao posição para comparar
     * @param tolerancia margem de proximidade
     * @return true se estão próximas
     */
    public boolean estaProximoDe(double outraPosicao, double tolerancia) {
        return Math.abs(this.posicao - outraPosicao) <= tolerancia;
    }

    /**
     * Atualiza a cor da criatura baseada na quantidade de moedas
     */
    private void atualizarCor() {
        if (moedas == 0) {
            cor = Color.GRAY;
        } else if (moedas < 5) {
            cor = Color.RED;
        } else if (moedas < 10) {
            cor = Color.ORANGE;
        } else if (moedas < 20) {
            cor = Color.YELLOW;
        } else {
            cor = Color.GREEN;
        }
    }

    /**
     * Gera uma cor aleatória para a criatura
     */
    private Color gerarCorAleatoria() {
        return new Color(
                (int) (Math.random() * 256),
                (int) (Math.random() * 256),
                (int) (Math.random() * 256)
        );
    }

    /**
     * Clona a criatura (útil para testes e simulações)
     */
    public Criatura clonar() {
        Criatura clone = new Criatura(this.id, this.posicao, this.moedas);
        clone.cor = this.cor;
        clone.ativa = this.ativa;
        clone.geracaoCluster = this.geracaoCluster;
        return clone;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPosicao() {
        return posicao;
    }

    public void setPosicao(double posicao) {
        this.posicao = Math.max(0, Math.min(100, posicao));
    }

    public int getMoedas() {
        return moedas;
    }

    public void setMoedas(int moedas) {
        this.moedas = Math.max(0, moedas);
        atualizarCor();
    }

    public Color getCor() {
        return cor;
    }

    public void setCor(Color cor) {
        this.cor = cor;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public int getGeracaoCluster() {
        return geracaoCluster;
    }

    public void setGeracaoCluster(int geracaoCluster) {
        this.geracaoCluster = geracaoCluster;
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
