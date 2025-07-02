package com.example.model.simulation;

import com.example.model.entities.Criatura;
import com.example.model.entities.Cluster;
import java.util.*;

/**
 * Gerencia a formação, dissolução e interações dos clusters
 */
public class ClusterManager {

    private static final double DISTANCIA_FORMACAO_CLUSTER = 0.1; // Distância mínima para formar cluster
    private static final double DISTANCIA_DISSOLUCAO_CLUSTER = 5.0; // Distância máxima para manter cluster

    private int proximoIdCluster;

    public ClusterManager() {
        this.proximoIdCluster = 1;
    }

    /**
     * Verifica e processa a formação de novos clusters
     */
    public void verificarFormacaoClusters(List<Criatura> criaturas, List<Cluster> clusters) {
        // Lista de criaturas que não estão em clusters
        List<Criatura> criaturasLivres = new ArrayList<>();

        for (Criatura criatura : criaturas) {
            if (criatura.isAtiva() && !estaCriatureEmCluster(criatura, clusters)) {
                criaturasLivres.add(criatura);
            }
        }

        // Verifica formação de clusters entre criaturas livres
        for (int i = 0; i < criaturasLivres.size(); i++) {
            Criatura criatura1 = criaturasLivres.get(i);

            for (int j = i + 1; j < criaturasLivres.size(); j++) {
                Criatura criatura2 = criaturasLivres.get(j);

                double distancia = Math.abs(criatura1.getPosicao() - criatura2.getPosicao());

                if (distancia <= DISTANCIA_FORMACAO_CLUSTER) {
                    // Formar novo cluster
                    Cluster novoCluster = criarCluster(criatura1, criatura2);
                    clusters.add(novoCluster);

                    // Remove criaturas da lista de livres
                    criaturasLivres.remove(criatura1);
                    criaturasLivres.remove(criatura2);

                    System.out.println("Novo cluster formado: " + novoCluster.getId()
                            + " com criaturas " + criatura1.getId() + " e " + criatura2.getId());

                    // Reinicia verificação
                    i = -1;
                    break;
                }
            }
        }

        // Verifica se criaturas livres podem se juntar a clusters existentes
        verificarAdesaoAClusters(criaturasLivres, clusters);

        // Verifica fusão entre clusters próximos
        verificarFusaoClusters(clusters);

        // Verifica dissolução de clusters
        verificarDissolucaoClusters(clusters);
    }

    /**
     * Verifica se uma criatura está em algum cluster
     */
    private boolean estaCriatureEmCluster(Criatura criatura, List<Cluster> clusters) {
        return clusters.stream()
                .filter(Cluster::isAtivo)
                .anyMatch(cluster -> cluster.getCriaturas().contains(criatura));
    }

    /**
     * Cria um novo cluster com duas criaturas
     */
    private Cluster criarCluster(Criatura criatura1, Criatura criatura2) {
        Cluster cluster = new Cluster(proximoIdCluster++);
        cluster.adicionarCriatura(criatura1);
        cluster.adicionarCriatura(criatura2);
        cluster.calcularPosicaoMedia();
        return cluster;
    }

    /**
     * Verifica se criaturas livres podem se juntar a clusters existentes
     */
    private void verificarAdesaoAClusters(List<Criatura> criaturasLivres, List<Cluster> clusters) {
        Iterator<Criatura> iterator = criaturasLivres.iterator();

        while (iterator.hasNext()) {
            Criatura criatura = iterator.next();

            for (Cluster cluster : clusters) {
                if (!cluster.isAtivo()) {
                    continue;
                }

                double distancia = Math.abs(criatura.getPosicao() - cluster.getPosicaoMedia());

                if (distancia <= DISTANCIA_FORMACAO_CLUSTER) {
                    cluster.adicionarCriatura(criatura);
                    iterator.remove();

                    System.out.println("Criatura " + criatura.getId()
                            + " se juntou ao cluster " + cluster.getId());
                    break;
                }
            }
        }
    }

