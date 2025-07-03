package model;

import java.util.ArrayList;
import java.util.List;

public class Simulacao {

    private List<Criatura> criaturas;
    private List<Cluster> clusters;
    private GuardiaoHorizonte guardiao;
    private Usuario usuario;
    private int iteracoes;
    private boolean concluida;
    private boolean bemSucedida;
    private int maxIteracoes;

    public Simulacao(Usuario usuario, int numCriaturas, int maxIteracoes) {
        this.usuario = usuario;
        this.maxIteracoes = maxIteracoes;
        this.iteracoes = 0;
        this.concluida = false;
        this.bemSucedida = false;

        // Inicializar criaturas
        this.criaturas = new ArrayList<>();
        for (int i = 1; i <= numCriaturas; i++) {
            double posicao = Math.random() * 100; // Posição aleatória entre 0-100
            int moedas = (int) (Math.random() * 10) + 1; // Moedas entre 1-10
            criaturas.add(new Criatura(i, posicao, moedas));
        }

        // Inicializar clusters (vazio no início)
        this.clusters = new ArrayList<>();

        // Inicializar guardião
        double posicaoGuardiao = Math.random() * 100;
        this.guardiao = new GuardiaoHorizonte(posicaoGuardiao);
    }

    public boolean executarIteracao() {
        if (concluida) {
            return false;
        }

        iteracoes++;

        // 1. Processar movimento das criaturas ativas
        List<Criatura> criaturasAtivas = getCriaturasAtivas();
        for (Criatura criatura : criaturasAtivas) {
            criatura.mover();
        }

        // 2. Processar movimento dos clusters
        for (Cluster cluster : clusters) {
            cluster.mover();
        }

        // 3. Verificar formação de novos clusters
        verificarFormacaoClusters();

        // 4. Processar roubo de moedas pelos clusters
        processarRouboClusters();

        // 5. Processar guardião
        guardiao.mover();
        processarGuardiao();

        // 6. Verificar condições de término
        verificarCondicaoTermino();

        return !concluida;
    }

    private List<Criatura> getCriaturasAtivas() {
        return criaturas.stream()
                .filter(Criatura::isAtiva)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private void verificarFormacaoClusters() {
        List<Criatura> criaturasAtivas = getCriaturasAtivas();
        List<Criatura> processadas = new ArrayList<>();

        for (int i = 0; i < criaturasAtivas.size(); i++) {
            Criatura c1 = criaturasAtivas.get(i);
            if (processadas.contains(c1)) {
                continue;
            }

            for (int j = i + 1; j < criaturasAtivas.size(); j++) {
                Criatura c2 = criaturasAtivas.get(j);
                if (processadas.contains(c2)) {
                    continue;
                }

                // Verificar se estão na mesma posição (com pequena tolerância)
                if (Math.abs(c1.getPosicao() - c2.getPosicao()) < 0.01) {
                    // Formar novo cluster
                    Cluster novoCluster = new Cluster(c1, c2);
                    clusters.add(novoCluster);

                    processadas.add(c1);
                    processadas.add(c2);

                    // Verificar se há mais criaturas na mesma posição
                    for (int k = j + 1; k < criaturasAtivas.size(); k++) {
                        Criatura c3 = criaturasAtivas.get(k);
                        if (!processadas.contains(c3) && c3.isAtiva()
                                && Math.abs(c3.getPosicao() - novoCluster.getPosicao()) < 0.01) {
                            novoCluster.adicionarCriatura(c3);
                            processadas.add(c3);
                        }
                    }

                    // Após formar o cluster, roubar metade das moedas da criatura mais próxima
                    Criatura vizinhoMaisProximo = encontrarVizinhoMaisProximoParaCluster(novoCluster, processadas);
                    if (vizinhoMaisProximo != null) {
                        int metadeMoedas = vizinhoMaisProximo.getMoedas() / 2;
                        int moedasRoubadas = vizinhoMaisProximo.removerMoedas(metadeMoedas);
                        novoCluster.setTotalMoedas(novoCluster.getTotalMoedas() + moedasRoubadas);
                    }

                    return; // Processar apenas um cluster por iteração
                }
            }
        }

        // Verificar também colisões entre criaturas e clusters existentes
        verificarColisoesCriaturasComClusters();
    }

    private void verificarColisoesCriaturasComClusters() {
        List<Criatura> criaturasAtivas = getCriaturasAtivas();

        for (Criatura criatura : criaturasAtivas) {
            for (Cluster cluster : new ArrayList<>(clusters)) {
                if (Math.abs(criatura.getPosicao() - cluster.getPosicao()) < 0.01) {
                    // Criatura se junta ao cluster existente
                    cluster.adicionarCriatura(criatura);

                    // Roubar metade das moedas da criatura mais próxima
                    Criatura vizinhoMaisProximo = encontrarVizinhoMaisProximoParaCluster(cluster, List.of(criatura));
                    if (vizinhoMaisProximo != null) {
                        int metadeMoedas = vizinhoMaisProximo.getMoedas() / 2;
                        int moedasRoubadas = vizinhoMaisProximo.removerMoedas(metadeMoedas);
                        cluster.setTotalMoedas(cluster.getTotalMoedas() + moedasRoubadas);
                    }
                    return; // Processar apenas uma junção por iteração
                }
            }
        }
    }

    private Criatura encontrarVizinhoMaisProximoParaCluster(Cluster cluster, List<Criatura> excluir) {
        List<Criatura> criaturasAtivas = getCriaturasAtivas();
        Criatura maisProximo = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Criatura criatura : criaturasAtivas) {
            if (excluir.contains(criatura)) {
                continue;
            }

            double distancia = Math.abs(criatura.getPosicao() - cluster.getPosicao());
            if (distancia > 0.01 && distancia < menorDistancia) {
                menorDistancia = distancia;
                maisProximo = criatura;
            }
        }

        return maisProximo;
    }

