package com.example.model.entities;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

/**
 * Representa o Guardião do Horizonte que patrulha e elimina clusters O guardião
 * tem comportamentos especiais e condições de vitória
 */
public class GuardiaoHorizonte implements Serializable {

    private static final long serialVersionUID = 1L;

    private double posicao;
    private int moedas;
    private double velocidade;
    private boolean ativo;
    private int clustersEliminados;
    private int iteracoesVivo;
    private MovimentoPatrulha tipoMovimento;
    private double limiteInferior;
    private double limiteSuperior;
    private boolean vitoriosa;

    // Enum para tipos de movimento do guardião
    public enum MovimentoPatrulha {
        ALEATORIO,
        PATRULHA_LINEAR,
        BUSCA_CLUSTERS,
        DEFENSIVO
    }

    public GuardiaoHorizonte(double posicaoInicial) {
        this.posicao = posicaoInicial;
        this.moedas = 0;
        this.velocidade = 2.0;
        this.ativo = true;
        this.clustersEliminados = 0;
        this.iteracoesVivo = 0;
        this.tipoMovimento = MovimentoPatrulha.PATRULHA_LINEAR;
        this.limiteInferior = 80.0;
        this.limiteSuperior = 100.0;
        this.vitoriosa = false;
    }

    /**
     * Move o guardião baseado no seu tipo de movimento atual
     *
     * @param clusters lista de clusters para análise de movimento
     * @param criaturas lista de criaturas individuais
     */
    public void mover(List<Cluster> clusters, List<Criatura> criaturas) {
        if (!ativo) {
            return;
        }

        iteracoesVivo++;

        switch (tipoMovimento) {
            case ALEATORIO:
                moverAleatorio();
                break;
            case PATRULHA_LINEAR:
                moverPatrulhaLinear();
                break;
            case BUSCA_CLUSTERS:
                moverBuscaClusters(clusters, criaturas);
                break;
            case DEFENSIVO:
                moverDefensivo(clusters, criaturas);
                break;
        }

        // Adapta estratégia baseada na situação
        adaptarEstrategia(clusters, criaturas);

        // Garante que não saia dos limites
        posicao = Math.max(0, Math.min(100, posicao));
    }

    /**
     * Movimento aleatório do guardião
     */
    private void moverAleatorio() {
        double movimento = (Math.random() - 0.5) * velocidade * 2;
        posicao += movimento;
    }

    /**
     * Movimento de patrulha linear entre limites
     */
    private void moverPatrulhaLinear() {
        // Move em direção aos limites da área de patrulha
        if (posicao <= limiteInferior) {
            posicao += velocidade;
        } else if (posicao >= limiteSuperior) {
            posicao -= velocidade;
        } else {
            // Dentro da área, move em direção ao limite mais próximo
            double distanciaInferior = posicao - limiteInferior;
            double distanciaSuperior = limiteSuperior - posicao;

            if (distanciaInferior < distanciaSuperior) {
                posicao -= velocidade;
            } else {
                posicao += velocidade;
            }
        }
    }

    /**
     * Movimento direcionado para buscar clusters
     */
    private void moverBuscaClusters(List<Cluster> clusters, List<Criatura> criaturas) {
        Object alvoMaisProximo = encontrarAlvoMaisProximo(clusters, criaturas);

        if (alvoMaisProximo != null) {
            double posicaoAlvo = obterPosicaoAlvo(alvoMaisProximo);
            double distancia = Math.abs(posicao - posicaoAlvo);

            if (distancia > 1.0) {
                // Move em direção ao alvo
                double direcao = posicaoAlvo > posicao ? 1 : -1;
                posicao += direcao * Math.min(velocidade, distancia);
            }
        } else {
            // Sem alvos, faz patrulha normal
            moverPatrulhaLinear();
        }
    }

    /**
     * Movimento defensivo, mantém distância dos alvos mais fortes
     */
    private void moverDefensivo(List<Cluster> clusters, List<Criatura> criaturas) {
        Object alvoMaisPerigoso = encontrarAlvoMaisPerigoso(clusters, criaturas);

        if (alvoMaisPerigoso != null) {
            double posicaoAlvo = obterPosicaoAlvo(alvoMaisPerigoso);
            double distancia = Math.abs(posicao - posicaoAlvo);

            if (distancia < 10.0) {
                // Muito próximo, se afasta
                double direcao = posicaoAlvo > posicao ? -1 : 1;
                posicao += direcao * velocidade;
            } else {
                // Distância segura, faz patrulha
                moverPatrulhaLinear();
            }
        } else {
            moverPatrulhaLinear();
        }
    }

    /**
     * Encontra o alvo mais próximo (cluster ou criatura)
     */
    private Object encontrarAlvoMaisProximo(List<Cluster> clusters, List<Criatura> criaturas) {
        Object alvoMaisProximo = null;
        double menorDistancia = Double.MAX_VALUE;

        // Verifica clusters
        for (Cluster cluster : clusters) {
            if (cluster.isAtivo()) {
                double distancia = Math.abs(posicao - cluster.getPosicaoMedia());
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    alvoMaisProximo = cluster;
                }
            }
        }

