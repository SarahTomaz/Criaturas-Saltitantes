package service;


import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TestEstatisticaService {

        // @Mock creates a mock implementation for the class it annotates.
        @Mock
        private UsuarioService usuarioService;

        @Mock
        private SimuladorService simuladorService; // Mocked even if not used, as it's a dependency

        // @InjectMocks creates an instance of the class and injects the mocks that are created with the @Mock
        // annotations into this instance.
        @InjectMocks
        private EstatisticasService estatisticasService;

        private Usuario user1;
        private Usuario user2;
        private List<Usuario> userList;

        @BeforeEach
        void setUp() {
            // Create sample user data for tests
            user1 = new Usuario("ana", "s1", "avatar1.png");
            user1.setPontuacao(10); // 10 successful simulations
            user1.setTotalSimulacoes(20); // 20 total simulations

            user2 = new Usuario("bia", "s2", "avatar2.png");
            user2.setPontuacao(15); // 15 successful simulations
            user2.setTotalSimulacoes(25); // 25 total simulations

            userList = new ArrayList<>();
            userList.add(user1);
            userList.add(user2);
        }

        @Test
        @DisplayName("Deve calcular o total de simulações globais corretamente")
        void getTotalSimulacoesGlobal_ShouldReturnCorrectSum() {
            // Arrange: Tell the mock what to return when its method is called
            when(usuarioService.listarUsuarios()).thenReturn(userList);

            // Act
            int total = estatisticasService.getTotalSimulacoesGlobal();

            // Assert
            assertEquals(45, total, "Total de simulações deve ser 20 + 25");
        }

        @Test
        @DisplayName("Deve retornar 0 para total de simulações quando não há usuários")
        void getTotalSimulacoesGlobal_ShouldReturnZeroForNoUsers() {
            // Arrange
            when(usuarioService.listarUsuarios()).thenReturn(Collections.emptyList());

            // Act
            int total = estatisticasService.getTotalSimulacoesGlobal();

            // Assert
            assertEquals(0, total);
        }

        @Test
        @DisplayName("Deve calcular o total de simulações bem-sucedidas corretamente")
        void getTotalSimulacoesBemSucedidas_ShouldReturnCorrectSum() {
            // Arrange
            when(usuarioService.listarUsuarios()).thenReturn(userList);

            // Act
            int total = estatisticasService.getTotalSimulacoesBemSucedidas();

            // Assert
            assertEquals(25, total, "Total de simulações bem-sucedidas deve ser 10 + 15");
        }

        @Test
        @DisplayName("Deve calcular a taxa de sucesso global corretamente")
        void getTaxaSucessoGlobal_ShouldCalculateCorrectly() {
            // Arrange
            when(usuarioService.listarUsuarios()).thenReturn(userList);
            // Total Sucedidas = 25, Total Global = 45

            // Act
            double taxa = estatisticasService.getTaxaSucessoGlobal();

            // Assert
            assertEquals(25.0 / 45.0, taxa, 0.001);
        }

        @Test
        @DisplayName("Deve retornar 0 para taxa de sucesso quando não há simulações")
        void getTaxaSucessoGlobal_ShouldReturnZeroForNoSimulations() {
            // Arrange
            user1.setTotalSimulacoes(0);
            user2.setTotalSimulacoes(0);
            when(usuarioService.listarUsuarios()).thenReturn(userList);

            // Act
            double taxa = estatisticasService.getTaxaSucessoGlobal();

            // Assert
            assertEquals(0.0, taxa);
        }

        @Test
        @DisplayName("Deve calcular a média de simulações por usuário")
        void getMediaSimulacoesPorUsuario_ShouldCalculateCorrectly() {
            // Arrange
            when(usuarioService.listarUsuarios()).thenReturn(userList);
            when(usuarioService.getTotalUsuarios()).thenReturn(2); // Mock this call too

            // Act
            double media = estatisticasService.getMediaSimulacoesPorUsuario();

            // Assert
            assertEquals(22.5, media, "A média deve ser 45 / 2");
        }

        @Test
        @DisplayName("Deve retornar 0 para média de simulações quando não há usuários")
        void getMediaSimulacoesPorUsuario_ShouldReturnZeroForNoUsers() {
            // Arrange
            when(usuarioService.listarUsuarios()).thenReturn(Collections.emptyList());

            // Act
            double media = estatisticasService.getMediaSimulacoesPorUsuario();

            // Assert
            assertEquals(0.0, media);
        }


        @Test
        @DisplayName("Deve retornar o ranking de usuários ordenado por pontuação")
        void getRankingUsuarios_ShouldReturnSortedList() {
            // Arrange
            when(usuarioService.listarUsuarios()).thenReturn(userList); // user2 (15 pts) > user1 (10 pts)

            // Act
            List<Usuario> ranking = estatisticasService.getRankingUsuarios();

            // Assert
            assertEquals(2, ranking.size());
            assertEquals("bia", ranking.get(0).getLogin(), "Bia deveria estar em primeiro lugar");
            assertEquals("ana", ranking.get(1).getLogin(), "Ana deveria estar em segundo lugar");
        }

    @Test
    @DisplayName("Deve gerar o resumo de estatísticas corretamente")
    void getResumoEstatisticas_ShouldGenerateCorrectString() {
        // Arrange
        when(usuarioService.listarUsuarios()).thenReturn(userList);
        when(usuarioService.getTotalUsuarios()).thenReturn(2);

        // Act
        String resumo = estatisticasService.getResumoEstatisticas();

        // Assert
        // Expected: "Usuários: 2 | Simulações: 45 | Bem-sucedidas: 25 | Taxa: 55.6%"
        String expected = String.format(
                "Usuários: %d | Simulações: %d | Bem-sucedidas: %d | Taxa: %.1f%%",
                2, 45, 25, (25.0 / 45.0) * 100
        );
        assertEquals(expected, resumo);
    }

    @Test
    @DisplayName("Deve gerar estatísticas para um usuário específico")
    void getEstatisticasUsuario_ShouldGenerateCorrectReportForUser() {
        // Arrange
        when(usuarioService.listarUsuarios()).thenReturn(userList); // Needed for ranking calculation

        // Act
        String relatorioAna = estatisticasService.getEstatisticasUsuario("ana");

        // Assert
        assertTrue(relatorioAna.contains("=== ESTATÍSTICAS DE ANA ==="));
        assertTrue(relatorioAna.contains("Simulações bem-sucedidas: 10"));
        assertTrue(relatorioAna.contains("Total de simulações: 20"));
        // Taxa de sucesso para Ana: 10/20 = 50%
        assertTrue(relatorioAna.contains("Taxa de sucesso: 50,00%"));
        // Posição no ranking: Bia (15) é 1ª, Ana (10) é 2ª
        assertTrue(relatorioAna.contains("Posição no ranking: 2 de 2"));
    }

    @Test
    @DisplayName("Deve retornar mensagem de erro para usuário não encontrado")
    void getEstatisticasUsuario_ShouldReturnNotFoundMessage() {
        // Arrange
        when(usuarioService.listarUsuarios()).thenReturn(userList);

        // Act
        String relatorio = estatisticasService.getEstatisticasUsuario("carlos");

        // Assert
        assertEquals("Usuário não encontrado.", relatorio);
    }

    @Test
    @DisplayName("Deve gerar o relatório completo com todos os dados")
    void gerarRelatorioCompleto_ShouldContainAllSections() {
        // Arrange
        when(usuarioService.listarUsuarios()).thenReturn(userList);
        when(usuarioService.getTotalUsuarios()).thenReturn(2);

        // Act
        String relatorio = estatisticasService.gerarRelatorioCompleto();

        // Assert
        // Check for key sections and data points
        assertTrue(relatorio.contains("=== RELATÓRIO DE ESTATÍSTICAS ==="));

        // User stats section
        assertTrue(relatorio.contains("ESTATÍSTICAS POR USUÁRIO:"));
        assertTrue(relatorio.contains("Usuário: ana"));
        assertTrue(relatorio.contains("Taxa de sucesso: 50,00%"));
        assertTrue(relatorio.contains("Usuário: bia"));
        assertTrue(relatorio.contains("Taxa de sucesso: 60,00%")); // 15/25

        // Global stats section
        assertTrue(relatorio.contains("ESTATÍSTICAS GLOBAIS:"));
        assertTrue(relatorio.contains("Total de usuários cadastrados: 2"));
        assertTrue(relatorio.contains("Total de simulações executadas: 45"));
        assertTrue(relatorio.contains("Taxa de sucesso global: 55,56%")); // (25/45)*100
        assertTrue(relatorio.contains("Média de simulações por usuário: 22,50"));

        // Ranking section
        assertTrue(relatorio.contains("RANKING DE USUÁRIOS"));
        // Check for correct order in the ranking part of the string
        int posBia = relatorio.indexOf("1. bia");
        int posAna = relatorio.indexOf("2. ana");
        assertTrue(posBia != -1 && posAna != -1, "Ranking entries should exist");
        assertTrue(posBia < posAna, "Bia should appear before Ana in the ranking");
    }
}
