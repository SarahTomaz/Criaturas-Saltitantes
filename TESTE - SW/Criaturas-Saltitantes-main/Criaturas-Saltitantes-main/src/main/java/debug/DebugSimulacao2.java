package debug;

import org.example.model.Usuario;
import org.example.service.SimuladorService;
import org.example.service.UsuarioService;

public class DebugSimulacao2 {

    public static void main(String[] args) {
        // Setup
        UsuarioService usuarioService = new UsuarioService();
        SimuladorService simuladorService = new SimuladorService();

        usuarioService.cadastrarUsuario("testuser", "password", "avatar.png");
        Usuario usuario = usuarioService.autenticar("testuser", "password");

        // Create simulation
        simuladorService.criarNovaSimulacao(usuario, 5, 100);

        // Run for 50 iterations
        System.out.println("Initial state:");
        System.out.println("Active: " + simuladorService.temSimulacaoAtiva());
        System.out.println("Concluded: " + simuladorService.getSimulacaoAtual().isConcluida());
        System.out.println("History size: " + simuladorService.getHistoricoSimulacoes().size());
        System.out.println();

        for (int i = 0; i < 50; i++) {
            boolean canContinue = simuladorService.executarProximaIteracao();
            if (!canContinue) {
                System.out.println("Simulation ended at iteration: " + (i + 1));
                break;
            }
        }

        System.out.println("After 50 iterations (or end):");
        System.out.println("Active: " + simuladorService.temSimulacaoAtiva());
        System.out.println("Concluded: " + simuladorService.getSimulacaoAtual().isConcluida());
        System.out.println("Status: " + simuladorService.getSimulacaoAtual().getStatus());
        System.out.println("obterEstadoAtual contains 'Iteração': "
                + simuladorService.obterEstadoAtual().contains("Iteração"));
        System.out.println("History size: " + simuladorService.getHistoricoSimulacoes().size());
        System.out.println();

        // Execute full simulation
        simuladorService.executarSimulacaoCompleta(usuario, 5, 100);

        System.out.println("After executarSimulacaoCompleta:");
        System.out.println("Active: " + simuladorService.temSimulacaoAtiva());
        System.out.println("History size: " + simuladorService.getHistoricoSimulacoes().size());
        System.out.println("Taxa sucesso: " + simuladorService.getTaxaSucessoGeral());
    }
}
