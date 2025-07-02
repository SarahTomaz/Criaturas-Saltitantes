package com.example.model.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o resultado de uma simulação executada Contém todas as informações
 * sobre o que aconteceu durante a simulação
 */
public class ResultadoSimulacao implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String usuarioLogin;
    private LocalDateTime dataExecucao;
    private int numeroIteracoes;
    private boolean simulacaoCompleta;
    private TipoResultado tipoResultado;
    private String descricaoResultado;

    // Estatísticas iniciais
    private int criaturasIniciais;
    private int moedasIniciais;
    private double posicaoInicialGuardiao;

    // Estatísticas finais
    private int criaturasFinais;
    private int clustersFinais;
    private int moedasFinais;
    private double posicaoFinalGuardiao;
    private int moedasGuardiao;
    private int clustersEliminados;

    // Estatísticas durante a simulação
    private int maxClusters;
    private int maxCriaturas;
    private int maxMoedas;
    private double mediaMovimento;
    private int totalRoubos;
    private int totalEliminacoes;

    // Detalhes da vitória/derrota
    private String condicaoVitoria;
    private int iteracaoFinal;
    private List<EventoSimulacao> eventosImportantes;

    // Pontuação
    private int pontuacao;
    private int bonusTempo;
    private int bonusEficiencia;
    private int penalidades;

    public enum TipoResultado {
        VITORIA_GUARDIAO,
        VITORIA_CRIATURAS,
        EMPATE,
        SIMULACAO_INTERROMPIDA,
        ERRO_EXECUCAO
    }

    public static class EventoSimulacao implements Serializable {

        private static final long serialVersionUID = 1L;

        private int iteracao;
        private TipoEvento tipo;
        private String descricao;
        private LocalDateTime timestamp;

        public enum TipoEvento {
            CLUSTER_FORMADO,
            CLUSTER_ELIMINADO,
            CRIATURA_ELIMINADA,
            ROUBO_REALIZADO,
            MUDANCA_ESTRATEGIA,
            MARCO_IMPORTANTE
        }

        public EventoSimulacao(int iteracao, TipoEvento tipo, String descricao) {
            this.iteracao = iteracao;
            this.tipo = tipo;
            this.descricao = descricao;
            this.timestamp = LocalDateTime.now();
        }

        // Getters
        public int getIteracao() {
            return iteracao;
        }

        public TipoEvento getTipo() {
            return tipo;
        }

        public String getDescricao() {
            return descricao;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return String.format("Iteração %d: %s - %s", iteracao, tipo, descricao);
        }
    }

    public ResultadoSimulacao(String usuarioLogin) {
        this.id = gerarId();
        this.usuarioLogin = usuarioLogin;
        this.dataExecucao = LocalDateTime.now();
        this.eventosImportantes = new ArrayList<>();
        this.simulacaoCompleta = false;
        this.tipoResultado = TipoResultado.SIMULACAO_INTERROMPIDA;
        this.pontuacao = 0;
    }

    /**
     * Gera um ID único para o resultado
     */
    private String gerarId() {
        return "SIM_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);
    }

    /**
     * Inicializa as estatísticas iniciais da simulação
     */
    public void inicializarEstatisticas(int criaturas, int moedas, double posicaoGuardiao) {
        this.criaturasIniciais = criaturas;
        this.moedasIniciais = moedas;
        this.posicaoInicialGuardiao = posicaoGuardiao;

        // Inicializa contadores
        this.maxClusters = 0;
        this.maxCriaturas = criaturas;
        this.maxMoedas = moedas;
        this.totalRoubos = 0;
        this.totalEliminacoes = 0;
        this.mediaMovimento = 0.0;
    }

    /**
     * Finaliza a simulação com as estatísticas finais
     */
    public void finalizarSimulacao(int criaturas, int clusters, int moedas,
            GuardiaoHorizonte guardiao, int iteracao) {
        this.criaturasFinais = criaturas;
        this.clustersFinais = clusters;
        this.moedasFinais = moedas;
        this.posicaoFinalGuardiao = guardiao.getPosicao();
        this.moedasGuardiao = guardiao.getMoedas();
        this.clustersEliminados = guardiao.getClustersEliminados();
        this.iteracaoFinal = iteracao;
        this.numeroIteracoes = iteracao;
        this.simulacaoCompleta = true;

        // Determina o tipo de resultado
        determinarTipoResultado(guardiao);

        // Calcula pontuação
        calcularPontuacao();
    }

    /**
     * Determina o tipo de resultado baseado no estado final
     */
    private void determinarTipoResultado(GuardiaoHorizonte guardiao) {
        if (guardiao.isVitoriosa()) {
            this.tipoResultado = TipoResultado.VITORIA_GUARDIAO;
            this.condicaoVitoria = "Guardião eliminou todos os alvos ou dominou o território";
            this.descricaoResultado = String.format("Vitória do Guardião! Eliminou %d clusters em %d iterações.",
                    clustersEliminados, numeroIteracoes);
        } else if (criaturasFinais > 0 || clustersFinais > 0) {
            this.tipoResultado = TipoResultado.VITORIA_CRIATURAS;
            this.condicaoVitoria = "Criaturas sobreviveram ao ataque do guardião";
            this.descricaoResultado = String.format("Vitória das Criaturas! %d criaturas e %d clusters sobreviveram.",
                    criaturasFinais, clustersFinais);
        } else {
            this.tipoResultado = TipoResultado.EMPATE;
            this.condicaoVitoria = "Todas as entidades foram eliminadas simultaneamente";
            this.descricaoResultado = "Empate! Todos foram eliminados ao mesmo tempo.";
        }
    }

    /**
     * Calcula a pontuação baseada no desempenho
     */
    private void calcularPontuacao() {
        int pontuacaoBase = 100;

        // Bônus por vitória
        if (tipoResultado == TipoResultado.VITORIA_GUARDIAO) {
            pontuacaoBase += 200;
        } else if (tipoResultado == TipoResultado.VITORIA_CRIATURAS) {
            pontuacaoBase += 150;
        }

        // Bônus por eficiência (menos iterações)
        if (numeroIteracoes <= 50) {
            bonusTempo = 100;
        } else if (numeroIteracoes <= 100) {
            bonusTempo = 50;
        } else {
            bonusTempo = 0;
        }

        // Bônus por performance
        bonusEficiencia = (clustersEliminados * 25) + (totalRoubos * 5);

        // Penalidades por simulação incompleta
        if (!simulacaoCompleta) {
            penalidades = 50;
        }

        this.pontuacao = pontuacaoBase + bonusTempo + bonusEficiencia - penalidades;
        this.pontuacao = Math.max(0, this.pontuacao); // Não permite pontuação negativa
    }

    /**
     * Adiciona um evento importante à simulação
     */
    public void adicionarEvento(int iteracao, EventoSimulacao.TipoEvento tipo, String descricao) {
        EventoSimulacao evento = new EventoSimulacao(iteracao, tipo, descricao);
        eventosImportantes.add(evento);

        // Mantém apenas os últimos 20 eventos para não sobrecarregar
        if (eventosImportantes.size() > 20) {
            eventosImportantes.remove(0);
        }
    }

    /**
     * Atualiza estatísticas durante a simulação
     */
    public void atualizarEstatisticas(int clusters, int criaturas, int moedas, double movimento) {
        this.maxClusters = Math.max(this.maxClusters, clusters);
        this.maxCriaturas = Math.max(this.maxCriaturas, criaturas);
        this.maxMoedas = Math.max(this.maxMoedas, moedas);

        // Atualiza média de movimento
        if (numeroIteracoes > 0) {
            this.mediaMovimento = ((this.mediaMovimento * (numeroIteracoes - 1)) + movimento) / numeroIteracoes;
        } else {
            this.mediaMovimento = movimento;
        }
    }

    /**
     * Registra um roubo na simulação
     */
    public void registrarRoubo(int iteracao, String descricao) {
        this.totalRoubos++;
        adicionarEvento(iteracao, EventoSimulacao.TipoEvento.ROUBO_REALIZADO, descricao);
    }

    /**
     * Registra uma eliminação na simulação
     */
    public void registrarEliminacao(int iteracao, String descricao) {
        this.totalEliminacoes++;
        adicionarEvento(iteracao, EventoSimulacao.TipoEvento.CRIATURA_ELIMINADA, descricao);
    }

    /**
     * Gera um relatório detalhado do resultado
     */
    public String gerarRelatorioDetalhado() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DA SIMULAÇÃO ===\n");
        sb.append(String.format("ID: %s\n", id));
        sb.append(String.format("Usuário: %s\n", usuarioLogin));
        sb.append(String.format("Data: %s\n", dataExecucao.toString()));
        sb.append(String.format("Resultado: %s\n", tipoResultado));
        sb.append(String.format("Descrição: %s\n", descricaoResultado));
        sb.append(String.format("Iterações: %d\n", numeroIteracoes));
        sb.append(String.format("Pontuação: %d\n", pontuacao));

        sb.append("\n=== ESTATÍSTICAS INICIAIS ===\n");
        sb.append(String.format("Criaturas: %d\n", criaturasIniciais));
        sb.append(String.format("Moedas: %d\n", moedasIniciais));
        sb.append(String.format("Posição Guardião: %.2f\n", posicaoInicialGuardiao));

        sb.append("\n=== ESTATÍSTICAS FINAIS ===\n");
        sb.append(String.format("Criaturas: %d\n", criaturasFinais));
        sb.append(String.format("Clusters: %d\n", clustersFinais));
        sb.append(String.format("Moedas: %d\n", moedasFinais));
        sb.append(String.format("Posição Guardião: %.2f\n", posicaoFinalGuardiao));
        sb.append(String.format("Moedas Guardião: %d\n", moedasGuardiao));
        sb.append(String.format("Clusters Eliminados: %d\n", clustersEliminados));

        sb.append("\n=== ESTATÍSTICAS GERAIS ===\n");
        sb.append(String.format("Máximo Clusters: %d\n", maxClusters));
        sb.append(String.format("Máximo Criaturas: %d\n", maxCriaturas));
        sb.append(String.format("Máximo Moedas: %d\n", maxMoedas));
        sb.append(String.format("Média Movimento: %.2f\n", mediaMovimento));
        sb.append(String.format("Total Roubos: %d\n", totalRoubos));
        sb.append(String.format("Total Eliminações: %d\n", totalEliminacoes));

        sb.append("\n=== PONTUAÇÃO DETALHADA ===\n");
        sb.append(String.format("Bônus Tempo: %d\n", bonusTempo));
        sb.append(String.format("Bônus Eficiência: %d\n", bonusEficiencia));
        sb.append(String.format("Penalidades: %d\n", penalidades));
        sb.append(String.format("Pontuação Final: %d\n", pontuacao));

        if (!eventosImportantes.isEmpty()) {
            sb.append("\n=== EVENTOS IMPORTANTES ===\n");
            for (EventoSimulacao evento : eventosImportantes) {
                sb.append(evento.toString()).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Gera um resumo conciso do resultado
     */
    public String gerarResumo() {
        return String.format("%s - %s (%d iterações, %d pontos)",
                tipoResultado, descricaoResultado, numeroIteracoes, pontuacao);
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public String getUsuarioLogin() {
        return usuarioLogin;
    }

    public LocalDateTime getDataExecucao() {
        return dataExecucao;
    }

    public int getNumeroIteracoes() {
        return numeroIteracoes;
    }

    public boolean isSimulacaoCompleta() {
        return simulacaoCompleta;
    }

    public TipoResultado getTipoResultado() {
        return tipoResultado;
    }

    public String getDescricaoResultado() {
        return descricaoResultado;
    }

    public String getCondicaoVitoria() {
        return condicaoVitoria;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public int getCriaturasIniciais() {
        return criaturasIniciais;
    }

    public int getMoedasIniciais() {
        return moedasIniciais;
    }

    public double getPosicaoInicialGuardiao() {
        return posicaoInicialGuardiao;
    }

    public int getCriaturasFinais() {
        return criaturasFinais;
    }

    public int getClustersFinais() {
        return clustersFinais;
    }

    public int getMoedasFinais() {
        return moedasFinais;
    }

    public double getPosicaoFinalGuardiao() {
        return posicaoFinalGuardiao;
    }

    public int getMoedasGuardiao() {
        return moedasGuardiao;
    }

    public int getClustersEliminados() {
        return clustersEliminados;
    }

    public int getMaxClusters() {
        return maxClusters;
    }

    public int getMaxCriaturas() {
        return maxCriaturas;
    }

    public int getMaxMoedas() {
        return maxMoedas;
    }

    public double getMediaMovimento() {
        return mediaMovimento;
    }

    public int getTotalRoubos() {
        return totalRoubos;
    }

    public int getTotalEliminacoes() {
        return totalEliminacoes;
    }

    public List<EventoSimulacao> getEventosImportantes() {
        return new ArrayList<>(eventosImportantes);
    }

    @Override
    public String toString() {
        return gerarResumo();
    }
}
