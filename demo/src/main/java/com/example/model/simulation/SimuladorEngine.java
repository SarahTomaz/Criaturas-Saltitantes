package com.example.model.simulation;

import com.example.model.entities.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Motor principal da simulação das criaturas saltitantes
 * Responsável por executar todas as iterações e controlar o fluxo da simulação
 */
public class SimuladorEngine {
    
    private List<Criatura> criaturas;
    private List<Cluster> clusters;
    private GuardiaoHorizonte guardiao;
    private StatusSimulacao status;
    private ParametrosSimulacao parametros;
    private Random random;
    
    private int iteracaoAtual;
    private int maxIteracoes;
    private boolean simulacaoAtiva;
    private boolean pausada;
    
    // Managers auxiliares
    private ClusterManager clusterManager;
    private HorizonteManager horizonteManager;
    
    // Estatísticas da simulação
    private int totalRoubos;
    private int totalEliminacoes;
    private double movimentoMedio;
    
    public static class ParametrosSimulacao {
        private int numeroCriaturas;
        private int moedasPorCriatura;
        private double posicaoInicialGuardiao;
        private int maxIteracoes;
        private double fatorMovimento;
        private boolean permitirRoubo;
        private boolean guardiacaoAtiva;
        
        public ParametrosSimulacao() {
            // Valores padrão
            this.numeroCriaturas = 10;
            this.moedasPorCriatura = 5;
            this.posicaoInicialGuardiao = 90.0;
            this.maxIteracoes = 200;
            this.fatorMovimento = 1.0;
            this.permitirRoubo = true;
            this.guardiacaoAtiva = true;
        }
        
        // Getters e Setters
        public int getNumeroCriaturas() { return numeroCriaturas; }
        public void setNumeroCriaturas(int numeroCriaturas) { this.numeroCriaturas = numeroCriaturas; }
        
        public int getMoedasPorCriatura() { return moedasPorCriatura; }
        public void setMoedasPorCriatura(int moedasPorCriatura) { this.moedasPorCriatura = moedasPorCriatura; }
        
        public double getPosicaoInicialGuardiao() { return posicaoInicialGuardiao; }
        public void setPosicaoInicialGuardiao(double posicaoInicialGuardiao) { this.posicaoInicialGuardiao = posicaoInicialGuardiao; }
        
        public int getMaxIteracoes() { return maxIteracoes; }
        public void setMaxIteracoes(int maxIteracoes) { this.maxIteracoes = maxIteracoes; }
        
        public double getFatorMovimento() { return fatorMovimento; }
        public void setFatorMovimento(double fatorMovimento) { this.fatorMovimento = fatorMovimento; }
        
        public boolean isPermitirRoubo() { return permitirRoubo; }
        public void setPermitirRoubo(boolean permitirRoubo) { this.permitirRoubo = permitirRoubo; }
        
        public boolean isGuardiacaoAtiva() { return guardiacaoAtiva; }
        public void setGuardiacaoAtiva(boolean guardiacaoAtiva) { this.guardiacaoAtiva = guardiacaoAtiva; }
    }
    
    public SimuladorEngine() {
        this.criaturas = new ArrayList<>();
        this.clusters = new ArrayList<>();
        this.random = new Random();
        this.clusterManager = new ClusterManager();
        this.horizonteManager = new HorizonteManager();
        this.status = StatusSimulacao.PARADO;
        this.simulacaoAtiva = false;
        this.pausada = false;
    }
    
    /**
     * Inicializa uma nova simulação com os parâmetros fornecidos
     */
    public void iniciarSimulacao(ParametrosSimulacao parametros) {
        this.parametros = parametros;
        this.maxIteracoes = parametros.getMaxIteracoes();
        this.iteracaoAtual = 0;
        this.totalRoubos = 0;
        this.totalEliminacoes = 0;
        this.movimentoMedio = 0.0;
        
        // Limpa estado anterior
        criaturas.clear();
        clusters.clear();
        
        // Cria criaturas iniciais
        criarCriaturasIniciais();
        
        // Cria guardião se ativo
        if (parametros.isGuardiacaoAtiva()) {
            guardiao = new GuardiaoHorizonte(parametros.getPosicaoInicialGuardiao());
        }
        
        // Atualiza status
        this.simulacaoAtiva = true;
        this.pausada = false;
        this.status = StatusSimulacao.EXECUTANDO;
        
        System.out.println("Simulação iniciada com " + criaturas.size() + " criaturas");
    }
    