    private void processarRouboClusters() {
        // Esta funcionalidade agora é feita quando o cluster é formado
        // Mantendo o método por compatibilidade, mas sem lógica adicional
    }

    private void processarGuardiao() {
        // Verificar colisão com clusters
        List<Cluster> clustersParaRemover = new ArrayList<>();
        for (Cluster cluster : clusters) {
            if (Math.abs(guardiao.getPosicao() - cluster.getPosicao()) < 0.01) {
                guardiao.eliminarCluster(cluster);
                clustersParaRemover.add(cluster);
            }
        }
        clusters.removeAll(clustersParaRemover);

        // Verificar colisão com criaturas
        List<Criatura> criaturasAtivas = getCriaturasAtivas();
        for (Criatura criatura : criaturasAtivas) {
            if (Math.abs(guardiao.getPosicao() - criatura.getPosicao()) < 0.01) {
                guardiao.eliminarCriatura(criatura);
            }
        }
    }

    private void verificarCondicaoTermino() {
        List<Criatura> criaturasAtivas = getCriaturasAtivas();

        // Condição 1: Apenas guardião sobrevive
        if (criaturasAtivas.isEmpty() && clusters.isEmpty()) {
            concluida = true;
            bemSucedida = true;
            return;
        }

        // Condição 2: Guardião + 1 criatura, com guardião tendo mais moedas
        if (criaturasAtivas.size() == 1 && clusters.isEmpty()) {
            Criatura ultimaCriatura = criaturasAtivas.get(0);
            if (guardiao.temMaisMoedasQue(ultimaCriatura)) {
                concluida = true;
                bemSucedida = true;
                return;
            }
        }

        // Condição de timeout
        if (iteracoes >= maxIteracoes) {
            concluida = true;
            bemSucedida = false;
        }
    }

    public void finalizar() {
        if (bemSucedida) {
            usuario.incrementarSimulacaoBemSucedida();
        } else {
            usuario.incrementarTotalSimulacoes();
        }
    }

    // Getters
    public List<Criatura> getCriaturas() {
        return new ArrayList<>(criaturas);
    }

    public List<Cluster> getClusters() {
        return new ArrayList<>(clusters);
    }

    public GuardiaoHorizonte getGuardiao() {
        return guardiao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public int getIteracoes() {
        return iteracoes;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public boolean isBemSucedida() {
        return bemSucedida;
    }

    public int getMaxIteracoes() {
        return maxIteracoes;
    }

    public String getStatus() {
        if (!concluida) {
            return String.format("Iteração %d/%d - Em andamento", iteracoes, maxIteracoes);
        } else if (bemSucedida) {
            return String.format("Concluída com sucesso em %d iterações", iteracoes);
        } else {
            return String.format("Falhou após %d iterações", iteracoes);
        }
    }
}
