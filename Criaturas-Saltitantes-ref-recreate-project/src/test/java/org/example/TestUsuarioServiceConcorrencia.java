package service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes de concorrência e thread safety para UsuarioService Verifica
 * comportamento em cenários multi-thread
 */
public class TestUsuarioServiceConcorrencia {

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

    // ==================== TESTES DE CONCORRÊNCIA PARA CADASTRO ====================
    @Test
    @DisplayName("Deve lidar com cadastros concorrentes sem duplicação")
    void cadastroConcorrente_SemDuplicacao() throws InterruptedException, ExecutionException {
        // Arrange
        int numThreads = 10;
        int numCadastrosPorThread = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicInteger contadorSucesso = new AtomicInteger(0);
        AtomicInteger contadorFalha = new AtomicInteger(0);

        // Act
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            futures.add(executor.submit(() -> {
                for (int j = 0; j < numCadastrosPorThread; j++) {
                    String login = "user_" + threadId + "_" + j;
                    boolean sucesso = usuarioService.cadastrarUsuario(login, "senha1234", "avatar.png");
                    if (sucesso) {
                        contadorSucesso.incrementAndGet();
                    } else {
                        contadorFalha.incrementAndGet();
                    }
                }
            }));
        }

        // Aguardar conclusão de todas as threads
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        // Assert
        int totalEsperado = numThreads * numCadastrosPorThread;
        assertEquals(totalEsperado, contadorSucesso.get(), "Todos os cadastros únicos devem ter sucesso");
        assertEquals(0, contadorFalha.get(), "Não deve haver falhas com logins únicos");
        assertEquals(totalEsperado, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("Deve lidar com tentativas de cadastro do mesmo login concorrentemente")
    void cadastroMesmoLogin_Concorrente() throws InterruptedException, ExecutionException {
        // Arrange
        int numThreads = 20;
        String loginComum = "usuario_comum";
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicInteger contadorSucesso = new AtomicInteger(0);
        AtomicInteger contadorFalha = new AtomicInteger(0);

        // Act
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(() -> {
                boolean sucesso = usuarioService.cadastrarUsuario(loginComum, "senha1234", "avatar.png");
                if (sucesso) {
                    contadorSucesso.incrementAndGet();
                } else {
                    contadorFalha.incrementAndGet();
                }
            }));
        }

