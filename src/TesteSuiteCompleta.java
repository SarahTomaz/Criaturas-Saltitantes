
import model.*;
import service.*;

/**
 * Suite de Testes para o Simulador de Criaturas Saltitantes v2
 *
 * Esta classe executa testes abrangentes para validar todos os requisitos: -
 * Formação de clusters e soma de moedas - Roubo de metade das moedas da
 * criatura mais próxima - Guardião do horizonte e eliminação de clusters -
 * Condições de simulação bem-sucedida - Funcionalidades de usuários e
 * estatísticas
 */
public class TesteSuiteCompleta {

    private static int testesExecutados = 0;
    private static int testesPassaram = 0;
    private static final double DELTA = 0.001;

    public static void main(String[] args) {
        System.out.println("=== SUITE DE TESTES - SIMULADOR DE CRIATURAS SALTITANTES V2 ===\n");

        TesteSuiteCompleta suite = new TesteSuiteCompleta();

        // Testes das classes modelo
        suite.testarCriatura();
        suite.testarCluster();
        suite.testarGuardiaoHorizonte();
        suite.testarUsuario();
        suite.testarSimulacao();

        // Testes dos serviços
        suite.testarUsuarioService();
        suite.testarSimuladorService();
        suite.testarEstatisticasService();

        // Testes de integração
        suite.testarIntegracao();

        System.out.println("\n=== RESUMO DOS TESTES ===");
        System.out.println("Testes executados: " + testesExecutados);
        System.out.println("Testes que passaram: " + testesPassaram);
        System.out.println("Taxa de sucesso: " + (100.0 * testesPassaram / testesExecutados) + "%");

        if (testesPassaram == testesExecutados) {
            System.out.println("✅ TODOS OS TESTES PASSARAM!");
        } else {
            System.out.println("❌ ALGUNS TESTES FALHARAM!");
        }
    }

    // === TESTES DA CLASSE CRIATURA ===
    public void testarCriatura() {
        System.out.println("--- Testando Classe Criatura ---");

        // Teste 1: Criação e valores iniciais
        testar("Criação de criatura", () -> {
            Criatura c = new Criatura(1, 10.0, 5);
            return c.getId() == 1 && Math.abs(c.getPosicao() - 10.0) < DELTA
                    && c.getMoedas() == 5 && c.isAtiva();
        });

        // Teste 2: Movimento (fórmula xi ← xi + rgi)
        testar("Movimento de criatura", () -> {
            Criatura c = new Criatura(1, 10.0, 5);
            double posInicial = c.getPosicao();
            for (int i = 0; i < 100; i++) {
                c.mover();
                if (Math.abs(c.getPosicao() - posInicial) > DELTA) {
                    return true; // Posição mudou
                }
            }
            return false;
        });

        // Teste 3: Criatura inativa não se move
        testar("Criatura inativa não se move", () -> {
            Criatura c = new Criatura(1, 10.0, 5);
            c.desativar();
            double posInicial = c.getPosicao();
            c.mover();
            return Math.abs(c.getPosicao() - posInicial) < DELTA;
        });

        // Teste 4: Gestão de moedas
        testar("Gestão de moedas", () -> {
            Criatura c = new Criatura(1, 10.0, 5);
            c.adicionarMoedas(3);
            int removidas = c.removerMoedas(2);
            return c.getMoedas() == 6 && removidas == 2;
        });
    }

    // === TESTES DA CLASSE CLUSTER ===
    public void testarCluster() {
        System.out.println("--- Testando Classe Cluster ---");

        // Teste 5: Formação de cluster e soma de moedas
        testar("Formação de cluster", () -> {
            Criatura c1 = new Criatura(1, 10.0, 5);
            Criatura c2 = new Criatura(2, 10.0, 3);
            Cluster cluster = new Cluster(c1, c2);
            return cluster.getTotalMoedas() == 8 && cluster.getTamanho() == 2
                    && !c1.isAtiva() && !c2.isAtiva();
        });

        // Teste 6: Roubo de metade das moedas
        testar("Roubo de moedas pelo cluster", () -> {
            Criatura c1 = new Criatura(1, 10.0, 5);
            Criatura c2 = new Criatura(2, 10.0, 3);
            Criatura vizinha = new Criatura(3, 15.0, 10);

            Cluster cluster = new Cluster(c1, c2);
            int moedasAntes = cluster.getTotalMoedas();

            cluster.roubarMoedasDeVizinho(vizinha);

            return cluster.getTotalMoedas() == moedasAntes + 5 && vizinha.getMoedas() == 5;
        });

        // Teste 7: Movimento do cluster
        testar("Movimento do cluster", () -> {
            Criatura c1 = new Criatura(1, 20.0, 8);
            Criatura c2 = new Criatura(2, 20.0, 7);
            Cluster cluster = new Cluster(c1, c2);
            double posInicial = cluster.getPosicao();

            for (int i = 0; i < 100; i++) {
                cluster.mover();
                if (Math.abs(cluster.getPosicao() - posInicial) > DELTA) {
                    return true;
                }
            }
            return false;
        });
    }

