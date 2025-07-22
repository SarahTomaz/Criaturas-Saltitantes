package service;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Testes específicos para tratamento de exceções, I/O e casos de erro Estes
 * testes focam em cenários de falha e recuperação
 */
public class TestUsuarioServiceExceptions {

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService();
    }

    // Helper method to modify a static final field
    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
        field.set(null, newValue);
    }

    @Test
    @DisplayName("Deve lidar com diretório sem permissão de escrita")
    void salvarUsuarios_DiretorioSemPermissao(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path arquivoSemPermissao = tempDir.resolve("readonly").resolve("usuarios.ser");
        Files.createDirectories(arquivoSemPermissao.getParent());

        // Tornar o diretório somente leitura no Windows é complicado,
        // então vamos simular com um arquivo que não pode ser criado
        Path arquivoInvalido = tempDir.resolve("con").resolve("usuarios.ser"); // 'con' é nome reservado no Windows

        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        setFinalStatic(field, arquivoInvalido.toString());

        UsuarioService service = new UsuarioService();

        // Act & Assert - não deve lançar exceção, apenas logar erro
        assertDoesNotThrow(() -> {
            service.cadastrarUsuario("test", "senha123", "avatar.png");
        });

        // Cleanup
        setFinalStatic(field, "data/usuarios.ser");
    }

    @Test
    @DisplayName("Deve lidar com arquivo corrompido durante carregamento")
    void carregarUsuarios_ArquivoComDadosInvalidos(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path arquivoCorrempido = tempDir.resolve("usuarios_corrompidos.ser");

        // Criar um arquivo com dados que não são um List<Usuario>
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoCorrempido.toFile()))) {
            oos.writeObject("String inválida em vez de List<Usuario>");
        }

        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        setFinalStatic(field, arquivoCorrempido.toString());

        // Act & Assert
        assertDoesNotThrow(() -> {
            UsuarioService service = new UsuarioService();
            assertEquals(0, service.getTotalUsuarios()); // Deve inicializar lista vazia
        });

        // Cleanup
        setFinalStatic(field, "data/usuarios.ser");
    }

    @Test
    @DisplayName("Deve lidar com arquivo que não pode ser lido")
    void carregarUsuarios_ArquivoIlegivel(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path arquivoIlegivel = tempDir.resolve("usuarios_ilegivel.ser");
        Files.createFile(arquivoIlegivel);

        // Escrever dados binários inválidos
        try (FileOutputStream fos = new FileOutputStream(arquivoIlegivel.toFile())) {
            fos.write(new byte[]{1, 2, 3, 4, 5}); // Dados inválidos para ObjectInputStream
        }

        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        setFinalStatic(field, arquivoIlegivel.toString());

        // Act & Assert
        assertDoesNotThrow(() -> {
            UsuarioService service = new UsuarioService();
            assertEquals(0, service.getTotalUsuarios());
        });

        // Cleanup
        setFinalStatic(field, "data/usuarios.ser");
    }

    @Test
    @DisplayName("Deve criar estrutura de diretórios complexa")
    void criarDiretoriosAninhados(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path arquivoAninhado = tempDir.resolve("nivel1").resolve("nivel2").resolve("nivel3").resolve("usuarios.ser");

        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        setFinalStatic(field, arquivoAninhado.toString());

        // Act
        UsuarioService service = new UsuarioService();
        service.cadastrarUsuario("test", "senha123", "avatar.png");

        // Assert
        assertTrue(Files.exists(arquivoAninhado));
        assertTrue(Files.exists(arquivoAninhado.getParent()));

        // Verificar se os dados foram salvos corretamente
        UsuarioService newService = new UsuarioService();
        assertEquals(1, newService.getTotalUsuarios());

        // Cleanup
        setFinalStatic(field, "data/usuarios.ser");
    }

    @Test
    @DisplayName("Deve manter dados consistentes após falha de salvamento")
    void consistenciaDados_AposFalhaSalvamento(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path arquivoValido = tempDir.resolve("usuarios_valido.ser");
        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        setFinalStatic(field, arquivoValido.toString());

        UsuarioService service = new UsuarioService();
        service.cadastrarUsuario("user1", "senha1", "avatar1.png");
        service.cadastrarUsuario("user2", "senha2", "avatar2.png");

        // Simular falha mudando para caminho inválido
        Path arquivoInvalido = tempDir.resolve("invalido\0").resolve("usuarios.ser");
        setFinalStatic(field, arquivoInvalido.toString());

        // Act - tentar adicionar mais usuários (salvamento falhará)
        assertDoesNotThrow(() -> {
            service.cadastrarUsuario("user3", "senha3", "avatar3.png");
        });

        // Assert - dados em memória ainda devem estar consistentes
        assertEquals(3, service.getTotalUsuarios()); // Em memória
        assertNotNull(service.autenticar("user1", "senha1"));
        assertNotNull(service.autenticar("user2", "senha2"));
        assertNotNull(service.autenticar("user3", "senha3"));

        // Cleanup
        setFinalStatic(field, "data/usuarios.ser");
    }

    @Test
    @DisplayName("Deve recuperar de arquivo parcialmente corrompido")
    void recuperacao_ArquivoParcialmenteCorrempido(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path arquivo = tempDir.resolve("usuarios_parcial.ser");

        // Primeiro, criar arquivo válido
        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        setFinalStatic(field, arquivo.toString());

        UsuarioService service1 = new UsuarioService();
        service1.cadastrarUsuario("user1", "senha1", "avatar1.png");

        // Corromper o arquivo adicionando dados inválidos no final
        try (FileOutputStream fos = new FileOutputStream(arquivo.toFile(), true)) {
            fos.write("dados corrompidos".getBytes());
        }

        // Act & Assert
        assertDoesNotThrow(() -> {
            UsuarioService service2 = new UsuarioService();
            // Deve falhar ao carregar e inicializar lista vazia
            assertEquals(0, service2.getTotalUsuarios());
        });

        // Cleanup
        setFinalStatic(field, "data/usuarios.ser");
    }

    @Test
    @DisplayName("Deve lidar com múltiplas tentativas de operação após falha")
    void resilencia_MultiplavTentativasAposFalha(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path arquivoProblematico = tempDir.resolve("con"); // Nome reservado no Windows
        Field field = UsuarioService.class.getDeclaredField("ARQUIVO_USUARIOS");
        setFinalStatic(field, arquivoProblematico.toString());

        UsuarioService service = new UsuarioService();

        // Act & Assert - múltiplas operações devem funcionar em memória
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                service.cadastrarUsuario("user" + i, "senha" + i, "avatar.png");
            }
        });

        assertEquals(10, service.getTotalUsuarios());

        // Verificar que as operações de leitura funcionam
        assertNotNull(service.autenticar("user5", "senha5"));

        // Cleanup
        setFinalStatic(field, "data/usuarios.ser");
    }

    @Test
    @DisplayName("Deve validar entrada robustamente")
    void validacao_EntradaRobusta() {
        // Testes com strings contendo caracteres especiais
        assertDoesNotThrow(() -> {
            usuarioService.cadastrarUsuario("user\ncom\tcaracteres", "senha123", "avatar.png");
        });

        // Testes com strings muito longas
        String loginLongo = "a".repeat(10000);
        String senhaLonga = "b".repeat(10000);
        String avatarLongo = "c".repeat(10000) + ".png";

        assertDoesNotThrow(() -> {
            usuarioService.cadastrarUsuario(loginLongo, senhaLonga, avatarLongo);
        });

        // Verificar que os dados foram preservados corretamente
        Usuario usuario = usuarioService.autenticar(loginLongo, senhaLonga);
        assertNotNull(usuario);
        assertEquals(loginLongo, usuario.getLogin());
        assertEquals(avatarLongo, usuario.getAvatar());
    }

    @Test
    @DisplayName("Deve manter performance com operações repetidas")
    void performance_OperacoesRepetidas() {
        // Simular uso intensivo
        long inicio = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            usuarioService.cadastrarUsuario("user" + i, "senha" + i, "avatar.png");
            usuarioService.autenticar("user" + i, "senha" + i);
            usuarioService.logout();
            usuarioService.alterarAvatar("user" + i, "novo_avatar.png");
        }

        long fim = System.currentTimeMillis();
        long duracao = fim - inicio;

        // Assert - operações devem completar em tempo razoável (menos de 5 segundos)
        assertTrue(duracao < 5000, "Operações não devem demorar mais que 5 segundos");
        assertEquals(100, usuarioService.getTotalUsuarios());
    }

    @Test
    @DisplayName("Deve lidar com operações concorrentes simuladas")
    void concorrencia_OperacoesSimultaneas() {
        // Simular operações que poderiam acontecer simultaneamente
        usuarioService.cadastrarUsuario("user1", "senha1", "avatar1.png");
        usuarioService.cadastrarUsuario("user2", "senha2", "avatar2.png");

        // Simular duas sessões diferentes
        UsuarioService service1 = new UsuarioService();
        UsuarioService service2 = new UsuarioService();

        // Operações simultâneas simuladas
        service1.autenticar("user1", "senha1");
        service2.autenticar("user2", "senha2");

        // Verificar isolamento
        assertEquals("user1", service1.getUsuarioLogado().getLogin());
        assertEquals("user2", service2.getUsuarioLogado().getLogin());

        // Operações de modificação
        service1.alterarAvatar("user1", "novo1.png");
        service2.alterarAvatar("user2", "novo2.png");

        // Verificar consistência
        UsuarioService service3 = new UsuarioService();
        Usuario user1 = service3.autenticar("user1", "senha1");
        Usuario user2 = service3.autenticar("user2", "senha2");

        assertEquals("novo1.png", user1.getAvatar());
        assertEquals("novo2.png", user2.getAvatar());
    }
}