        // Aguardar conclusão de todas as threads
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        // Assert
        assertEquals(1, contadorSucesso.get(), "Apenas um cadastro deve ter sucesso");
        assertEquals(numThreads - 1, contadorFalha.get(), "Demais tentativas devem falhar");
        assertEquals(1, usuarioService.getTotalUsuarios());
    }

    // ==================== TESTES DE CONCORRÊNCIA PARA AUTENTICAÇÃO ====================
    @Test
    @DisplayName("Deve lidar com autenticações concorrentes")
    void autenticacaoConcorrente() throws InterruptedException, ExecutionException {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");
        usuarioService.cadastrarUsuario("user3", "senha3", "avatar3.png");

        int numThreads = 15;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);

        // Act
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            futures.add(executor.submit(() -> {
                String user = "user" + ((threadId % 3) + 1);
                String senha = "senha" + ((threadId % 3) + 1);
                Usuario usuario = usuarioService.autenticar(user, senha);
                if (usuario != null) {
                    sucessos.incrementAndGet();
                } else {
                    falhas.incrementAndGet();
                }
            }));
        }

        // Aguardar conclusão
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        // Assert
        assertEquals(numThreads, sucessos.get(), "Todas as autenticações devem ter sucesso");
        assertEquals(0, falhas.get(), "Não deve haver falhas");
    }

    // ==================== TESTES DE CONCORRÊNCIA PARA REMOÇÃO ====================
    @Test
    @DisplayName("Deve lidar com remoções concorrentes")
    void remocaoConcorrente() throws InterruptedException, ExecutionException {
        // Arrange - cadastrar vários usuários
        int numUsuarios = 20;
        for (int i = 0; i < numUsuarios; i++) {
            usuarioService.cadastrarUsuario("user" + i, "senha" + i, "avatar" + i + ".png");
        }

        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicInteger remocoesSucesso = new AtomicInteger(0);

        // Act
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            futures.add(executor.submit(() -> {
                // Cada thread tenta remover 2 usuários
                for (int j = 0; j < 2; j++) {
                    String userToRemove = "user" + (threadId * 2 + j);
                    boolean sucesso = usuarioService.removerUsuario(userToRemove);
                    if (sucesso) {
                        remocoesSucesso.incrementAndGet();
                    }
                }
            }));
        }

        // Aguardar conclusão
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        // Assert
        assertEquals(numUsuarios, remocoesSucesso.get(), "Todas as remoções devem ter sucesso");
        assertEquals(0, usuarioService.getTotalUsuarios(), "Todos os usuários devem ter sido removidos");
    }

    // ==================== TESTES DE ESTADO COMPARTILHADO ====================
    @Test
    @DisplayName("Deve manter consistência do usuário logado em threads diferentes")
    void usuarioLogado_ConsistenciaEntreThreads() throws InterruptedException, ExecutionException {
        // Arrange
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");

        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Act - diferentes threads fazem login/logout
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            futures.add(executor.submit(() -> {
                if (threadId % 2 == 0) {
                    usuarioService.autenticar("user1", "senha1");
                    Thread.yield(); // Dar chance para outras threads
                    if (usuarioService.temUsuarioLogado()) {
                        assertEquals("user1", usuarioService.getUsuarioLogado().getLogin());
                    }
                } else {
                    usuarioService.autenticar("user2", "senha2");
                    Thread.yield(); // Dar chance para outras threads
                    if (usuarioService.temUsuarioLogado()) {
                        assertEquals("user2", usuarioService.getUsuarioLogado().getLogin());
                    }
                }
            }));
        }

        // Aguardar conclusão
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        // Assert - deve ter algum usuário logado (o último)
        assertTrue(usuarioService.temUsuarioLogado());
        assertNotNull(usuarioService.getUsuarioLogado());
    }

    // ==================== TESTES DE OPERAÇÕES MISTAS CONCORRENTES ====================
    @Test
    @DisplayName("Deve lidar com operações mistas concorrentes")
    void operacoesMistasConcorrentes() throws InterruptedException, ExecutionException {
        // Arrange
        int numThreads = 15;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Cadastrar alguns usuários iniciais
        for (int i = 0; i < 5; i++) {
            usuarioService.cadastrarUsuario("initial" + i, "senha" + i, "avatar" + i + ".png");
        }

        AtomicInteger operacoesSucesso = new AtomicInteger(0);

        // Act - threads fazem operações diferentes
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            futures.add(executor.submit(() -> {
                switch (threadId % 4) {
                    case 0: // Cadastro
                        boolean cadastrou = usuarioService.cadastrarUsuario("thread" + threadId, "senha", "avatar.png");
                        if (cadastrou) {
                            operacoesSucesso.incrementAndGet();
                        }
                        break;
                    case 1: // Autenticação
                        Usuario usuario = usuarioService.autenticar("initial" + (threadId % 5), "senha" + (threadId % 5));
                        if (usuario != null) {
                            operacoesSucesso.incrementAndGet();
                        }
                        break;
                    case 2: // Alteração de avatar
                        boolean alterou = usuarioService.alterarAvatar("initial" + (threadId % 5), "new_avatar.png");
                        if (alterou) {
                            operacoesSucesso.incrementAndGet();
                        }
                        break;
                    case 3: // Atualização de pontuação
                        boolean atualizou = usuarioService.atualizarPontuacao("initial" + (threadId % 5), threadId);
                        if (atualizou) {
                            operacoesSucesso.incrementAndGet();
                        }
                        break;
                }
            }));
        }

        // Aguardar conclusão
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        // Assert - verificar que o sistema mantém consistência
        assertTrue(operacoesSucesso.get() > 0, "Pelo menos algumas operações devem ter sucesso");
        assertTrue(usuarioService.getTotalUsuarios() >= 5, "Deve manter pelo menos os usuários iniciais");

        // Verificar que os dados ainda são válidos
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        for (Usuario user : usuarios) {
            assertNotNull(user.getLogin());
            assertNotNull(user.getAvatar());
            assertTrue(user.getTotalSimulacoes() >= 0);
            assertTrue(user.getPontuacao() >= 0);
        }
    }

    // ==================== TESTE DE PERSISTÊNCIA CONCORRENTE ====================
    @Test
    @DisplayName("Deve lidar com persistência concorrente")
    void persistenciaConcorrente() throws InterruptedException, ExecutionException {
        // Arrange
        int numThreads = 8;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Act - threads fazem operações que disparam persistência
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            futures.add(executor.submit(() -> {
                // Operações que chamam salvarUsuarios()
                usuarioService.cadastrarUsuario("persist" + threadId, "senha", "avatar.png");
                usuarioService.alterarAvatar("persist" + threadId, "new_avatar_" + threadId + ".png");
                usuarioService.atualizarPontuacao("persist" + threadId, threadId);
            }));
        }

        // Aguardar conclusão
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        // Assert - verificar que dados foram persistidos corretamente
        UsuarioService novoService = new UsuarioService();
        assertEquals(numThreads, novoService.getTotalUsuarios());

        for (int i = 0; i < numThreads; i++) {
            Usuario usuario = novoService.autenticar("persist" + i, "senha");
            assertNotNull(usuario, "Usuario persist" + i + " deve existir");
            assertEquals("new_avatar_" + i + ".png", usuario.getAvatar());
        }
    }

    // ==================== TESTE DE TIMEOUT ====================
    @Test
    @DisplayName("Deve completar operações em tempo razoável mesmo com concorrência")
    void timeoutOperacoesConcorrentes() throws InterruptedException, TimeoutException {
        // Arrange
        int numThreads = 50;
        int operacoesPorThread = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Act
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            futures.add(executor.submit(() -> {
                for (int j = 0; j < operacoesPorThread; j++) {
                    usuarioService.cadastrarUsuario("speed_" + threadId + "_" + j, "senha", "avatar.png");
                }
            }));
        }

        // Assert - todas as operações devem completar em menos de 10 segundos
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(10), () -> {
            for (Future<?> future : futures) {
                future.get();
            }
        });

        executor.shutdown();
        assertEquals(numThreads * operacoesPorThread, usuarioService.getTotalUsuarios());
    }
}