        // Verifica criaturas individuais
        for (Criatura criatura : criaturas) {
            if (criatura.isAtiva()) {
                double distancia = Math.abs(posicao - criatura.getPosicao());
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    alvoMaisProximo = criatura;
                }
            }
        }

        return alvoMaisProximo;
    }

    /**
     * Encontra o alvo mais perigoso (com mais moedas/criaturas)
     */
    private Object encontrarAlvoMaisPerigoso(List<Cluster> clusters, List<Criatura> criaturas) {
        Object alvoMaisPerigoso = null;
        double maiorPerigo = 0;

        // Avalia clusters
        for (Cluster cluster : clusters) {
            if (cluster.isAtivo()) {
                double perigo = cluster.getTamanho() * 2 + cluster.getTotalMoedas() * 0.1;
                if (perigo > maiorPerigo) {
                    maiorPerigo = perigo;
                    alvoMaisPerigoso = cluster;
                }
            }
        }

        // Avalia criaturas
        for (Criatura criatura : criaturas) {
            if (criatura.isAtiva()) {
                double perigo = criatura.getMoedas() * 0.2;
                if (perigo > maiorPerigo) {
                    maiorPerigo = perigo;
                    alvoMaisPerigoso = criatura;
                }
            }
        }

        return alvoMaisPerigoso;
    }

    /**
     * Obtém a posição de um alvo (cluster ou criatura)
     */
    private double obterPosicaoAlvo(Object alvo) {
        if (alvo instanceof Cluster) {
            return ((Cluster) alvo).getPosicaoMedia();
        } else if (alvo instanceof Criatura) {
            return ((Criatura) alvo).getPosicao();
        }
        return 0.0;
    }

    /**
     * Adapta a estratégia do guardião baseada na situação atual
     */
    private void adaptarEstrategia(List<Cluster> clusters, List<Criatura> criaturas) {
        int totalAlvos = clusters.size() + criaturas.size();
        int totalMoedasInimigas = clusters.stream().mapToInt(Cluster::getTotalMoedas).sum()
                + criaturas.stream().mapToInt(Criatura::getMoedas).sum();

        // Adapta velocidade baseada na urgência
        if (totalAlvos > 5 || totalMoedasInimigas > 50) {
            velocidade = Math.min(3.0, velocidade + 0.1);
            tipoMovimento = MovimentoPatrulha.BUSCA_CLUSTERS;
        } else if (totalAlvos <= 2 && totalMoedasInimigas <= 20) {
            velocidade = Math.max(1.0, velocidade - 0.05);
            tipoMovimento = MovimentoPatrulha.PATRULHA_LINEAR;
        }

        // Fica defensivo se há alvos muito fortes
        Object alvoPerigoso = encontrarAlvoMaisPerigoso(clusters, criaturas);
        if (alvoPerigoso instanceof Cluster) {
            Cluster cluster = (Cluster) alvoPerigoso;
            if (cluster.getTamanho() > 3 && cluster.getTotalMoedas() > 30) {
                tipoMovimento = MovimentoPatrulha.DEFENSIVO;
            }
        }
    }

    /**
     * Tenta eliminar um cluster próximo
     *
     * @param cluster cluster alvo
     * @return true se conseguiu eliminar
     */
    public boolean eliminarCluster(Cluster cluster) {
        if (!ativo || cluster == null || !cluster.isAtivo()) {
            return false;
        }

        double distancia = Math.abs(posicao - cluster.getPosicaoMedia());

        // Só pode eliminar se estiver próximo
        if (distancia <= 3.0) {
            // Chance de eliminação baseada no tamanho do cluster
            double chanceEliminacao = Math.max(0.3, 0.8 - (cluster.getTamanho() * 0.1));

            if (Math.random() < chanceEliminacao) {
                // Rouba algumas moedas antes de eliminar
                int moedasRoubadas = cluster.perderMoedas(cluster.getTotalMoedas() / 2);
                this.moedas += moedasRoubadas;

                // Elimina o cluster
                cluster.setAtivo(false);
                clustersEliminados++;

                return true;
            }
        }

        return false;
    }

    /**
     * Tenta eliminar uma criatura próxima
     *
     * @param criatura criatura alvo
     * @return true se conseguiu eliminar
     */
    public boolean eliminarCriatura(Criatura criatura) {
        if (!ativo || criatura == null || !criatura.isAtiva()) {
            return false;
        }

        double distancia = Math.abs(posicao - criatura.getPosicao());

        // Só pode eliminar se estiver próximo
        if (distancia <= 2.0) {
            // Chance de eliminação baseada nas moedas da criatura
            double chanceEliminacao = Math.max(0.5, 0.9 - (criatura.getMoedas() * 0.02));

            if (Math.random() < chanceEliminacao) {
                // Rouba todas as moedas
                int moedasRoubadas = criatura.perderMoedas(criatura.getMoedas());
                this.moedas += moedasRoubadas;

                // Elimina a criatura
                criatura.setAtiva(false);

                return true;
            }
        }

        return false;
    }

    /**
     * Verifica se o guardião alcançou condição de vitória
     *
     * @param clusters lista atual de clusters
     * @param criaturas lista atual de criaturas
     * @return true se vitória foi alcançada
     */
    public boolean verificarVitoria(List<Cluster> clusters, List<Criatura> criaturas) {
        // Conta alvos ativos
        long clustersAtivos = clusters.stream().filter(Cluster::isAtivo).count();
        long criaturasAtivas = criaturas.stream().filter(Criatura::isAtiva).count();

        // Vitória se eliminou todos os alvos
        if (clustersAtivos == 0 && criaturasAtivas == 0) {
            vitoriosa = true;
            return true;
        }

        // Vitória alternativa: eliminou muitos clusters e tem muitas moedas
        if (clustersEliminados >= 3 && moedas >= 50) {
            vitoriosa = true;
            return true;
        }

        // Vitória por dominância: controlou o território por muito tempo
        if (iteracoesVivo >= 100 && moedas >= 30 && clustersEliminados >= 2) {
            vitoriosa = true;
            return true;
        }

        return false;
    }

    /**
     * Verifica se o guardião deve ser eliminado
     *
     * @param clusters lista de clusters
     * @param criaturas lista de criaturas
     * @return true se deve ser eliminado
     */
    public boolean verificarEliminacao(List<Cluster> clusters, List<Criatura> criaturas) {
        // Guardião pode ser eliminado se estiver cercado por muitos inimigos fortes
        int inimigosProximos = 0;
        int forcaInimigas = 0;

        for (Cluster cluster : clusters) {
            if (cluster.isAtivo()) {
                double distancia = Math.abs(posicao - cluster.getPosicaoMedia());
                if (distancia <= 5.0) {
                    inimigosProximos++;
                    forcaInimigas += cluster.getTamanho() + cluster.getTotalMoedas() / 10;
                }
            }
        }

        for (Criatura criatura : criaturas) {
            if (criatura.isAtiva()) {
                double distancia = Math.abs(posicao - criatura.getPosicao());
                if (distancia <= 3.0) {
                    inimigosProximos++;
                    forcaInimigas += criatura.getMoedas() / 5;
                }
            }
        }

        // Eliminado se cercado por muitos inimigos fortes
        if (inimigosProximos >= 3 && forcaInimigas > 20) {
            double chanceEliminacao = 0.1 + (forcaInimigas * 0.01);
            if (Math.random() < chanceEliminacao) {
                ativo = false;
                return true;
            }
        }

        return false;
    }

    /**
     * Retorna a cor do guardião baseada no seu estado
     */
    public Color getCor() {
        if (!ativo) {
            return Color.DARK_GRAY;
        }

        if (vitoriosa) {
            return Color.BLUE;
        }

        // Cor baseada no número de clusters eliminados
        if (clustersEliminados == 0) {
            return Color.BLACK;
        } else if (clustersEliminados <= 2) {
            return Color.RED;
        } else {
            return Color.MAGENTA;
        }
    }

    /**
     * Retorna informações de status do guardião
     */
    public String getStatus() {
        return String.format("Guardião[pos=%.2f, moedas=%d, eliminados=%d, iterações=%d, %s]",
                posicao, moedas, clustersEliminados, iteracoesVivo,
                ativo ? "ATIVO" : "INATIVO");
    }

    /**
     * Retorna estatísticas detalhadas
     */
    public String getEstatisticasDetalhadas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GUARDIÃO DO HORIZONTE ===\n");
        sb.append(String.format("Posição: %.2f\n", posicao));
        sb.append(String.format("Moedas: %d\n", moedas));
        sb.append(String.format("Velocidade: %.2f\n", velocidade));
        sb.append(String.format("Clusters Eliminados: %d\n", clustersEliminados));
        sb.append(String.format("Iterações Vivo: %d\n", iteracoesVivo));
        sb.append(String.format("Tipo Movimento: %s\n", tipoMovimento));
        sb.append(String.format("Status: %s\n", ativo ? "ATIVO" : "INATIVO"));
        sb.append(String.format("Vitoriosa: %s\n", vitoriosa ? "SIM" : "NÃO"));
        return sb.toString();
    }

    // Getters e Setters
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
    }

    public double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(double velocidade) {
        this.velocidade = Math.max(0.5, Math.min(5.0, velocidade));
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public int getClustersEliminados() {
        return clustersEliminados;
    }

    public void setClustersEliminados(int clustersEliminados) {
        this.clustersEliminados = Math.max(0, clustersEliminados);
    }

    public int getIteracoesVivo() {
        return iteracoesVivo;
    }

    public MovimentoPatrulha getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(MovimentoPatrulha tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public boolean isVitoriosa() {
        return vitoriosa;
    }

    public void setVitoriosa(boolean vitoriosa) {
        this.vitoriosa = vitoriosa;
    }

    @Override
    public String toString() {
        return getStatus();
    }
}
