package org.example;

import org.example.model.Usuario;
import org.example.service.SimuladorService;

public class TestIteracao50 {

    public static void main(String[] args) {
        SimuladorService simuladorService = new SimuladorService();
        Usuario usuario = new Usuario("TestUser", "TestUser", "test@example.com");

        // Initialize simulation with same parameters as test
        simuladorService.criarNovaSimulacao(usuario, 5, 100);

        // Run exactly 50 iterations as in the test
        for (int i = 0; i < 50; i++) {
            if (!simuladorService.temSimulacaoAtiva()) {
                System.out.println("Simulation ended at iteration: " + i);
                break;
            }
            simuladorService.executarProximaIteracao();
        }

        // Check state after 50 iterations
        System.out.println("After 50 iterations:");
        System.out.println("temSimulacaoAtiva: " + simuladorService.temSimulacaoAtiva());
        System.out.println("isConcluida: " + simuladorService.getSimulacaoAtual().isConcluida());
        System.out.println("Estado: " + simuladorService.obterEstadoAtual());

        var simulacao = simuladorService.getSimulacaoAtual();
        System.out.println("Active creatures: " + simulacao.getCriaturas().stream().mapToInt(c -> c.isAtiva() ? 1 : 0).sum());
        System.out.println("Total clusters: " + simulacao.getClusters().size());
        System.out.println("Guardian coins: " + simulacao.getGuardiao().getMoedas());
        System.out.println("Iterations: " + simulacao.getIteracoes());
    }
}
