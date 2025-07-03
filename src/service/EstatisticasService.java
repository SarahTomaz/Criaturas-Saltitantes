package service;

import model.Usuario;
import java.util.List;
import java.util.stream.Collectors;

public class EstatisticasService {

    private UsuarioService usuarioService;
    private SimuladorService simuladorService;

    public EstatisticasService(UsuarioService usuarioService, SimuladorService simuladorService) {
        this.usuarioService = usuarioService;
        this.simuladorService = simuladorService;
    }

    public String gerarRelatorioCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DE ESTATÍSTICAS ===\n\n");

        // Estatísticas por usuário
        sb.append("ESTATÍSTICAS POR USUÁRIO:\n");
        sb.append("-".repeat(50)).append("\n");

        List<Usuario> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            sb.append("Nenhum usuário cadastrado.\n\n");
        } else {
            for (Usuario usuario : usuarios) {
                sb.append(String.format("Usuário: %s\n", usuario.getLogin()));
                sb.append(String.format("  Avatar: %s\n", usuario.getAvatar()));
                sb.append(String.format("  Simulações bem-sucedidas: %d\n", usuario.getPontuacao()));
                sb.append(String.format("  Total de simulações: %d\n", usuario.getTotalSimulacoes()));
                sb.append(String.format("  Taxa de sucesso: %.2f%%\n", usuario.getTaxaSucesso() * 100));
                sb.append("\n");
            }
        }

        // Estatísticas globais
        sb.append("ESTATÍSTICAS GLOBAIS:\n");
        sb.append("-".repeat(50)).append("\n");
        sb.append(String.format("Total de usuários cadastrados: %d\n", getTotalUsuarios()));
        sb.append(String.format("Total de simulações executadas: %d\n", getTotalSimulacoesGlobal()));
        sb.append(String.format("Total de simulações bem-sucedidas: %d\n", getTotalSimulacoesBemSucedidas()));
        sb.append(String.format("Taxa de sucesso global: %.2f%%\n", getTaxaSucessoGlobal() * 100));
        sb.append(String.format("Média de simulações por usuário: %.2f\n", getMediaSimulacoesPorUsuario()));
        sb.append(String.format("Média de simulações bem-sucedidas por usuário: %.2f\n", getMediaSimulacoesBemSucedidasPorUsuario()));

        // Ranking de usuários
        sb.append("\nRANKING DE USUÁRIOS (por simulações bem-sucedidas):\n");
        sb.append("-".repeat(50)).append("\n");
        List<Usuario> ranking = getRankingUsuarios();
        for (int i = 0; i < ranking.size(); i++) {
            Usuario usuario = ranking.get(i);
            sb.append(String.format("%d. %s - %d simulações bem-sucedidas\n",
                    i + 1, usuario.getLogin(), usuario.getPontuacao()));
        }

        return sb.toString();
    }

    public int getTotalUsuarios() {
        return usuarioService.getTotalUsuarios();
    }

    public int getTotalSimulacoesGlobal() {
        return usuarioService.listarUsuarios().stream()
                .mapToInt(Usuario::getTotalSimulacoes)
                .sum();
    }

    public int getTotalSimulacoesBemSucedidas() {
        return usuarioService.listarUsuarios().stream()
                .mapToInt(Usuario::getPontuacao)
                .sum();
    }

    public double getTaxaSucessoGlobal() {
        int total = getTotalSimulacoesGlobal();
        if (total == 0) {
            return 0.0;
        }
        return (double) getTotalSimulacoesBemSucedidas() / total;
    }

    public double getMediaSimulacoesPorUsuario() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            return 0.0;
        }
        return (double) getTotalSimulacoesGlobal() / usuarios.size();
    }

    public double getMediaSimulacoesBemSucedidasPorUsuario() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            return 0.0;
        }
        return (double) getTotalSimulacoesBemSucedidas() / usuarios.size();
    }

    public List<Usuario> getRankingUsuarios() {
        return usuarioService.listarUsuarios().stream()
                .sorted((u1, u2) -> Integer.compare(u2.getPontuacao(), u1.getPontuacao()))
                .collect(Collectors.toList());
    }

    public String getEstatisticasUsuario(String login) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        Usuario usuario = usuarios.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .findFirst()
                .orElse(null);

        if (usuario == null) {
            return "Usuário não encontrado.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== ESTATÍSTICAS DE %s ===\n", usuario.getLogin().toUpperCase()));
        sb.append(String.format("Avatar: %s\n", usuario.getAvatar()));
        sb.append(String.format("Simulações bem-sucedidas: %d\n", usuario.getPontuacao()));
        sb.append(String.format("Total de simulações: %d\n", usuario.getTotalSimulacoes()));
        sb.append(String.format("Taxa de sucesso: %.2f%%\n", usuario.getTaxaSucesso() * 100));

        // Posição no ranking
        List<Usuario> ranking = getRankingUsuarios();
        int posicao = ranking.indexOf(usuario) + 1;
        sb.append(String.format("Posição no ranking: %d de %d\n", posicao, ranking.size()));

        return sb.toString();
    }

    public String getResumoEstatisticas() {
        return String.format(
                "Usuários: %d | Simulações: %d | Bem-sucedidas: %d | Taxa: %.1f%%",
                getTotalUsuarios(),
                getTotalSimulacoesGlobal(),
                getTotalSimulacoesBemSucedidas(),
                getTaxaSucessoGlobal() * 100
        );
    }
}
