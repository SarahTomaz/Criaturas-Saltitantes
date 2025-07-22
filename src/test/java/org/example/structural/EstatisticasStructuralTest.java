package org.example.structural;

import org.example.model.Usuario;
import org.example.service.EstatisticasService;
import org.example.service.SimuladorService;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EstatisticasStructuralTest {

    private EstatisticasService estatisticasService;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
        estatisticasService = new EstatisticasService(usuarioService, new SimuladorService());
    }

    @Test
    @DisplayName("MC/DC para getEstatisticasUsuario - Todas condições")
    void getEstatisticasUsuario_MCDC() {
        // Condição 1: Usuário não existe
        assertEquals("Usuário não encontrado.", estatisticasService.getEstatisticasUsuario("inexistente"));

        // Condição 2: Usuário existe sem simulações
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        String statsUsuarioSemSimulacoes = estatisticasService.getEstatisticasUsuario("user1");
        assertAll(
                () -> assertTrue(statsUsuarioSemSimulacoes.contains("user1")),
                () -> assertTrue(statsUsuarioSemSimulacoes.contains("0 simulações"))
        );

        // Condição 3: Usuário existe com simulações
        Usuario usuario = usuarioService.autenticar("user1", "senha1");
        usuario.setPontuacao(2);
        usuario.setTotalSimulacoes(3);
        String statsUsuarioComSimulacoes = estatisticasService.getEstatisticasUsuario("user1");
        assertTrue(statsUsuarioComSimulacoes.contains("2 simulações bem-sucedidas"));
    }

    @Test
    @DisplayName("Cobertura de caminhos para gerarRelatorioCompleto")
    void gerarRelatorioCompleto_CoberturaCaminhos() {
        // Caminho 1: Sem usuários
        String relatorioSemUsuarios = estatisticasService.gerarRelatorioCompleto();
        assertTrue(relatorioSemUsuarios.contains("Nenhum usuário cadastrado"));

        // Caminho 2: Com usuários
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");
        String relatorioComUsuarios = estatisticasService.gerarRelatorioCompleto();
        assertAll(
                () -> assertTrue(relatorioComUsuarios.contains("user2")),
                () -> assertTrue(relatorioComUsuarios.contains("ESTATÍSTICAS GLOBAIS")),
                () -> assertTrue(relatorioComUsuarios.contains("RANKING DE USUÁRIOS"))
        );
    }
}
