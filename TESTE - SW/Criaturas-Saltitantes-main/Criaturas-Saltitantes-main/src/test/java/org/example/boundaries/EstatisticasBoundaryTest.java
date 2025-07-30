package org.example.boundaries;

import org.example.model.Usuario;
import org.example.service.EstatisticasService;
import org.example.service.SimuladorService;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EstatisticasBoundaryTest {

    @Test
    @DisplayName("Teste de fronteira - Sem usuários cadastrados")
    void semUsuariosCadastrados() {
        UsuarioService usuarioService = new UsuarioService();
        usuarioService.limparUsuarios(); // Limpar dados persistidos
        EstatisticasService estatisticasService = new EstatisticasService(usuarioService, new SimuladorService());

        assertEquals(0, estatisticasService.getTotalUsuarios());
        assertEquals(0, estatisticasService.getTotalSimulacoesGlobal());
        assertEquals(0, estatisticasService.getTotalSimulacoesBemSucedidas());
        assertEquals(0.0, estatisticasService.getTaxaSucessoGlobal());
        assertTrue(estatisticasService.getRankingUsuarios().isEmpty());
    }

    @Test
    @DisplayName("Teste de fronteira - Usuário sem simulações")
    void usuarioSemSimulacoes() {
        UsuarioService usuarioService = new UsuarioService();
        usuarioService.limparUsuarios(); // Limpar dados persistidos
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");

        EstatisticasService estatisticasService = new EstatisticasService(usuarioService, new SimuladorService());

        assertEquals(1, estatisticasService.getTotalUsuarios());
        assertEquals(0, estatisticasService.getTotalSimulacoesGlobal());
        assertEquals(0, estatisticasService.getTotalSimulacoesBemSucedidas());
        assertEquals(0.0, estatisticasService.getTaxaSucessoGlobal());
    }

    @Test
    @DisplayName("Teste de fronteira - Taxa 100% de sucesso")
    void taxaCemPorcentoSucesso() {
        UsuarioService usuarioService = new UsuarioService();
        usuarioService.limparUsuarios(); // Limpar dados persistidos

        // Cadastrar usuário pelo serviço
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");

        // Buscar o usuário cadastrado e simular que ele teve 5 simulações todas bem-sucedidas
        Usuario usuario = usuarioService.autenticar("user1", "senha1");
        for (int i = 0; i < 5; i++) {
            usuario.incrementarSimulacaoBemSucedida();
        }
        usuarioService.atualizarUsuario(usuario);

        EstatisticasService estatisticasService = new EstatisticasService(usuarioService, new SimuladorService());

        assertEquals(1.0, estatisticasService.getTaxaSucessoGlobal(), 0.001);
    }
}
