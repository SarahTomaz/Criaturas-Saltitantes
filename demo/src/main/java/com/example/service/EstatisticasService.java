package com.example.service;

import com.example.model.entities.Usuario;
import com.example.model.entities.ResultadoSimulacao;
import com.example.repository.UsuarioRepository;
import java.util.List;

public class EstatisticasService {

    private final UsuarioRepository usuarioRepository;

    public EstatisticasService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void registrarSimulacao(Usuario usuario, ResultadoSimulacao resultado) {
        boolean vitoria = resultado.getTipoResultado() == ResultadoSimulacao.TipoResultado.VITORIA_GUARDIAO;
        usuario.incrementarPontuacao(vitoria, resultado.getPontuacao() / 10);
        usuarioRepository.atualizarUsuario(usuario);
    }

    public String getEstatisticasUsuario(Usuario usuario) {
        List<ResultadoSimulacao> resultados = usuarioRepository.getHistoricoSimulacoes(usuario.getLogin());
        long totalSimulacoes = resultados.size();
        long simulacoesVencidas = resultados.stream()
                .filter(r -> r.getTipoResultado() == ResultadoSimulacao.TipoResultado.VITORIA_GUARDIAO)
                .count();

        return String.format(
                "Estatísticas de %s:\n"
                + "- Total de simulações: %d\n"
                + "- Simulações bem-sucedidas: %d (%.1f%%)\n"
                + "- Pontuação total: %d",
                usuario.getLogin(), totalSimulacoes, simulacoesVencidas,
                (totalSimulacoes > 0 ? (simulacoesVencidas * 100.0 / totalSimulacoes) : 0),
                usuario.getPontuacao()
        );
    }
}
