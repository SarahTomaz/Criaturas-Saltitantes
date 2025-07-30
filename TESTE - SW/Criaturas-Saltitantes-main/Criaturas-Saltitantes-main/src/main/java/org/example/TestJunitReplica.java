package org.example;

import org.example.model.Usuario;
import org.example.service.SimuladorService;

public class TestJunitReplica {

    public static void main(String[] args) {
        SimuladorService simuladorService = new SimuladorService();
        Usuario usuario = new Usuario("Test User", "testuser", "test@test.com");

        // Exactly replicate the test
        simuladorService.criarNovaSimulacao(usuario, 5, 100);

        // Executa até a metade (exactly like the test)
        for (int i = 0; i < 50; i++) {
            simuladorService.executarProximaIteracao();
        }

        // Verifica estado intermediário (exactly like the test)
        boolean temSimulacaoAtiva = simuladorService.temSimulacaoAtiva();
        boolean isConcluida = simuladorService.getSimulacaoAtual().isConcluida();
        boolean contemIteracao = simuladorService.obterEstadoAtual().contains("Iteração");

        System.out.println("Test results:");
        System.out.println("temSimulacaoAtiva: " + temSimulacaoAtiva + " (expected: true)");
        System.out.println("isConcluida: " + isConcluida + " (expected: false)");
        System.out.println("contemIteracao: " + contemIteracao + " (expected: true)");

        System.out.println("\nTest would " + (temSimulacaoAtiva && !isConcluida && contemIteracao ? "PASS" : "FAIL"));

        System.out.println("\nDetailed state:");
        System.out.println("Estado: " + simuladorService.obterEstadoAtual());
    }
}
