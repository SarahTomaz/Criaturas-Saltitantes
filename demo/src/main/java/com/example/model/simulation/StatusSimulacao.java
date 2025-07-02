package com.example.model.simulation;

/**
 * Representa o status atual da simulação
 */
public class StatusSimulacao {

    public enum EstadoSimulacao {
        PARADA, EXECUTANDO, PAUSADA, FINALIZADA, ERRO
    }

    private EstadoSimulacao estado;
    private int iteracaoAtual;
    private int criaturasAtivas;
    private int clustersAtivos;
    private boolean bemSucedida;
    private String motivoTermino;
    private int totalRoubos;
    private int totalEliminacoes;
    private double movimentoMedio;
    private int moedasGuardiao;
    private double posicaoGuardiao;
    private long tempoExecucao;
    private long tempoInicio;

    public StatusSimulacao() {
        this.estado = EstadoSimulacao.PARADA;
        this.iteracaoAtual = 0;
        this.criaturasAtivas = 0;
        this.clustersAtivos = 0;
        this.bemSucedida = false;
        this.motivoTermino = "";
        this.totalRoubos = 0;
        this.totalEliminacoes = 0;
        this.movimentoMedio = 0.0;
        this.moedasGuardiao = 0;
        this.posicaoGuardiao = 0.0;
        this.tempoInicio = System.currentTimeMillis();
    }

    /**
     * Atualiza o tempo de execução
     */
    public void atualizarTempoExecucao() {
        this.tempoExecucao = System.currentTimeMillis() - tempoInicio;
    }

    /**
     * Reinicia o cronômetro
     */
    public void reiniciarCronometro() {
        this.tempoInicio = System.currentTimeMillis();
        this.tempoExecucao = 0;
    }

    /**
     * Verifica se a simulação está em execução
     */
    public boolean isExecutando() {
        return estado == EstadoSimulacao.EXECUTANDO;
    }

    /**
     * Verifica se a simulação está pausada
     */
    public boolean isPausada() {
        return estado == EstadoSimulacao.PAUSADA;
    }

    /**
     * Verifica se a simulação foi finalizada
     */
    public boolean isFinalizada() {
        return estado == EstadoSimulacao.FINALIZADA;
    }

    /**
     * Cria uma cópia do status atual
     */
    public StatusSimulacao copiar() {
        StatusSimulacao copia = new StatusSimulacao();
        copia.estado = this.estado;
        copia.iteracaoAtual = this.iteracaoAtual;
        copia.criaturasAtivas = this.criaturasAtivas;
        copia.clustersAtivos = this.clustersAtivos;
        copia.bemSucedida = this.bemSucedida;
        copia.motivoTermino = this.motivoTermino;
        copia.totalRoubos = this.totalRoubos;
        copia.totalEliminacoes = this.totalEliminacoes;
        copia.movimentoMedio = this.movimentoMedio;
        copia.moedasGuardiao = this.moedasGuardiao;
        copia.posicaoGuardiao = this.posicaoGuardiao;
        copia.tempoExecucao = this.tempoExecucao;
        copia.tempoInicio = this.tempoInicio;
        return copia;
    }

    @Override
    public String toString() {
        return String.format("StatusSimulacao{estado=%s, iteracao=%d, criaturas=%d, clusters=%d, "
                + "bemSucedida=%s, roubos=%d, eliminacoes=%d}",
                estado, iteracaoAtual, criaturasAtivas, clustersAtivos,
                bemSucedida, totalRoubos, totalEliminacoes);
    }

    // Getters e Setters
    public EstadoSimulacao getEstado() {
        return estado;
    }

    public void setEstado(EstadoSimulacao estado) {
        this.estado = estado;
    }

    public int getIteracaoAtual() {
        return iteracaoAtual;
    }

    public void setIteracaoAtual(int iteracaoAtual) {
        this.iteracaoAtual = iteracaoAtual;
    }

    public int getCriaturasAtivas() {
        return criaturasAtivas;
    }

    public void setCriaturasAtivas(int criaturasAtivas) {
        this.criaturasAtivas = criaturasAtivas;
    }

    public int getClustersAtivos() {
        return clustersAtivos;
    }

    public void setClustersAtivos(int clustersAtivos) {
        this.clustersAtivos = clustersAtivos;
    }

    public boolean isBemSucedida() {
        return bemSucedida;
    }

    public void setBemSucedida(boolean bemSucedida) {
        this.bemSucedida = bemSucedida;
    }

    public String getMotivoTermino() {
        return motivoTermino;
    }

    public void setMotivoTermino(String motivoTermino) {
        this.motivoTermino = motivoTermino;
    }

    public int getTotalRoubos() {
        return totalRoubos;
    }

    public void setTotalRoubos(int totalRoubos) {
        this.totalRoubos = totalRoubos;
    }

    public int getTotalEliminacoes() {
        return totalEliminacoes;
    }

    public void setTotalEliminacoes(int totalEliminacoes) {
        this.totalEliminacoes = totalEliminacoes;
    }

    public double getMovimentoMedio() {
        return movimentoMedio;
    }

    public void setMovimentoMedio(double movimentoMedio) {
        this.movimentoMedio = movimentoMedio;
    }

    public int getMoedasGuardiao() {
        return moedasGuardiao;
    }

    public void setMoedasGuardiao(int moedasGuardiao) {
        this.moedasGuardiao = moedasGuardiao;
    }

    public double getPosicaoGuardiao() {
        return posicaoGuardiao;
    }

    public void setPosicaoGuardiao(double posicaoGuardiao) {
        this.posicaoGuardiao = posicaoGuardiao;
    }

    public long getTempoExecucao() {
        return tempoExecucao;
    }

    public void setTempoExecucao(long tempoExecucao) {
        this.tempoExecucao = tempoExecucao;
    }
}
