package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cluster {

    private List<Criatura> criaturas;
    private double posicao;
    private int totalMoedas;
    private int id;
    private static int contadorId = 1000; // IDs começam em 1000 para diferenciar
    private static final Random random = new Random();

    public Cluster(Criatura criatura1, Criatura criatura2) {
        this.id = contadorId++;
        this.criaturas = new ArrayList<>();
        this.criaturas.add(criatura1);
        this.criaturas.add(criatura2);
        this.posicao = criatura1.getPosicao(); // Mesma posição onde se encontraram
        this.totalMoedas = criatura1.getMoedas() + criatura2.getMoedas();

        // Desativar as criaturas originais
        criatura1.desativar();
        criatura2.desativar();
    }

    public void adicionarCriatura(Criatura criatura) {
        this.criaturas.add(criatura);
        this.totalMoedas += criatura.getMoedas();
        criatura.desativar();
    }

    public void mover() {
        double r = (random.nextDouble() * 2) - 1; // Valor entre -1 e 1
        posicao = posicao + (r * totalMoedas);
    }

    public void roubarMoedasDeVizinho(Criatura vizinho) {
        if (vizinho != null && vizinho.isAtiva()) {
            int metadeMoedas = vizinho.getMoedas() / 2;
            int moedasRoubadas = vizinho.removerMoedas(metadeMoedas);
            this.totalMoedas += moedasRoubadas;
        }
    }

    public void roubarMoedasDeVizinho(Cluster vizinho) {
        if (vizinho != null) {
            int metadeMoedas = vizinho.getTotalMoedas() / 2;
            vizinho.totalMoedas -= metadeMoedas;
            this.totalMoedas += metadeMoedas;
        }
    }

    // Getters
    public List<Criatura> getCriaturas() {
        return new ArrayList<>(criaturas);
    }

    public double getPosicao() {
        return posicao;
    }

    public void setPosicao(double posicao) {
        this.posicao = posicao;
    }

    public int getTotalMoedas() {
        return totalMoedas;
    }

    public void setTotalMoedas(int totalMoedas) {
        this.totalMoedas = totalMoedas;
    }

    public int getId() {
        return id;
    }

    public int getTamanho() {
        return criaturas.size();
    }

    @Override
    public String toString() {
        return String.format("Cluster[id=%d, pos=%.2f, moedas=%d, criaturas=%d]",
                id, posicao, totalMoedas, criaturas.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Cluster cluster = (Cluster) obj;
        return id == cluster.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
