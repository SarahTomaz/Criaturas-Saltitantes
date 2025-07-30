package debug;

import org.example.model.Criatura;
import org.example.model.Usuario;
import org.example.service.SimuladorService;
import org.example.service.UsuarioService;

public class DebugSimulacao3 {

    public static void main(String[] args) {
        // Setup
        UsuarioService usuarioService = new UsuarioService();
        SimuladorService simuladorService = new SimuladorService();

        usuarioService.cadastrarUsuario("testuser", "password", "avatar.png");
        Usuario usuario = usuarioService.autenticar("testuser", "password");

        // Create simulation
        simuladorService.criarNovaSimulacao(usuario, 5, 100);

        System.out.println("=== SIMULATION START ===");
        System.out.println("Guardian initial position: " + simuladorService.getSimulacaoAtual().getGuardiao().getPosicao());
        System.out.println("Guardian initial coins: " + simuladorService.getSimulacaoAtual().getGuardiao().getMoedas());
        System.out.println("Active creatures: " + simuladorService.getSimulacaoAtual().getCriaturas().stream()
                .filter(Criatura::isAtiva).count());
        System.out.println();

        // Run and observe every 10 iterations
        for (int i = 0; i < 50; i++) {
            boolean canContinue = simuladorService.executarProximaIteracao();

            if ((i + 1) % 10 == 0 || !canContinue) {
                System.out.println("=== ITERATION " + (i + 1) + " ===");
                System.out.println("Can continue: " + canContinue);
                System.out.println("Guardian position: " + simuladorService.getSimulacaoAtual().getGuardiao().getPosicao());
                System.out.println("Guardian coins: " + simuladorService.getSimulacaoAtual().getGuardiao().getMoedas());
                System.out.println("Active creatures: " + simuladorService.getSimulacaoAtual().getCriaturas().stream()
                        .filter(Criatura::isAtiva).count());
                System.out.println("Clusters: " + simuladorService.getSimulacaoAtual().getClusters().size());
                System.out.println("Status: " + simuladorService.getSimulacaoAtual().getStatus());
                System.out.println();
            }

            if (!canContinue) {
                System.out.println("Simulation ended at iteration: " + (i + 1));
                break;
            }
        }
    }
}
