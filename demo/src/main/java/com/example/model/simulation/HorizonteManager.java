package com.example.model.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.entities.Cluster;
import com.example.model.entities.Criatura;
import com.example.model.entities.GuardiaoHorizonte;

/**
 * Gerencia o horizonte da simulação e as posições das entidades
 */
public class HorizonteManager {

    private static final double LIMITE_INFERIOR = 0.0;
    private static final double LIMITE_SUPERIOR = 100.0;
    private static final double TOLERANCIA_POSICAO = 0.01;

    private Map<Double, List<Object>> mapaHorizonte;
    private double limiteInferior;
    private double limiteSuperior;

    public HorizonteManager() {
        this.mapaHorizonte = new HashMap<>();
        this.limiteInferior = LIMITE_INFERIOR;
        this.limiteSuperior = LIMITE_SUPERIOR;
    }

    public HorizonteManager(double limiteInferior, double limiteSuperior) {
        this();
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
    }

    /**
     * Atualiza o mapa do horizonte com todas as entidades
     */
    public void atualizarHorizonte(List<Criatura> criaturas, List<Cluster> clusters, GuardiaoHorizonte guardiao) {
        mapaHorizonte.clear();

        // Adiciona criaturas ativas
        for (Criatura criatura : criaturas) {
            if (criatura.isAtiva()) {
                adicionarEntidadeNoMapa(criatura.getPosicao(), criatura);
            }
        }

        // Adiciona clusters ativos
        for (Cluster cluster : clusters) {
            if (cluster.isAtivo()) {
                adicionarEntidadeNoMapa(cluster.getPosicaoMedia(), cluster);
            }
        }

        // Adiciona guardião se ativo
        if (guardiao != null && guardiao.isAtivo()) {
            adicionarEntidadeNoMapa(guardiao.getPosicao(), guardiao);
        }
    }

    /**
     * Adiciona uma entidade no mapa do horizonte
     */
    private void adicionarEntidadeNoMapa(double posicao, Object entidade) {
        // Normaliza a posição
        double posicaoNormalizada = normalizarPosicao(posicao);

        mapaHorizonte.computeIfAbsent(posicaoNormalizada, k -> new ArrayList<>()).add(entidade);
    }

    /**
     * Normaliza a posição dentro dos limites do horizonte
     */
    private double normalizarPosicao(double posicao) {
        // Arredonda para evitar problemas de precisão de ponto flutuante
        posicao = Math.round(posicao * 1000.0) / 1000.0;

        // Aplica os limites
        if (posicao < limiteInferior) {
            return limiteInferior;
        } else if (posicao > limiteSuperior) {
            return limiteSuperior;
        }

        return posicao;
    }

    /**
     * Verifica se uma posição está ocupada
     */
    public boolean isPosicaoOcupada(double posicao) {
        double posicaoNormalizada = normalizarPosicao(posicao);
        return mapaHorizonte.containsKey(posicaoNormalizada)
                && !mapaHorizonte.get(posicaoNormalizada).isEmpty();
    }

    /**
     * Retorna as entidades em uma posição específica
     */
    public List<Object> getEntidadesNaPosicao(double posicao) {
        double posicaoNormalizada = normalizarPosicao(posicao);
        return mapaHorizonte.getOrDefault(posicaoNormalizada, new ArrayList<>());
    }

    /**
     * Encontra a entidade mais próxima de uma posição
     */
    public Object encontrarEntidadeMaisProxima(double posicao, Class<?> tipoEntidade) {
        Object maisProxima = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Map.Entry<Double, List<Object>> entrada : mapaHorizonte.entrySet()) {
            for (Object entidade : entrada.getValue()) {
                if (tipoEntidade.isAssignableFrom(entidade.getClass())) {
                    double distancia = Math.abs(posicao - entrada.getKey());

                    if (distancia < menorDistancia) {
                        menorDistancia = distancia;
                        maisProxima = entidade;
                    }
                }
            }
        }