    // === TESTES DO GUARDIÃO DO HORIZONTE ===
    public void testarGuardiaoHorizonte() {
        System.out.println("--- Testando Guardião do Horizonte ---");

        // Teste 8: Criação do guardião
        testar("Criação do guardião", () -> {
            GuardiaoHorizonte guardiao = new GuardiaoHorizonte(50.0);
            return Math.abs(guardiao.getPosicao() - 50.0) < DELTA && guardiao.getMoedas() == 0;
        });

        // Teste 9: Eliminação de cluster pelo guardião
        testar("Eliminação de cluster", () -> {
            GuardiaoHorizonte guardiao = new GuardiaoHorizonte(30.0);
            Criatura c1 = new Criatura(1, 10.0, 5);
            Criatura c2 = new Criatura(2, 10.0, 3);
            Cluster cluster = new Cluster(c1, c2);

            int moedasAntes = guardiao.getMoedas();
            guardiao.eliminarCluster(cluster);

            return guardiao.getMoedas() == moedasAntes + 8;
        });

        // Teste 10: Movimento do guardião
        testar("Movimento do guardião", () -> {
            GuardiaoHorizonte guardiao = new GuardiaoHorizonte(40.0);
            guardiao.setMoedas(5); // Dar algumas moedas para permitir movimento
            double posInicial = guardiao.getPosicao();

            for (int i = 0; i < 100; i++) {
                guardiao.mover();
                if (Math.abs(guardiao.getPosicao() - posInicial) > DELTA) {
                    return true;
                }
            }
            return false;
        });
    }

    // === TESTES DA CLASSE USUARIO ===
    public void testarUsuario() {
        System.out.println("--- Testando Classe Usuario ---");

        // Teste 11: Criação de usuário
        testar("Criação de usuário", () -> {
            Usuario usuario = new Usuario("teste", "senha123", "avatar.png");
            return "teste".equals(usuario.getLogin())
                    && "avatar.png".equals(usuario.getAvatar())
                    && usuario.getPontuacao() == 0;
        });

        // Teste 12: Autenticação
        testar("Autenticação de usuário", () -> {
            Usuario usuario = new Usuario("user", "pass", "img.png");
            return usuario.autenticar("pass") && !usuario.autenticar("wrong");
        });

        // Teste 13: Incremento de simulações
        testar("Incremento de simulações", () -> {
            Usuario usuario = new Usuario("player", "pwd", "pic.jpg");
            usuario.incrementarSimulacaoBemSucedida();
            usuario.incrementarTotalSimulacoes();
            return usuario.getPontuacao() == 1 && usuario.getTotalSimulacoes() == 2;
        });
    }

    // === TESTES DA CLASSE SIMULACAO ===
    public void testarSimulacao() {
        System.out.println("--- Testando Classe Simulacao ---");

        // Teste 14: Criação de simulação
        testar("Criação de simulação", () -> {
            Usuario usuario = new Usuario("sim", "test", "av.png");
            Simulacao sim = new Simulacao(usuario, 5, 100);
            return sim.getCriaturas().size() == 5
                    && sim.getGuardiao() != null
                    && !sim.isConcluida();
        });

        // Teste 15: Execução de iteração
        testar("Execução de iteração", () -> {
            Usuario usuario = new Usuario("iter", "test", "av.png");
            Simulacao sim = new Simulacao(usuario, 3, 100);
            boolean continuou = sim.executarIteracao();
            return continuou && sim.getIteracoes() == 1;
        });
    }

    // === TESTES DOS SERVIÇOS ===
    public void testarUsuarioService() {
        System.out.println("--- Testando UsuarioService ---");

        // Teste 16: Cadastro de usuário
        testar("Cadastro de usuário", () -> {
            UsuarioService service = new UsuarioService();
            return service.cadastrarUsuario("novo", "senha", "avatar.png");
        });
    }

    public void testarSimuladorService() {
        System.out.println("--- Testando SimuladorService ---");

        // Teste 17: Criação de simulação no serviço
        testar("Criação de simulação no serviço", () -> {
            SimuladorService service = new SimuladorService();
            Usuario usuario = new Usuario("sim_user", "pass", "av.png");
            Simulacao sim = service.criarNovaSimulacao(usuario, 4, 50);
            return sim != null && sim.getCriaturas().size() == 4;
        });
    }

    public void testarEstatisticasService() {
        System.out.println("--- Testando EstatisticasService ---");

        // Teste 18: Geração de relatório
        testar("Geração de relatório", () -> {
            UsuarioService usuarioService = new UsuarioService();
            SimuladorService simuladorService = new SimuladorService();
            EstatisticasService estatisticas = new EstatisticasService(usuarioService, simuladorService);

            String relatorio = estatisticas.gerarRelatorioCompleto();
            return relatorio != null && relatorio.contains("RELATÓRIO");
        });
    }

    // === TESTE DE INTEGRAÇÃO ===
    public void testarIntegracao() {
        System.out.println("--- Testando Integração Completa ---");

        // Teste 19: Fluxo completo de simulação
        testar("Fluxo completo", () -> {
            // Criar usuário
            UsuarioService usuarioService = new UsuarioService();
            usuarioService.cadastrarUsuario("integra", "test", "avatar.png");
            Usuario usuario = usuarioService.autenticar("integra", "test");

            // Executar simulação
            SimuladorService simuladorService = new SimuladorService();
            Simulacao sim = simuladorService.executarSimulacaoCompleta(usuario, 3, 50);

            // Verificar estatísticas
            EstatisticasService estatisticas = new EstatisticasService(usuarioService, simuladorService);
            String resumo = estatisticas.getResumoEstatisticas();

            return sim != null && resumo != null && resumo.length() > 0;
        });
    }

    // === MÉTODO AUXILIAR PARA EXECUTAR TESTES ===
    private void testar(String nome, TestCase teste) {
        testesExecutados++;
        try {
            if (teste.executar()) {
                testesPassaram++;
                System.out.println("✓ " + nome + " - PASSOU");
            } else {
                System.out.println("✗ " + nome + " - FALHOU");
            }
        } catch (Exception e) {
            System.out.println("✗ " + nome + " - ERRO: " + e.getMessage());
        }
    }

    // Interface funcional para testes
    @FunctionalInterface
    private interface TestCase {

        boolean executar() throws Exception;
    }
}
