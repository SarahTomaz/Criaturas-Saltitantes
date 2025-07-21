package service;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de performance e carga para UsuarioService Verifica comportamento sob
 * alta carga e requisitos de performance
 */
public class TestUsuarioServicePerformance {

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        limprarDadosPersistentes();
        usuarioService = new UsuarioService();
    }

    private void limprarDadosPersistentes() {
        File dataDir = new File("data");
        if (dataDir.exists()) {
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            dataDir.delete();
        }
    }

    // ==================== TESTES DE PERFORMANCE TEMPORAL ====================
    @Test
    @DisplayName("Cadastro de usuário deve completar em menos de 100ms")
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    void cadastroUsuario_PerformanceTemporal() {
        // Act & Assert (timeout implícito via @Timeout)
        boolean resultado = usuarioService.cadastrarUsuario("user_perf", "senha1234", "avatar.png");
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Autenticação deve completar em menos de 50ms")
    @Timeout(value = 50, unit = TimeUnit.MILLISECONDS)
    void autenticacao_PerformanceTemporal() {
        // Arrange
        usuarioService.cadastrarUsuario("user_auth", "senha1234", "avatar.png");

        // Act & Assert (timeout implícito via @Timeout)
        Usuario usuario = usuarioService.autenticar("user_auth", "senha1234");
        assertNotNull(usuario);
    }

    @Test
    @DisplayName("Listagem de usuários deve completar em menos de 10ms")
    @Timeout(value = 10, unit = TimeUnit.MILLISECONDS)
    void listarUsuarios_PerformanceTemporal() {
        // Arrange
        for (int i = 0; i < 10; i++) {
            usuarioService.cadastrarUsuario("user" + i, "senha" + i, "avatar" + i + ".png");
        }

        // Act & Assert (timeout implícito via @Timeout)
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        assertEquals(10, usuarios.size());
    }

    // ==================== TESTES DE CARGA ====================
    @Test
    @DisplayName("Deve lidar com cadastro de muitos usuários (teste de carga)")
    void cadastroMuitosUsuarios_TesteCarga() {
        // Arrange
        int numUsuarios = 10000;
        Instant inicio = Instant.now();

        // Act
        for (int i = 0; i < numUsuarios; i++) {
            boolean resultado = usuarioService.cadastrarUsuario("user" + i, "senha" + i, "avatar" + i + ".png");
            assertTrue(resultado, "Cadastro do usuário " + i + " deve ter sucesso");
        }

        Instant fim = Instant.now();
        Duration duracao = Duration.between(inicio, fim);

        // Assert
        assertEquals(numUsuarios, usuarioService.getTotalUsuarios());

        // Performance: deve cadastrar pelo menos 100 usuários por segundo
        double usuariosPorSegundo = numUsuarios / (duracao.toMillis() / 1000.0);
        assertTrue(usuariosPorSegundo >= 100,
                String.format("Performance insuficiente: %.2f usuários/segundo (mínimo: 100)", usuariosPorSegundo));

        System.out.printf("Cadastro de %d usuários: %.2fms (%.2f usuários/segundo)%n",
                numUsuarios, duracao.toMillis(), usuariosPorSegundo);
    }

    @Test
    @DisplayName("Deve lidar com muitas autenticações (teste de carga)")
    void muitasAutenticacoes_TesteCarga() {
        // Arrange
        int numUsuarios = 1000;
        int numAutenticacoesPorUsuario = 10;

        // Cadastrar usuários
        for (int i = 0; i < numUsuarios; i++) {
            usuarioService.cadastrarUsuario("user" + i, "senha" + i, "avatar" + i + ".png");
        }

        Instant inicio = Instant.now();

        // Act
        int sucessos = 0;
        for (int i = 0; i < numUsuarios; i++) {
            for (int j = 0; j < numAutenticacoesPorUsuario; j++) {
                Usuario usuario = usuarioService.autenticar("user" + i, "senha" + i);
                if (usuario != null) {
                    sucessos++;
                }
            }
        }

        Instant fim = Instant.now();
        Duration duracao = Duration.between(inicio, fim);

        // Assert
        int totalAutenticacoes = numUsuarios * numAutenticacoesPorUsuario;
        assertEquals(totalAutenticacoes, sucessos);

        // Performance: deve autenticar pelo menos 1000 usuários por segundo
        double autenticacoesPorSegundo = totalAutenticacoes / (duracao.toMillis() / 1000.0);
        assertTrue(autenticacoesPorSegundo >= 1000,
                String.format("Performance insuficiente: %.2f autenticações/segundo (mínimo: 1000)", autenticacoesPorSegundo));

        System.out.printf("Autenticação de %d usuários: %.2fms (%.2f autenticações/segundo)%n",
                totalAutenticacoes, duracao.toMillis(), autenticacoesPorSegundo);
    }

    @Test
    @DisplayName("Deve lidar com muitas operações de busca (teste de carga)")
    void muitasBuscas_TesteCarga() {
        // Arrange
        int numUsuarios = 5000;

        // Cadastrar usuários
        for (int i = 0; i < numUsuarios; i++) {
            usuarioService.cadastrarUsuario("search_user" + i, "senha" + i, "avatar" + i + ".png");
        }

        Instant inicio = Instant.now();

        // Act - buscar usuários de forma sequencial e aleatória
        int buscasRealizadas = 0;
        int sucessos = 0;

        for (int i = 0; i < numUsuarios * 2; i++) {
            int userIndex = i % numUsuarios;
            Usuario usuario = usuarioService.autenticar("search_user" + userIndex, "senha" + userIndex);
            buscasRealizadas++;
            if (usuario != null) {
                sucessos++;
            }
        }

        Instant fim = Instant.now();
        Duration duracao = Duration.between(inicio, fim);

        // Assert
        assertEquals(buscasRealizadas, sucessos);

        // Performance: deve buscar pelo menos 2000 usuários por segundo
        double buscasPorSegundo = buscasRealizadas / (duracao.toMillis() / 1000.0);
        assertTrue(buscasPorSegundo >= 2000,
                String.format("Performance insuficiente: %.2f buscas/segundo (mínimo: 2000)", buscasPorSegundo));

        System.out.printf("Busca de %d usuários: %.2fms (%.2f buscas/segundo)%n",
                buscasRealizadas, duracao.toMillis(), buscasPorSegundo);
    }

    // ==================== TESTES DE MEMÓRIA ====================
    @Test
    @DisplayName("Deve manter uso de memória razoável com muitos usuários")
    void usoMemoria_ComMuitosUsuarios() {
        // Arrange
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Forçar garbage collection antes do teste

        long memoriaInicial = runtime.totalMemory() - runtime.freeMemory();

        // Act
        int numUsuarios = 50000;
        for (int i = 0; i < numUsuarios; i++) {
            usuarioService.cadastrarUsuario("mem_user" + i, "senha" + i, "avatar" + i + ".png");
        }

        runtime.gc(); // Forçar garbage collection após o teste
        long memoriaFinal = runtime.totalMemory() - runtime.freeMemory();

        // Assert
        assertEquals(numUsuarios, usuarioService.getTotalUsuarios());

        long memoriaUsada = memoriaFinal - memoriaInicial;
        long memoriaUsuario = memoriaUsada / numUsuarios;

        // Cada usuário não deve usar mais que 1KB em média
        assertTrue(memoriaUsuario < 1024,
                String.format("Uso de memória por usuário muito alto: %d bytes (máximo: 1024)", memoriaUsuario));

        System.out.printf("Memória usada para %d usuários: %d bytes (%.2f bytes/usuário)%n",
                numUsuarios, memoriaUsada, (double) memoriaUsuario);
    }

    // ==================== TESTES DE ESCALABILIDADE ====================
    @Test
    @DisplayName("Performance deve escalar linearmente com o número de usuários")
    void escalabilidade_PerformanceLinear() {
        // Test com diferentes quantidades de usuários
        int[] tamanhos = {100, 500, 1000, 2000};
        double[] temposPorUsuario = new double[tamanhos.length];

        for (int t = 0; t < tamanhos.length; t++) {
            // Limpar estado
            limprarDadosPersistentes();
            usuarioService = new UsuarioService();

            int numUsuarios = tamanhos[t];
            Instant inicio = Instant.now();

            // Cadastrar usuários
            for (int i = 0; i < numUsuarios; i++) {
                usuarioService.cadastrarUsuario("scale_user" + i, "senha" + i, "avatar" + i + ".png");
            }

            Instant fim = Instant.now();
            Duration duracao = Duration.between(inicio, fim);

            temposPorUsuario[t] = duracao.toMillis() / (double) numUsuarios;

            System.out.printf("Tamanho: %d usuários, Tempo por usuário: %.3fms%n",
                    numUsuarios, temposPorUsuario[t]);
        }

        // Assert - o tempo por usuário não deve crescer drasticamente
        // (permitindo alguma variação devido ao overhead fixo)
        for (int i = 1; i < temposPorUsuario.length; i++) {
            double razao = temposPorUsuario[i] / temposPorUsuario[0];
            assertTrue(razao < 3.0,
                    String.format("Performance não escalável: razão %.2f entre %d e %d usuários",
                            razao, tamanhos[i], tamanhos[0]));
        }
    }

    // ==================== TESTES DE PERSISTÊNCIA SOB CARGA ====================
    @Test
    @DisplayName("Persistência deve funcionar sob alta carga de operações")
    void persistencia_SobAltaCarga() {
        // Arrange
        int numOperacoes = 1000;
        Instant inicio = Instant.now();

        // Act - operações que disparam persistência
        for (int i = 0; i < numOperacoes; i++) {
            String user = "persist_user" + i;
            usuarioService.cadastrarUsuario(user, "senha" + i, "avatar" + i + ".png");
            usuarioService.alterarAvatar(user, "new_avatar" + i + ".png");
            usuarioService.atualizarPontuacao(user, i % 10);
        }

        Instant fim = Instant.now();
        Duration duracao = Duration.between(inicio, fim);

        // Assert
        assertEquals(numOperacoes, usuarioService.getTotalUsuarios());

        // Verificar persistência criando nova instância
        UsuarioService novoService = new UsuarioService();
        assertEquals(numOperacoes, novoService.getTotalUsuarios());

        // Performance da persistência
        double operacoesPorSegundo = (numOperacoes * 3) / (duracao.toMillis() / 1000.0); // 3 operações por usuário
        assertTrue(operacoesPorSegundo >= 500,
                String.format("Performance de persistência insuficiente: %.2f operações/segundo (mínimo: 500)", operacoesPorSegundo));

        System.out.printf("Persistência de %d operações: %.2fms (%.2f operações/segundo)%n",
                numOperacoes * 3, duracao.toMillis(), operacoesPorSegundo);
    }

    // ==================== TESTE DE RECUPERAÇÃO APÓS SOBRECARGA ====================
    @Test
    @DisplayName("Sistema deve recuperar funcionalidade após sobrecarga")
    void recuperacao_AposSobrecarga() {
        // Arrange - sobrecarregar o sistema
        int sobrecarga = 20000;
        for (int i = 0; i < sobrecarga; i++) {
            usuarioService.cadastrarUsuario("overload_user" + i, "senha" + i, "avatar" + i + ".png");
        }

        // Act - operações normais após sobrecarga
        boolean cadastroNormal = usuarioService.cadastrarUsuario("normal_user", "senha_normal", "avatar_normal.png");
        Usuario usuarioNormal = usuarioService.autenticar("normal_user", "senha_normal");
        boolean atualizacao = usuarioService.alterarAvatar("normal_user", "novo_avatar.png");

        // Assert
        assertTrue(cadastroNormal, "Cadastro deve funcionar após sobrecarga");
        assertNotNull(usuarioNormal, "Autenticação deve funcionar após sobrecarga");
        assertTrue(atualizacao, "Atualização deve funcionar após sobrecarga");
        assertEquals(sobrecarga + 1, usuarioService.getTotalUsuarios());
    }

    // ==================== TESTE DE DEGRADAÇÃO GRACEFUL ====================
    @Test
    @DisplayName("Performance deve degradar graciosamente com aumento de carga")
    void degradacaoGraceful_ComAumentoCarga() {
        // Test gradual increase in load
        int[] cargas = {50, 100, 200, 500, 1000};
        boolean[] sucessos = new boolean[cargas.length];

        for (int c = 0; c < cargas.length; c++) {
            // Limpar estado
            limprarDadosPersistentes();
            usuarioService = new UsuarioService();

            int carga = cargas[c];

            try {
                // Medir tempo para operações básicas
                Instant inicio = Instant.now();

                for (int i = 0; i < carga; i++) {
                    usuarioService.cadastrarUsuario("graceful_user" + i, "senha" + i, "avatar" + i + ".png");
                }

                // Teste de operação após carga
                boolean teste = usuarioService.cadastrarUsuario("test_after_load", "senha_test", "avatar_test.png");
                Usuario usuario = usuarioService.autenticar("test_after_load", "senha_test");

                Instant fim = Instant.now();
                Duration duracao = Duration.between(inicio, fim);

                sucessos[c] = teste && usuario != null && duracao.toSeconds() < 5; // Máximo 5 segundos

                System.out.printf("Carga: %d usuários, Sucesso: %s, Tempo: %.2fs%n",
                        carga, sucessos[c], duracao.toMillis() / 1000.0);

            } catch (Exception e) {
                sucessos[c] = false;
                System.out.printf("Carga: %d usuários, Falha: %s%n", carga, e.getMessage());
            }
        }

        // Assert - pelo menos as cargas menores devem ter sucesso
        assertTrue(sucessos[0], "Carga baixa deve sempre funcionar");
        assertTrue(sucessos[1], "Carga média deve funcionar");

        // Contar quantas cargas tiveram sucesso
        int sucessosCount = 0;
        for (boolean sucesso : sucessos) {
            if (sucesso) {
                sucessosCount++;
            }
        }

        assertTrue(sucessosCount >= 3, "Pelo menos 3 níveis de carga devem funcionar corretamente");
    }
}