    /**
     * Cria as criaturas iniciais da simulação
     */
    private void criarCriaturasIniciais() {
        for (int i = 0; i < parametros.getNumeroCriaturas(); i++) {
            double posicao = random.nextDouble() * 100; // Posição entre 0 e 100
            Criatura criatura = new Criatura(i + 1, posicao);
            criatura.receberMoedas(parametros.getMoedasPorCriatura());
            criaturas.add(criatura);
        }
    }
    
    /**
     * Executa uma única iteração da simulação
     */
    public StatusSimulacao executarIteracao() {
        if (!simulacaoAtiva || pausada) {
            return status;
        }
        
        iteracaoAtual++;
        
        try {
            // 1. Processa movimento das criaturas
            processarCriaturas();
            
            // 2. Atualiza clusters
            atualizarClusters();
            
            // 3. Processa guardião
            if (guardiao != null && guardiao.isAtivo()) {
                processarGuardiao();
            }
            
            // 4. Executa interações (roubos, eliminações)
            executarInteracoes();
            
            // 5. Atualiza estatísticas
            atualizarEstatisticas();
            
            // 6. Verifica condições de término
            if (verificarCondicaoTermino()) {
                finalizarSimulacao();
            }
            
        } catch (Exception e) {
            System.err.println("Erro na iteração " + iteracaoAtual + ": " + e.getMessage());
            status = StatusSimulacao.ERRO;
            simulacaoAtiva = false;
        }
        
        return status;
    }
    
    /**
     * Processa o movimento e ações das criaturas
     */
    private void processarCriaturas() {
        double movimentoTotal = 0.0;
        int criaturasProcessadas = 0;
        
        for (Criatura criatura : criaturas) {
            if (criatura.isAtiva()) {
                double posicaoAnterior = criatura.getPosicao();
                
                // Move a criatura
                criatura.mover(parametros.getFatorMovimento());
                
                // Calcula movimento para estatísticas
                double movimento = Math.abs(criatura.getPosicao() - posicaoAnterior);
                movimentoTotal += movimento;
                criaturasProcessadas++;
            }
        }
        
        // Atualiza média de movimento
        if (criaturasProcessadas > 0) {
            double movimentoMedioIteracao = movimentoTotal / criaturasProcessadas;
            movimentoMedio = ((movimentoMedio * (iteracaoAtual - 1)) + movimentoMedioIteracao) / iteracaoAtual;
        }
    }
    
    /**
     * Atualiza a formação e dissolução de clusters
     */
    private void atualizarClusters() {
        // Remove clusters inativos
        clusters.removeIf(cluster -> !cluster.isAtivo());
        
        // Atualiza clusters existentes
        for (Cluster cluster : clusters) {
            cluster.atualizarEstatisticas();
        }
        
        // Verifica formação de novos clusters
        clusterManager.verificarFormacaoClusters(criaturas, clusters);
        
        // Processa clusters (movimento, reorganização)
        for (Cluster cluster : clusters) {
            if (cluster.isAtivo()) {
                cluster.mover(parametros.getFatorMovimento());
            }
        }
    }
    
    /**
     * Processa as ações do guardião
     */
    private void processarGuardiao() {
        // Move o guardião
        guardiao.mover(clusters, criaturas);
        
        // Verifica se guardião deve ser eliminado
        if (guardiao.verificarEliminacao(clusters, criaturas)) {
            System.out.println("Guardião foi eliminado na iteração " + iteracaoAtual);
        }
    }
    
    /**
     * Executa interações entre entidades (roubos, eliminações)
     */
    private void executarInteracoes() {
        if (!parametros.isPermitirRoubo()) {
            return;
        }
        
        // Roubos entre criaturas próximas
        executarRoubosEntreCriaturas();
        
        // Roubos entre clusters
        executarRoubosEntreClusters();
        
        // Ações do guardião
        if (guardiao != null && guardiao.isAtivo()) {
            executarAcoesGuardiao();
        }
    }
    