        return maisProxima;
    }

    /**
     * Encontra todas as entidades dentro de um raio
     */
    public List<Object> encontrarEntidadesNoRaio(double posicao, double raio, Class<?> tipoEntidade) {
        List<Object> entidadesNoRaio = new ArrayList<>();

        for (Map.Entry<Double, List<Object>> entrada : mapaHorizonte.entrySet()) {
            double distancia = Math.abs(posicao - entrada.getKey());

            if (distancia <= raio) {
                for (Object entidade : entrada.getValue()) {
                    if (tipoEntidade == null || tipoEntidade.isAssignableFrom(entidade.getClass())) {
                        entidadesNoRaio.add(entidade);
                    }
                }
            }
        }

        return entidadesNoRaio;
    }

    /**
     * Calcula a densidade de entidades em uma região
     */
    public double calcularDensidade(double posicaoInicial, double posicaoFinal) {
        int totalEntidades = 0;

        for (Map.Entry<Double, List<Object>> entrada : mapaHorizonte.entrySet()) {
            double posicao = entrada.getKey();

            if (posicao >= posicaoInicial && posicao <= posicaoFinal) {
                totalEntidades += entrada.getValue().size();
            }
        }

        double tamanhoRegiao = Math.abs(posicaoFinal - posicaoInicial);
        return tamanhoRegiao > 0 ? totalEntidades / tamanhoRegiao : 0;
    }

    /**
     * Retorna estatísticas do horizonte
     */
    public EstatisticasHorizonte getEstatisticas() {
        int totalEntidades = mapaHorizonte.values().stream()
                .mapToInt(List::size)
                .sum();

        int posicosOcupadas = mapaHorizonte.size();

        double posicaoMedia = 0;
        if (totalEntidades > 0) {
            double somaWeightedPosition = 0;

            for (Map.Entry<Double, List<Object>> entrada : mapaHorizonte.entrySet()) {
                somaWeightedPosition += entrada.getKey() * entrada.getValue().size();
            }

            posicaoMedia = somaWeightedPosition / totalEntidades;
        }

        return new EstatisticasHorizonte(totalEntidades, posicosOcupadas, posicaoMedia);
    }

    /**
     * Limpa o horizonte
     */
    public void limpar() {
        mapaHorizonte.clear();
    }

    /**
     * Retorna o limite inferior do horizonte
     */
    public double getLimiteInferior() {
        return limiteInferior;
    }

    /**
     * Define o limite inferior do horizonte
     */
    public void setLimiteInferior(double limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    /**
     * Retorna o limite superior do horizonte
     */
    public double getLimiteSuperior() {
        return limiteSuperior;
    }

    /**
     * Define o limite superior do horizonte
     */
    public void setLimiteSuperior(double limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    /**
     * Classe para armazenar estatísticas do horizonte
     */
    public static class EstatisticasHorizonte {

        private int totalEntidades;
        private int posicoesOcupadas;
        private double posicaoMedia;

        public EstatisticasHorizonte(int totalEntidades, int posicoesOcupadas, double posicaoMedia) {
            this.totalEntidades = totalEntidades;
            this.posicoesOcupadas = posicoesOcupadas;
            this.posicaoMedia = posicaoMedia;
        }

        public int getTotalEntidades() {
            return totalEntidades;
        }

        public int getPosicoesOcupadas() {
            return posicoesOcupadas;
        }

        public double getPosicaoMedia() {
            return posicaoMedia;
        }

        @Override
        public String toString() {
            return "EstatisticasHorizonte{"
                    + "totalEntidades=" + totalEntidades
                    + ", posicoesOcupadas=" + posicoesOcupadas
                    + ", posicaoMedia=" + posicaoMedia
                    + '}';
        }
    }

    @Override
    public String toString() {
        return "HorizonteManager{"
                + "mapaHorizonte=" + mapaHorizonte
                + ", limiteInferior=" + limiteInferior
                + ", limiteSuperior=" + limiteSuperior
                + '}';
    }

    /**
     * Limpa o horizonte e reinicia os limites
     */
    public void reiniciar() {
        mapaHorizonte.clear();
        limiteInferior = LIMITE_INFERIOR;
        limiteSuperior = LIMITE_SUPERIOR;
    }
}
