package org.example.service;

import java.util.ArrayList;
import java.util.List;

import org.example.model.Cluster;
import org.example.model.Criatura;
import org.example.model.GuardiaoHorizonte;
import org.example.model.Simulacao;
import org.example.model.Usuario;

public class SimuladorService {

    private Simulacao simulacaoAtual;
    private List<Simulacao> historicoSimulacoes;

    public SimuladorService() {
        this.historicoSimulacoes = new ArrayList<>();
    }

    public Simulacao criarNovaSimulacao(Usuario usuario, int numCriaturas, int maxIteracoes) {
        if (usuario == null) {
            throw new NullPointerException("Usuário não pode ser nulo");
        }
        if (numCriaturas < 1 || numCriaturas > 100) {
            throw new IllegalArgumentException("Número de criaturas deve estar entre 1 e 100");
        }
        if (maxIteracoes < 1 || maxIteracoes > 10000) {
            throw new IllegalArgumentException("Máximo de iterações deve estar entre 1 e 10000");
        }

        simulacaoAtual = new Simulacao(usuario, numCriaturas, maxIteracoes);
        return simulacaoAtual;
    }

    public boolean executarProximaIteracao() {
        if (simulacaoAtual == null) {
            throw new IllegalStateException("Nenhuma simulação ativa");
        }

        boolean continuar = simulacaoAtual.executarIteracao();

        if (!continuar) {
            // Simulação terminou
            simulacaoAtual.finalizar();
            historicoSimulacoes.add(simulacaoAtual);
        }

        return continuar;
    }

    public Simulacao executarSimulacaoCompleta(Usuario usuario, int numCriaturas, int maxIteracoes) {
        // Se já existe uma simulação ativa com os mesmos parâmetros, mas já teve iterações, finalize-a
        if (simulacaoAtual != null && !simulacaoAtual.isConcluida()
                && simulacaoAtual.getUsuario().equals(usuario)
                && simulacaoAtual.getCriaturas().size() == numCriaturas
                && simulacaoAtual.getMaxIteracoes() == maxIteracoes) {

            // Se a simulação já teve iterações executadas, finalize e crie nova
            if (simulacaoAtual.getIteracoes() > 0) {
                simulacaoAtual.finalizar();
                historicoSimulacoes.add(simulacaoAtual);

                criarNovaSimulacao(usuario, numCriaturas, maxIteracoes);

                while (executarProximaIteracao()) {
                    // Continua executando até terminar
                }
            } else {
                // Se não teve iterações ainda, apenas complete-a
                while (executarProximaIteracao()) {
                    // Continua executando até terminar
                }
            }
        } else {
            // Se há uma simulação diferente ativa, finalize-a primeiro
            if (simulacaoAtual != null && !simulacaoAtual.isConcluida()) {
                simulacaoAtual.finalizar();
                historicoSimulacoes.add(simulacaoAtual);
            }

            // Cria e executa uma nova simulação
            criarNovaSimulacao(usuario, numCriaturas, maxIteracoes);

            while (executarProximaIteracao()) {
                // Continua executando até terminar
            }
        }

        return simulacaoAtual;
    }

    public String obterEstadoAtual() {
        if (simulacaoAtual == null) {
            return "Nenhuma simulação ativa";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADO DA SIMULAÇÃO ===\n");
        sb.append(simulacaoAtual.getStatus()).append("\n\n");

        // Criaturas ativas
        List<Criatura> criaturasAtivas = simulacaoAtual.getCriaturas().stream()
                .filter(Criatura::isAtiva)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        sb.append("Criaturas Ativas: ").append(criaturasAtivas.size()).append("\n");
        for (Criatura c : criaturasAtivas) {
            sb.append("  ").append(c).append("\n");
        }

        // Clusters
        sb.append("\nClusters: ").append(simulacaoAtual.getClusters().size()).append("\n");
        for (Cluster cluster : simulacaoAtual.getClusters()) {
            sb.append("  ").append(cluster).append("\n");
        }

        // Guardião
        sb.append("\nGuardião: ").append(simulacaoAtual.getGuardiao()).append("\n");

        return sb.toString();
    }

    public List<String> obterPosicoes() {
        if (simulacaoAtual == null) {
            return new ArrayList<>();
        }

        List<String> posicoes = new ArrayList<>();

        // Criaturas ativas
        simulacaoAtual.getCriaturas().stream()
                .filter(Criatura::isAtiva)
                .forEach(c -> posicoes.add(String.format("C%d: %.2f (%d moedas)",
                c.getId(), c.getPosicao(), c.getMoedas())));

        // Clusters
        simulacaoAtual.getClusters().forEach(cl
                -> posicoes.add(String.format("Cluster%d: %.2f (%d moedas)",
                        cl.getId(), cl.getPosicao(), cl.getTotalMoedas())));

        // Guardião
        GuardiaoHorizonte g = simulacaoAtual.getGuardiao();
        posicoes.add(String.format("Guardião: %.2f (%d moedas)",
                g.getPosicao(), g.getMoedas()));

        return posicoes;
    }

    public boolean temSimulacaoAtiva() {
        return simulacaoAtual != null && !simulacaoAtual.isConcluida();
    }

    public Simulacao getSimulacaoAtual() {
        return simulacaoAtual;
    }

    public List<Simulacao> getHistoricoSimulacoes() {
        return List.copyOf(historicoSimulacoes);
    }

    public void limparHistorico() {
        historicoSimulacoes.clear();
    }

    public int getNumeroSimulacoesBemSucedidas() {
        return (int) historicoSimulacoes.stream()
                .filter(Simulacao::isBemSucedida)
                .count();
    }

    public int getTotalSimulacoes() {
        return historicoSimulacoes.size();
    }

    public double getTaxaSucessoGeral() {
        if (historicoSimulacoes.isEmpty()) {
            return 0.0;
        }
        return (double) getNumeroSimulacoesBemSucedidas() / getTotalSimulacoes();
    }

    public String executarSimulacao(Usuario usuario) {
        // Executar uma simulação completa com parâmetros padrão
        Simulacao simulacao = executarSimulacaoCompleta(usuario, 5, 1000);

        StringBuilder resultado = new StringBuilder();
        resultado.append("=== RESULTADO DA SIMULAÇÃO ===\n");
        resultado.append("Usuário: ").append(usuario.getLogin()).append("\n");
        resultado.append("Status: ").append(simulacao.getStatus()).append("\n");
        resultado.append("Iterações executadas: ").append(simulacao.getIteracoes()).append("\n");
        resultado.append("Criaturas restantes: ").append(simulacao.getCriaturas().stream()
                .filter(Criatura::isAtiva).count()).append("\n");
        resultado.append("Clusters restantes: ").append(simulacao.getClusters().size()).append("\n");
        resultado.append("Moedas do guardião: ").append(simulacao.getGuardiao().getMoedas()).append("\n");

        if (simulacao.isBemSucedida()) {
            resultado.append("✅ SIMULAÇÃO BEM-SUCEDIDA!\n");
        } else {
            resultado.append("❌ Simulação não foi bem-sucedida.\n");
        }

        return resultado.toString();
    }
}
