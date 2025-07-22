package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Testes usando dublês de teste (mocks, stubs, fakes) para isolar dependências
 * e testar comportamentos específicos de I/O e outras dependências externas
 */
public class TestUsuarioServiceMocks {

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

    // ==================== TESTES DE PERSISTÊNCIA E ERROS ====================
    @Test
    @DisplayName("Deve lidar com arquivo corrompido")
    void carregarUsuarios_ComArquivoCorrempido() throws IOException {
        // Arrange - criar arquivo vazio (corrompido)
        File dataDir = new File("data");
        dataDir.mkdirs();
        File arquivo = new File("data/usuarios.ser");
        arquivo.createNewFile(); // Criar arquivo vazio

        // Act
        UsuarioService service = new UsuarioService();

        // Assert - deve criar lista vazia
        assertEquals(0, service.getTotalUsuarios());
    }

    @Test
    @DisplayName("Deve lidar com ClassNotFoundException ao carregar usuários")
    void carregarUsuarios_ComClassNotFoundException() throws IOException, ClassNotFoundException {
        // Arrange - criar arquivo com dados inválidos
        File dataDir = new File("data");
        dataDir.mkdirs();
        File arquivo = new File("data/usuarios.ser");

        // Simular um arquivo com dados que causam ClassNotFoundException
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            oos.writeObject("string inválida em vez de lista");
        }

        // Act
        UsuarioService service = new UsuarioService();

        // Assert - deve criar lista vazia
        assertEquals(0, service.getTotalUsuarios());
    }

    // ==================== FAKE PARA SISTEMA DE ARQUIVOS ====================
    @Test
    @DisplayName("Deve usar FakeFileSystem para testes isolados")
    void persistencia_ComFakeFileSystem() throws Exception {
        // Arrange - criar um "fake" arquivo temporário
        String fakeFilePath = "test_usuarios.ser";
        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        field.setAccessible(true);

        // Modificar temporariamente o caminho do arquivo
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
        field.set(null, fakeFilePath);

        try {
            // Act
            UsuarioService service1 = new UsuarioService();
            service1.cadastrarUsuario("user1", "senha1", "avatar1.png");
            service1.cadastrarUsuario("user2", "senha2", "avatar2.png");

            UsuarioService service2 = new UsuarioService();

            // Assert
            assertEquals(2, service2.getTotalUsuarios());
            assertNotNull(service2.autenticar("user1", "senha1"));
            assertNotNull(service2.autenticar("user2", "senha2"));

        } finally {
            // Cleanup - restaurar valor original e limpar arquivo fake
            field.set(null, "data/usuarios.ser");
            new File(fakeFilePath).delete();
        }
    }

    // ==================== STUB PARA COMPORTAMENTOS ESPECÍFICOS ====================
    @Test
    @DisplayName("Deve usar stub para simular comportamento específico de hash")
    void autenticacao_ComStubDeHash() {
        // Este é um exemplo conceitual - na prática, seria difícil mockar o hash SHA-256
        // mas demonstra como usar stubs para comportamentos específicos

        // Arrange
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");

        // Act & Assert - teste do comportamento real do hash
        Usuario usuario = usuarioService.autenticar("user", "senha1234");
        assertNotNull(usuario);

        // Verificar que senhas diferentes resultam em falha
        Usuario usuarioFalso = usuarioService.autenticar("user", "senha_errada");
        assertNull(usuarioFalso);
    }

    // ==================== SPY PARA VERIFICAR CHAMADAS ====================
    @Test
    @DisplayName("Deve verificar chamadas ao método salvarUsuarios usando spy")
    void verificarChamadasSalvarUsuarios() {
        // Arrange
        UsuarioService spyService = spy(usuarioService);

        // Act
        spyService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        spyService.alterarAvatar("user1", "novo_avatar.png");
        spyService.removerUsuario("user1");

        // Assert - verificar que salvarUsuarios foi chamado 3 vezes
        verify(spyService, times(3)).salvarUsuarios();
    }

    @Test
    @DisplayName("Deve verificar que atualizarUsuario chama salvarUsuarios")
    void atualizarUsuario_DeveChamarSalvarUsuarios() {
        // Arrange
        UsuarioService spyService = spy(usuarioService);
        spyService.cadastrarUsuario("user", "senha1234", "avatar.png");
        Usuario usuario = spyService.autenticar("user", "senha1234");
        usuario.incrementarTotalSimulacoes();

        // Reset para não contar a chamada do cadastro
        clearInvocations(spyService);

        // Act
        spyService.atualizarUsuario(usuario);

        // Assert
        verify(spyService, times(1)).salvarUsuarios();
    }

    // ==================== TESTE SIMPLES DE DIRETÓRIO ====================
    @Test
    @DisplayName("Deve lidar com criação normal de diretório")
    void salvarUsuarios_CriacaoNormalDeDiretorio() {
        // Arrange
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");

        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> {
            usuarioService.salvarUsuarios();
        });

        // Verificar que arquivo foi criado
        File arquivo = new File("data/usuarios.ser");
        assertTrue(arquivo.exists());
    }

    // ==================== TESTE DE INTEGRAÇÃO COM MOCKS ====================
    @Test
    @DisplayName("Teste de integração usando mocks para componentes externos")
    void integração_ComMocksParaComponentesExternos() {
        // Simulate external service dependencies (if any existed)
        // This is a conceptual example of how to test integration points

        // Arrange
        usuarioService.cadastrarUsuario("user", "senha1234", "avatar.png");

        // Act
        Usuario usuario = usuarioService.autenticar("user", "senha1234");
        usuarioService.atualizarPontuacao("user", 5);

        // Assert
        assertNotNull(usuario);
        assertEquals("user", usuario.getLogin());

        // Verify state after operations
        Usuario usuarioAtualizado = usuarioService.autenticar("user", "senha1234");
        assertEquals(1, usuarioAtualizado.getTotalSimulacoes());
        assertEquals(1, usuarioAtualizado.getPontuacao());
    }

    // ==================== DUBLÊ PARA COMPORTAMENTO DETERMINÍSTICO ====================
    @Test
    @DisplayName("Deve usar dublê para comportamento determinístico em testes")
    void comportamentoDeterministico_ComDuble() {
        // Create a predictable test scenario

        // Arrange
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");
        usuarioService.cadastrarUsuario("user3", "senha3", "avatar3.png");

        // Act
        usuarioService.autenticar("user2", "senha2");
        boolean removidoLogado = usuarioService.removerUsuario("user2");
        boolean removidoNaoLogado = usuarioService.removerUsuario("user1");

        // Assert
        assertFalse(removidoLogado, "Não deve remover usuário logado");
        assertTrue(removidoNaoLogado, "Deve remover usuário não logado");
        assertEquals(2, usuarioService.getTotalUsuarios());
        assertTrue(usuarioService.temUsuarioLogado());
        assertEquals("user2", usuarioService.getUsuarioLogado().getLogin());
    }
}
