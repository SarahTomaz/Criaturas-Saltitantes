package com.example.model.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um cluster de criaturas que ocupam a mesma posição Clusters podem
 * roubar moedas e ter comportamentos coletivos
 */
public class Cluster implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Criatura> criaturas;
    private double posicaoMedia;
    private int totalMoedas;
    private int geracao;
    private boolean ativo;

    public Cluster() {
        this.criaturas = new ArrayList<>();
        this.posicaoMedia = 0.0;
        this.totalMoedas = 0;
        this.geracao = 1;
        this.ativo = true;
    }

    public Cluster(List<Criatura> criaturas) {
        this();
        for (Criatura criatura : criaturas) {
            adicionarCriatura(criatura);
        }
    }

    /**
     * Adiciona uma criatura ao cluster
     *
     * @param criatura criatura a ser adicionada
     */
    public void adicionarCriatura(Criatura criatura) {
        if (criatura != null && !criaturas.contains(criatura)) {
            criaturas.add(criatura);
            criatura.setGeracaoCluster(this.geracao);
            recalcularEstatisticas();
        }
    }

    /**
     * Remove uma criatura do cluster
     *
     * @param criatura criatura a ser removida
     * @return true se foi removida com sucesso
     */
    public boolean removerCriatura(Criatura criatura) {
        boolean removido = criaturas.remove(criatura);
        if (removido) {
            criatura.setGeracaoCluster(0);
            recalcularEstatisticas();

            // Se cluster ficou vazio, desativa
            if (criaturas.isEmpty()) {
                this.ativo = false;
            }
        }
        return removido;
    }

    /**
     * Calcula a posição média do cluster
     *
     * @return posição média das criaturas
     */
    public double calcularPosicaoMedia() {
        if (criaturas.isEmpty()) {
            return 0.0;
        }

        double soma = criaturas.stream()
                .mapToDouble(Criatura::getPosicao)
                .sum();
        this.posicaoMedia = soma / criaturas.size();
        return this.posicaoMedia;
    }

    /**
     * Tenta roubar moedas de clusters ou criaturas próximas
     *
     * @param alvos lista de possíveis alvos para roubo
     * @return quantidade total de moedas roubadas
     */
    public int roubarMoedasProximo(List<Object> alvos) {
        if (!ativo || criaturas.isEmpty()) {
            return 0;
        }

        int moedasRoubadas = 0;

        for (Object alvo : alvos) {
            if (alvo == this) {
                continue; // Não pode roubar de si mesmo
            }
            double distancia = calcularDistanciaPara(alvo);
            if (distancia <= 5.0) { // Dentro do alcance de roubo
                int moedas = tentarRoubar(alvo);
                moedasRoubadas += moedas;
            }
        }

        if (moedasRoubadas > 0) {
            distribuirMoedasRoubadas(moedasRoubadas);
        }

        return moedasRoubadas;
    }

    /**
     * Calcula distância para outro objeto (Cluster ou Criatura)
     */
    private double calcularDistanciaPara(Object alvo) {
        double posicaoAlvo;

        if (alvo instanceof Cluster) {
            posicaoAlvo = ((Cluster) alvo).getPosicaoMedia();
        } else if (alvo instanceof Criatura) {
            posicaoAlvo = ((Criatura) alvo).getPosicao();
        } else {
            return Double.MAX_VALUE;
        }

        return Math.abs(this.posicaoMedia - posicaoAlvo);
    }

    /**
     * Tenta roubar moedas de um alvo específico
     */
    private int tentarRoubar(Object alvo) {
        // Força do cluster baseada no número de criaturas e moedas totais
        double forcaCluster = criaturas.size() * 0.3 + totalMoedas * 0.1;

        if (alvo instanceof Cluster) {
            Cluster clusterAlvo = (Cluster) alvo;
            double forcaAlvo = clusterAlvo.getTamanho() * 0.3 + clusterAlvo.getTotalMoedas() * 0.1;

            if (forcaCluster > forcaAlvo) {
                int moedasRoubar = (int) Math.min(clusterAlvo.getTotalMoedas() * 0.2,
                        forcaCluster - forcaAlvo);
                return clusterAlvo.perderMoedas(moedasRoubar);
            }
        } else if (alvo instanceof Criatura) {
            Criatura criaturaAlvo = (Criatura) alvo;
            if (forcaCluster > criaturaAlvo.getMoedas() * 0.5) {
                int moedasRoubar = (int) Math.min(criaturaAlvo.getMoedas() * 0.3, forcaCluster);
                return criaturaAlvo.perderMoedas(moedasRoubar);
            }
        }

        return 0;
    }

    /**
     * Distribui moedas roubadas entre as criaturas do cluster
     */
    private void distribuirMoedasRoubadas(int moedasTotal) {
        if (criaturas.isEmpty()) {
            return;
        }

        int moedasPorCriatura = moedasTotal / criaturas.size();
        int resto = moedasTotal % criaturas.size();

        for (int i = 0; i < criaturas.size(); i++) {
            int moedas = moedasPorCriatura;
            if (i < resto) {
                moedas++; // Distribui o resto
            }
            criaturas.get(i).receberMoedas(moedas);
        }

        recalcularEstatisticas();
    }

    /**
     * Remove moedas do cluster (distribuído entre criaturas)
     */
    public int perderMoedas(int quantidade) {
        if (quantidade <= 0 || criaturas.isEmpty()) {
            return 0;
        }

        int moedasPerdidas = 0;
        int moedasPorCriatura = quantidade / criaturas.size();
        int resto = quantidade % criaturas.size();

        for (int i = 0; i < criaturas.size() && moedasPerdidas < quantidade; i++) {
            int moedasParaPerder = moedasPorCriatura;
            if (i < resto) {
                moedasParaPerder++;
            }

            moedasPerdidas += criaturas.get(i).perderMoedas(moedasParaPerder);
        }

        recalcularEstatisticas();
        return moedasPerdidas;
    }

    /**
     * Move todas as criaturas do cluster
     */
    public void moverCluster(double fatorMovimento) {
        if (!ativo) {
            return;
        }

        // Cluster se move como uma unidade, mas com pequenas variações
        double movimentoBase = (Math.random() - 0.5) * fatorMovimento * (1 + totalMoedas * 0.05);

        for (Criatura criatura : criaturas) {
            double variacaoIndividual = (Math.random() - 0.5) * 0.5;
            criatura.setPosicao(criatura.getPosicao() + movimentoBase + variacaoIndividual);
        }

        calcularPosicaoMedia();
    }

    /**
     * Recalcula todas as estatísticas do cluster
     */
    private void recalcularEstatisticas() {
        calcularPosicaoMedia();

        this.totalMoedas = criaturas.stream()
                .mapToInt(Criatura::getMoedas)
                .sum();

        // Remove criaturas inativas
        criaturas.removeIf(c -> !c.isAtiva());

        if (criaturas.isEmpty()) {
            this.ativo = false;
        }
    }

    /**
     * Verifica se o cluster deve se dividir
     */
    public List<Cluster> verificarDivisao() {
        List<Cluster> novosClusters = new ArrayList<>();

        if (criaturas.size() <= 2) {
            return novosClusters; // Muito pequeno para dividir
        }

        // Agrupa criaturas por proximidade de posição
        List<List<Criatura>> grupos = new ArrayList<>();
        double tolerancia = 2.0;

        for (Criatura criatura : criaturas) {
            boolean adicionadaAGrupo = false;

            for (List<Criatura> grupo : grupos) {
                if (grupo.get(0).estaProximoDe(criatura.getPosicao(), tolerancia)) {
                    grupo.add(criatura);
                    adicionadaAGrupo = true;
                    break;
                }
            }

            if (!adicionadaAGrupo) {
                List<Criatura> novoGrupo = new ArrayList<>();
                novoGrupo.add(criatura);
                grupos.add(novoGrupo);
            }
        }

        // Se há mais de um grupo, divide o cluster
        if (grupos.size() > 1) {
            // O maior grupo permanece neste cluster
            List<Criatura> maiorGrupo = grupos.stream()
                    .max((g1, g2) -> Integer.compare(g1.size(), g2.size()))
                    .orElse(new ArrayList<>());

            // Remove todas as criaturas e adiciona apenas o maior grupo
            this.criaturas.clear();
            this.criaturas.addAll(maiorGrupo);

            // Cria novos clusters para os outros grupos
            for (List<Criatura> grupo : grupos) {
                if (grupo != maiorGrupo && grupo.size() > 0) {
                    Cluster novoCluster = new Cluster(grupo);
                    novoCluster.setGeracao(this.geracao + 1);
                    novosClusters.add(novoCluster);
                }
            }
        }

        recalcularEstatisticas();
        return novosClusters;
    }

    // Getters e Setters
    public List<Criatura> getCriaturas() {
        return new ArrayList<>(criaturas);
    }

    public double getPosicaoMedia() {
        return posicaoMedia;
    }

    public int getTotalMoedas() {
        return totalMoedas;
    }

    public int getTamanho() {
        return criaturas.size();
    }

    public int getGeracao() {
        return geracao;
    }

    public void setGeracao(int geracao) {
        this.geracao = geracao;
    }

    public boolean isAtivo() {
        return ativo && !criaturas.isEmpty();
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    /**
     * Retorna estatísticas do cluster
     */
    public String getEstatisticas() {
        return String.format("Cluster[criaturas=%d, pos=%.2f, moedas=%d, ger=%d]",
                criaturas.size(), posicaoMedia, totalMoedas, geracao);
    }

    @Override
    public String toString() {
        return getEstatisticas();
    }

    public String getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