    /**
     * Verifica fusão entre clusters próximos
     */
    private void verificarFusaoClusters(List<Cluster> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster1 = clusters.get(i);
            if (!cluster1.isAtivo()) {
                continue;
            }

            for (int j = i + 1; j < clusters.size(); j++) {
                Cluster cluster2 = clusters.get(j);
                if (!cluster2.isAtivo()) {
                    continue;
                }

                double distancia = Math.abs(cluster1.getPosicaoMedia() - cluster2.getPosicaoMedia());

                if (distancia <= DISTANCIA_FORMACAO_CLUSTER) {
                    // Fusão: cluster1 absorve cluster2
                    for (Criatura criatura : cluster2.getCriaturas()) {
                        cluster1.adicionarCriatura(criatura);
                    }

                    cluster2.setAtivo(false);

                    System.out.println("Clusters " + cluster1.getId() + " e "
                            + cluster2.getId() + " se fundiram");
                    break;
                }
            }
        }
    }

    /**
     * Verifica dissolução de clusters quando criaturas se afastam muito
     */
    private void verificarDissolucaoClusters(List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            if (!cluster.isAtivo()) {
                continue;
            }

            // Verifica se alguma criatura está muito distante do centro
            List<Criatura> criaturasParaRemover = new ArrayList<>();

            for (Criatura criatura : cluster.getCriaturas()) {
                double distancia = Math.abs(criatura.getPosicao() - cluster.getPosicaoMedia());

                if (distancia > DISTANCIA_DISSOLUCAO_CLUSTER) {
                    criaturasParaRemover.add(criatura);
                }
            }

            // Remove criaturas distantes
            for (Criatura criatura : criaturasParaRemover) {
                cluster.removerCriatura(criatura);
                System.out.println("Criatura " + criatura.getId()
                        + " removida do cluster " + cluster.getId() + " por distância");
            }

            // Se cluster ficou com menos de 2 criaturas, dissolve
            if (cluster.getCriaturas().size() < 2) {
                cluster.setAtivo(false);
                System.out.println("Cluster " + cluster.getId() + " dissolvido");
            }
        }
    }

    /**
     * Processa interações especiais dos clusters
     */
    public void processarInteracoesClusters(List<Cluster> clusters, List<Criatura> criaturas) {
        for (Cluster cluster : clusters) {
            if (!cluster.isAtivo()) {
                continue;
            }

            // Cluster rouba de criatura mais próxima
            Criatura criaturaMaisProxima = encontrarCriaturaMaisProxima(cluster, criaturas);

            if (criaturaMaisProxima != null
                    && Math.abs(cluster.getPosicaoMedia() - criaturaMaisProxima.getPosicao()) <= 3.0) {

                cluster.roubarMoedasProximo(criaturaMaisProxima);
            }
        }
    }

    /**
     * Encontra a criatura mais próxima de um cluster
     */
    private Criatura encontrarCriaturaMaisProxima(Cluster cluster, List<Criatura> criaturas) {
        Criatura maisProxima = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Criatura criatura : criaturas) {
            if (!criatura.isAtiva() || cluster.getCriaturas().contains(criatura)) {
                continue;
            }

            double distancia = Math.abs(criatura.getPosicao() - cluster.getPosicaoMedia());

            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                maisProxima = criatura;
            }
        }

        return maisProxima;
    }

    /**
     * Atualiza estatísticas de todos os clusters
     */
    public void atualizarEstatisticasClusters(List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            if (cluster.isAtivo()) {
                cluster.atualizarEstatisticas();
            }
        }
    }

    // Getters
    public int getProximoIdCluster() {
        return proximoIdCluster;
    }

    public void setProximoIdCluster(int proximoIdCluster) {
        this.proximoIdCluster = proximoIdCluster;
    }
}