    /**
     * Executa roubos entre criaturas próximas
     */
    private void executarRoubosEntreCriaturas() {
        for (int i = 0; i < criaturas.size(); i++) {
            Criatura criatura1 = criaturas.get(i);
            if (!criatura1.isAtiva()) continue;
            
            for (int j = i + 1; j < criaturas.size(); j++) {
                Criatura criatura2 = criaturas.get(j);
                if (!criatura2.isAtiva()) continue;
                
                double distancia = Math.abs(criatura1.getPosicao() - criatura2.getPosicao());
                
                if (distancia <= 2.0 && Math.random() < 0.3) { // 30% chance de roubo
                    if (criatura1.getMoedas() > criatura2.getMoedas()) {
                        int moedasRoubadas = criatura2.perderMoedas(1);
                        criatura1.receberMoedas(moedasRoubadas);
                        totalRoubos++;
                    } else if (criatura2.getMoedas() > criatura1.getMoedas()) {
                        int moedasRoubadas = criatura1.perderMoedas(1);
                        criatura2.receberMoedas(moedasRoubadas);
                        totalRoubos++;
                    }
                }
            }
        }
    }
    
    /**
     * Executa roubos entre clusters próximos
     */
    private void executarRoubosEntreClusters() {
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster1 = clusters.get(i);
            if (!cluster1.isAtivo()) continue;
            
            for (int j = i + 1; j < clusters.size(); j++) {
                Cluster cluster2 = clusters.get(j);
                if (!cluster2.isAtivo()) continue;
                
                double distancia = Math.abs(cluster1.getPosicaoMedia() - cluster2.getPosicaoMedia());
                
                if (distancia <= 5.0 && Math.random() < 0.2) { // 20% chance de roubo
                    if (cluster1.getTotalMoedas() > cluster2.getTotalMoedas()) {
                        cluster1.roubarMoedasProximoCluster(cluster2);
                        totalRoubos++;
                    } else {
                        cluster2.roubarMoedasProximoCluster(cluster1);
                        totalRoubos++;
                    }
                }
            }
        }
    }
    /** 
    /**
     * Executa ações do guardião (eliminações)
     */
    private void executarAcoesGuardiao() {
        // Tenta eliminar clusters próximos
        for (Cluster cluster : clusters) {
            if (cluster.isAtivo() && guardiao.eliminarCluster(cluster)) {
                System.out.println("Guardião eliminou cluster na iteração " + iteracaoAtual);
                totalEliminacoes++;
                break; // Apenas uma eliminação por iteração
            }
        }
        
        // Tenta eliminar criaturas próximas se não eliminou clusters
        for (Criatura criatura : criaturas) {
            if (criatura.isAtiva() && guardiao.eliminarCriatura(criatura)) {
                System.out.println("Guardião eliminou criatura na iteração " + iteracaoAtual);
                totalEliminacoes++;
                break; // Apenas uma eliminação por iteração
            }
        }
    }
    /**
     * Atualiza estatísticas da simulação
     */
    private void atualizarEstatisticas() {
        // Conta entidades ativas
        int criaturasAtivas = (int) criaturas.stream().filter(Criatura::isAtiva).count();
        int clustersAtivos = (int) clusters.stream().filter(Cluster::isAtivo).count();
        
        // Atualiza status com informações atuais
        status.setIteracaoAtual(iteracaoAtual);
        status.setCriaturasAtivas(criaturasAtivas);
        status.setClustersAtivos(clustersAtivos);
        status.setTotalRoubos(totalRoubos);
        status.setTotalEliminacoes(totalEliminacoes);
        status.setMovimentoMedio(movimentoMedio);
        
        if (guardiao != null) {
            status.setMoedasGuardiao(guardiao.getMoedas());
            status.setPosicaoGuardiao(guardiao.getPosicao());
        }
    }
    
    /**
     * Verifica se a simulação deve terminar
     */
    private boolean verificarCondicaoTermino() {
        // Verifica limite de iterações
        if (iteracaoAtual >= maxIteracoes) {
            status.setMotivoTermino("Limite de iterações atingido");
            return true;
        }
        
        // Conta entidades ativas
        int criaturasAtivas = (int) criaturas.stream().filter(Criatura::isAtiva).count();
        int clustersAtivos = (int) clusters.stream().filter(Cluster::isAtivo).count();
        
        // Condição de vitória: apenas guardião
        if (guardiao != null && guardiao.isAtivo() && criaturasAtivas == 0 && clustersAtivos == 0) {
            status.setMotivoTermino("Vitória: Apenas guardião sobreviveu");
            status.setBemSucedida(true);
            return true;
        }
        
        // Condição de vitória: guardião + 1 criatura com guardião tendo mais moedas
        if (guardiao != null && guardiao.isAtivo() && criaturasAtivas == 1 && clustersAtivos == 0) {
            Criatura criaturaSobrevivente = criaturas.stream()
                .filter(Criatura::isAtiva)
                .findFirst()
                .orElse(null);
            
            if (criaturaSobrevivente != null && guardiao.getMoedas() > criaturaSobrevivente.getMoedas()) {
                status.setMotivoTermino("Vitória: Guardião com mais moedas que única criatura");
                status.setBemSucedida(true);
                return true;
            }
        }
        
        // Condição de derrota: sem guardião e apenas 1 ou menos entidades
        if (guardiao == null || !guardiao.isAtivo()) {
            if (criaturasAtivas + clustersAtivos <= 1) {
                status.setMotivoTermino("Derrota: Guardião eliminado");
                status.setBemSucedida(false);
                return true;
            }
        }
        
        // Condição de empate: todas as entidades inativas
        if (criaturasAtivas == 0 && clustersAtivos == 0 && (guardiao == null || !guardiao.isAtivo())) {
            status.setMotivoTermino("Empate: Todas as entidades eliminadas");
            status.setBemSucedida(false);
            return true;
        }
        
        return false;
    }
    
    /**
     * Finaliza a simulação
     */
    private void finalizarSimulacao() {
        simulacaoAtiva = false;
        status.setEstado(StatusSimulacao.EstadoSimulacao.FINALIZADA);
        
        System.out.println("Simulação finalizada na iteração " + iteracaoAtual);
        System.out.println("Motivo: " + status.getMotivoTermino());
        System.out.println("Bem-sucedida: " + status.isBemSucedida());
    }
    
    /**
     * Pausa a simulação
     */
    public void pausar() {
        if (simulacaoAtiva) {
            pausada = true;
            status.setEstado(StatusSimulacao.EstadoSimulacao.PAUSADA);
        }
    }
    
    /**
     * Retoma a simulação pausada
     */
    public void retomar() {
        if (simulacaoAtiva && pausada) {
            pausada = false;
            status.setEstado(StatusSimulacao.EstadoSimulacao.EXECUTANDO);
        }
    }
    
    /**
     * Para a simulação completamente
     */
    public void parar() {
        simulacaoAtiva = false;
        pausada = false;
        status.setEstado(StatusSimulacao.EstadoSimulacao.PARADA);
    }
    
    /**
     * Executa a simulação completa até o fim
     */
    public StatusSimulacao executarSimulacaoCompleta() {
        while (simulacaoAtiva && !pausada) {
            executarIteracao();
            
            // Pausa curta para permitir visualização
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return status;
    }
    
    // Getters
    public List<Criatura> getCriaturas() { return new ArrayList<>(criaturas); }
    public List<Cluster> getClusters() { return new ArrayList<>(clusters); }
    public GuardiaoHorizonte getGuardiao() { return guardiao; }
    public StatusSimulacao getStatus() { return status; }
    public int getIteracaoAtual() { return iteracaoAtual; }
    public boolean isSimulacaoAtiva() { return simulacaoAtiva; }
    public boolean isPausada() { return pausada; }
    public int getTotalRoubos() { return totalRoubos; }
    public int getTotalEliminacoes() { return totalEliminacoes; }
    public double getMovimentoMedio() { return movimentoMedio; }
}

