package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.*;
import service.SimuladorService;
import service.UsuarioService;

public class SimulacaoPanel extends JDialog {

    private final SimuladorService simuladorService;
    private final UsuarioService usuarioService;
    private final Usuario usuario;

    private JButton btnIniciarSimulacao;
    private JButton btnPararSimulacao;
    private JButton btnFechar;
    private JTextArea txtLogSimulacao;
    private JPanel painelVisualizacao;
    private JSpinner spinnerCriaturas;
    private JComboBox<String> comboVelocidade;
    private JLabel lblIteracao;
    private JLabel lblStatus;
    private JLabel lblEstatisticas;

    private Timer timerSimulacao;
    private boolean simulacaoRodando = false;

    // Dimensões da visualização
    private static final int LARGURA_HORIZONTE = 500;
    private static final int ALTURA_VISUALIZACAO = 300;
    private static final int MARGEM = 50;

    public SimulacaoPanel(JFrame parent, Usuario usuario, SimuladorService simuladorService, UsuarioService usuarioService) {
        super(parent, "Simulação de Criaturas Saltitantes", true);
        this.usuario = usuario;
        this.simuladorService = simuladorService;
        this.usuarioService = usuarioService;
        configurarInterface();
    }

    private void configurarInterface() {
        setSize(900, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel superior - configurações
        JPanel painelConfiguracoes = criarPainelConfiguracoes();
        add(painelConfiguracoes, BorderLayout.NORTH);

        // Painel principal - visualização
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de visualização da simulação
        painelVisualizacao = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharSimulacao(g);
            }
        };
        painelVisualizacao.setPreferredSize(new Dimension(700, ALTURA_VISUALIZACAO + 100));
        painelVisualizacao.setBackground(new Color(240, 248, 255)); // Alice Blue
        painelVisualizacao.setBorder(BorderFactory.createTitledBorder("Horizonte da Simulação"));

        painelPrincipal.add(painelVisualizacao, BorderLayout.CENTER);

        // Painel de informações
        JPanel painelInfo = criarPainelInformacoes();
        painelPrincipal.add(painelInfo, BorderLayout.NORTH);

        // Área de log
        txtLogSimulacao = new JTextArea(8, 50);
        txtLogSimulacao.setEditable(false);
        txtLogSimulacao.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLogSimulacao.setBackground(new Color(248, 248, 248));
        JScrollPane scrollPane = new JScrollPane(txtLogSimulacao);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Log da Simulação"));
        painelPrincipal.add(scrollPane, BorderLayout.SOUTH);

        add(painelPrincipal, BorderLayout.CENTER);

        // Painel de controles
        JPanel painelControles = criarPainelControles();
        add(painelControles, BorderLayout.SOUTH);

        // Configurar timer inicial (será reconfigurado ao iniciar simulação)
        timerSimulacao = new Timer(300, e -> executarProximaIteracao());
    }

    private JPanel criarPainelConfiguracoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.setBorder(BorderFactory.createTitledBorder("Configurações da Simulação"));

        painel.add(new JLabel("Número de Criaturas:"));
        spinnerCriaturas = new JSpinner(new SpinnerNumberModel(5, 2, 20, 1));
        spinnerCriaturas.setPreferredSize(new Dimension(80, 25));
        painel.add(spinnerCriaturas);

        painel.add(Box.createHorizontalStrut(20));

        painel.add(new JLabel("Velocidade:"));
        String[] velocidades = {"Lenta (1s)", "Normal (500ms)", "Rápida (300ms)", "Muito Rápida (100ms)"};
        comboVelocidade = new JComboBox<>(velocidades);
        comboVelocidade.setSelectedIndex(2); // Rápida por padrão
        comboVelocidade.setPreferredSize(new Dimension(140, 25));
        painel.add(comboVelocidade);

        painel.add(Box.createHorizontalStrut(20));
        painel.add(new JLabel("Usuário: " + usuario.getLogin()));

        return painel;
    }

    private int obterDelayVelocidade() {
        int selectedIndex = comboVelocidade.getSelectedIndex();
        switch (selectedIndex) {
            case 0:
                return 1000;  // Lenta (1s)
            case 1:
                return 500;   // Normal (500ms)
            case 2:
                return 300;   // Rápida (300ms)
            case 3:
                return 100;   // Muito Rápida (100ms)
            default:
                return 300;  // Padrão
        }
    }

    private JPanel criarPainelInformacoes() {
        JPanel painel = new JPanel(new BorderLayout());

        // Painel superior com informações básicas
        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblIteracao = new JLabel("Iteração: 0");
        lblIteracao.setFont(new Font("Arial", Font.BOLD, 14));
        painelSuperior.add(lblIteracao);

        painelSuperior.add(Box.createHorizontalStrut(30));

        lblStatus = new JLabel("Pronto para iniciar simulação");
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 12));
        painelSuperior.add(lblStatus);

        painel.add(painelSuperior, BorderLayout.NORTH);

        // Painel de estatísticas em tempo real
        lblEstatisticas = new JLabel("<html><b>Estado Atual:</b> Nenhuma simulação em andamento</html>");
        lblEstatisticas.setFont(new Font("Arial", Font.PLAIN, 11));
        lblEstatisticas.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        painel.add(lblEstatisticas, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelControles() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnIniciarSimulacao = new JButton("Iniciar Simulação");
        btnIniciarSimulacao.setBackground(new Color(46, 125, 50));
        btnIniciarSimulacao.setForeground(Color.WHITE);
        btnIniciarSimulacao.setFocusPainted(false);
        btnIniciarSimulacao.addActionListener(e -> iniciarSimulacao());

        btnPararSimulacao = new JButton("Parar Simulação");
        btnPararSimulacao.setBackground(new Color(211, 47, 47));
        btnPararSimulacao.setForeground(Color.WHITE);
        btnPararSimulacao.setFocusPainted(false);
        btnPararSimulacao.setEnabled(false);
        btnPararSimulacao.addActionListener(e -> pararSimulacao());

        btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());

        painel.add(btnIniciarSimulacao);
        painel.add(btnPararSimulacao);
        painel.add(btnFechar);

        return painel;
    }

    private void desenharSimulacao(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int yBase = ALTURA_VISUALIZACAO / 2;

        // Desenhar linha do horizonte
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(MARGEM, yBase, MARGEM + LARGURA_HORIZONTE, yBase);

        // Desenhar marcações no horizonte
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 10; i++) {
            int x = MARGEM + (i * LARGURA_HORIZONTE / 10);
            g2d.drawLine(x, yBase - 5, x, yBase + 5);
            g2d.drawString(String.valueOf(i * 10), x - 8, yBase + 20);
        }

        g2d.drawString("Posição no Horizonte", MARGEM + LARGURA_HORIZONTE / 2 - 50, yBase + 35);

        if (simuladorService.getSimulacaoAtual() != null) {
            Simulacao sim = simuladorService.getSimulacaoAtual();
            desenharElementosSimulacao(g2d, sim, yBase);
        } else {
            // Desenhar mensagem quando não há simulação
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 16));
            String msg = "Configure o número de criaturas e clique em 'Iniciar Simulação'";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            g2d.drawString(msg, x, yBase - 50);
        }
    }

    private void desenharElementosSimulacao(Graphics2D g2d, Simulacao sim, int yBase) {
        // Desenhar criaturas ativas
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        List<Criatura> criaturasAtivas = sim.getCriaturas().stream()
                .filter(Criatura::isAtiva)
                .toList();

        for (Criatura criatura : criaturasAtivas) {
            desenharCriatura(g2d, criatura, yBase);
        }

        // Desenhar clusters
        for (Cluster cluster : sim.getClusters()) {
            desenharCluster(g2d, cluster, yBase);
        }

        // Desenhar guardião
        desenharGuardiao(g2d, sim.getGuardiao(), yBase);

        // Desenhar legenda
        desenharLegenda(g2d);
    }

    private void desenharCriatura(Graphics2D g2d, Criatura criatura, int yBase) {
        int x = MARGEM + (int) (criatura.getPosicao() * LARGURA_HORIZONTE / 100.0);
        x = Math.max(MARGEM, Math.min(MARGEM + LARGURA_HORIZONTE, x));

        // Desenhar sombra da criatura
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(x - 6, yBase - 23, 16, 16);

        // Desenhar criatura como círculo azul
        g2d.setColor(new Color(33, 150, 243));
        g2d.fillOval(x - 8, yBase - 25, 16, 16);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - 8, yBase - 25, 16, 16);

        // Desenhar ID da criatura
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 9));
        String id = String.valueOf(criatura.getId());
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(id, x - fm.stringWidth(id) / 2, yBase - 18);

        // Desenhar moedas com melhor formatação
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String moedas = criatura.getMoedas() + "🪙";
        FontMetrics fmMoedas = g2d.getFontMetrics();
        // Fundo branco para as moedas
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(x - fmMoedas.stringWidth(moedas) / 2 - 2, yBase - 40,
                fmMoedas.stringWidth(moedas) + 4, fmMoedas.getHeight(), 5, 5);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x - fmMoedas.stringWidth(moedas) / 2 - 2, yBase - 40,
                fmMoedas.stringWidth(moedas) + 4, fmMoedas.getHeight(), 5, 5);
        g2d.drawString(moedas, x - fmMoedas.stringWidth(moedas) / 2, yBase - 30);

        g2d.setStroke(new BasicStroke(1));
    }

    private void desenharCluster(Graphics2D g2d, Cluster cluster, int yBase) {
        int x = MARGEM + (int) (cluster.getPosicao() * LARGURA_HORIZONTE / 100.0);
        x = Math.max(MARGEM, Math.min(MARGEM + LARGURA_HORIZONTE, x));

        // Desenhar sombra do cluster
        g2d.setColor(new Color(0, 0, 0, 50));
        int[] xShadow = {x, x + 10, x + 15, x + 10, x, x - 5};
        int[] yShadow = {yBase - 28, yBase - 28, yBase - 18, yBase - 8, yBase - 8, yBase - 18};
        g2d.fillPolygon(xShadow, yShadow, 6);

        // Desenhar cluster como hexágono verde
        int[] xPoints = {x, x + 10, x + 15, x + 10, x, x - 5};
        int[] yPoints = {yBase - 30, yBase - 30, yBase - 20, yBase - 10, yBase - 10, yBase - 20};

        g2d.setColor(new Color(76, 175, 80));
        g2d.fillPolygon(xPoints, yPoints, 6);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, 6);

        // Desenhar quantidade de criaturas no cluster
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String size = String.valueOf(cluster.getTamanho());
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(size, x - fm.stringWidth(size) / 2, yBase - 18);

        // Desenhar moedas do cluster com melhor formatação
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        String moedas = cluster.getTotalMoedas() + "🪙";
        FontMetrics fmMoedas = g2d.getFontMetrics();
        // Fundo branco para as moedas
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(x - fmMoedas.stringWidth(moedas) / 2 - 2, yBase - 50,
                fmMoedas.stringWidth(moedas) + 4, fmMoedas.getHeight(), 5, 5);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x - fmMoedas.stringWidth(moedas) / 2 - 2, yBase - 50,
                fmMoedas.stringWidth(moedas) + 4, fmMoedas.getHeight(), 5, 5);
        g2d.drawString(moedas, x - fmMoedas.stringWidth(moedas) / 2, yBase - 40);

        g2d.setStroke(new BasicStroke(1));
    }

    private void desenharGuardiao(Graphics2D g2d, GuardiaoHorizonte guardiao, int yBase) {
        int x = MARGEM + (int) (guardiao.getPosicao() * LARGURA_HORIZONTE / 100.0);
        x = Math.max(MARGEM, Math.min(MARGEM + LARGURA_HORIZONTE, x));

        // Desenhar sombra do guardião
        g2d.setColor(new Color(0, 0, 0, 50));
        int[] xShadow = {x, x + 12, x, x - 12};
        int[] yShadow = {yBase - 33, yBase - 18, yBase - 3, yBase - 18};
        g2d.fillPolygon(xShadow, yShadow, 4);

        // Desenhar guardião como diamante vermelho
        int[] xPoints = {x, x + 12, x, x - 12};
        int[] yPoints = {yBase - 35, yBase - 20, yBase - 5, yBase - 20};

        g2d.setColor(new Color(244, 67, 54));
        g2d.fillPolygon(xPoints, yPoints, 4);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, 4);

        // Desenhar "G" no guardião
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString("G", x - fm.stringWidth("G") / 2, yBase - 17);

        // Desenhar moedas do guardião com melhor formatação
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String moedas = guardiao.getMoedas() + "🪙";
        FontMetrics fmMoedas = g2d.getFontMetrics();
        // Fundo dourado para as moedas do guardião
        g2d.setColor(new Color(255, 215, 0, 200));
        g2d.fillRoundRect(x - fmMoedas.stringWidth(moedas) / 2 - 3, yBase - 55,
                fmMoedas.stringWidth(moedas) + 6, fmMoedas.getHeight() + 2, 8, 8);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x - fmMoedas.stringWidth(moedas) / 2 - 3, yBase - 55,
                fmMoedas.stringWidth(moedas) + 6, fmMoedas.getHeight() + 2, 8, 8);
        g2d.drawString(moedas, x - fmMoedas.stringWidth(moedas) / 2, yBase - 43);

        g2d.setStroke(new BasicStroke(1));
    }

    private void desenharLegenda(Graphics2D g2d) {
        int xLegenda = MARGEM + LARGURA_HORIZONTE + 20;
        int yLegenda = 50;

        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Legenda:", xLegenda, yLegenda);

        // Criatura
        g2d.setColor(new Color(33, 150, 243));
        g2d.fillOval(xLegenda, yLegenda + 10, 16, 16);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(xLegenda, yLegenda + 10, 16, 16);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.drawString("Criatura", xLegenda + 25, yLegenda + 22);

        // Cluster
        g2d.setColor(new Color(76, 175, 80));
        int[] xPoints = {xLegenda + 8, xLegenda + 18, xLegenda + 23, xLegenda + 18, xLegenda + 8, xLegenda + 3};
        int[] yPoints = {yLegenda + 35, yLegenda + 35, yLegenda + 45, yLegenda + 55, yLegenda + 55, yLegenda + 45};
        g2d.fillPolygon(xPoints, yPoints, 6);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(xPoints, yPoints, 6);
        g2d.drawString("Cluster", xLegenda + 25, yLegenda + 47);

        // Guardião
        g2d.setColor(new Color(244, 67, 54));
        int[] xPointsG = {xLegenda + 8, xLegenda + 20, xLegenda + 8, xLegenda - 4};
        int[] yPointsG = {yLegenda + 65, yLegenda + 80, yLegenda + 95, yLegenda + 80};
        g2d.fillPolygon(xPointsG, yPointsG, 4);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(xPointsG, yPointsG, 4);
        g2d.drawString("Guardião", xLegenda + 25, yLegenda + 82);
    }

    private void iniciarSimulacao() {
        int numCriaturas = (Integer) spinnerCriaturas.getValue();

        try {
            // Criar nova simulação
            simuladorService.criarNovaSimulacao(usuario, numCriaturas, 1000);

            // Limpar log e atualizar interface
            txtLogSimulacao.setText("");
            txtLogSimulacao.append("Iniciando simulação com " + numCriaturas + " criaturas...\n");
            txtLogSimulacao.append("Usuário: " + usuario.getLogin() + "\n\n");

            // Mostrar estado inicial
            Simulacao sim = simuladorService.getSimulacaoAtual();
            int totalMoedasIniciais = sim.getCriaturas().stream()
                    .mapToInt(Criatura::getMoedas)
                    .sum();

            txtLogSimulacao.append(String.format("Estado inicial: %d criaturas, %d moedas totais\n",
                    numCriaturas, totalMoedasIniciais));
            txtLogSimulacao.append(String.format("Guardião inicia na posição %.1f com 0 moedas\n\n",
                    sim.getGuardiao().getPosicao()));

            // Atualizar estatísticas iniciais
            lblEstatisticas.setText(String.format(
                    "<html><b>Estado Inicial:</b> %d Criaturas | 0 Clusters | "
                    + "Moedas - Criaturas: %d | Guardião: 0 | Total: %d</html>",
                    numCriaturas, totalMoedasIniciais, totalMoedasIniciais
            ));

            // Configurar botões
            btnIniciarSimulacao.setEnabled(false);
            btnPararSimulacao.setEnabled(true);
            spinnerCriaturas.setEnabled(false);
            comboVelocidade.setEnabled(false);

            simulacaoRodando = true;

            // Configurar timer com a velocidade selecionada
            timerSimulacao.stop(); // Parar timer atual
            int delay = obterDelayVelocidade();
            timerSimulacao = new Timer(delay, e -> executarProximaIteracao());
            timerSimulacao.start();

            // Primeira atualização visual
            painelVisualizacao.repaint();
            lblStatus.setText("Simulação em execução...");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao iniciar simulação: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executarProximaIteracao() {
        if (!simulacaoRodando || simuladorService.getSimulacaoAtual() == null) {
            return;
        }

        try {
            boolean continuar = simuladorService.executarProximaIteracao();
            Simulacao sim = simuladorService.getSimulacaoAtual();

            // Atualizar interface
            lblIteracao.setText("Iteração: " + sim.getIteracoes());
            painelVisualizacao.repaint();

            // Contar elementos atuais
            long criaturasAtivas = sim.getCriaturas().stream().filter(Criatura::isAtiva).count();
            int totalClusters = sim.getClusters().size();
            int moedasGuardiao = sim.getGuardiao().getMoedas();

            // Calcular total de moedas no sistema
            int totalMoedasCriaturas = sim.getCriaturas().stream()
                    .filter(Criatura::isAtiva)
                    .mapToInt(Criatura::getMoedas)
                    .sum();
            int totalMoedasClusters = sim.getClusters().stream()
                    .mapToInt(Cluster::getTotalMoedas)
                    .sum();
            int totalMoedas = totalMoedasCriaturas + totalMoedasClusters + moedasGuardiao;

            // Atualizar estatísticas em tempo real
            String estatisticas = String.format(
                    "<html><b>Estado Atual:</b> Criaturas Ativas: %d | Clusters: %d | "
                    + "Moedas - Criaturas: %d | Clusters: %d | Guardião: %d | Total: %d</html>",
                    criaturasAtivas, totalClusters, totalMoedasCriaturas,
                    totalMoedasClusters, moedasGuardiao, totalMoedas
            );
            lblEstatisticas.setText(estatisticas);

            // Log da iteração com mais detalhes
            txtLogSimulacao.append(String.format("Iteração %d: Criaturas=%d, Clusters=%d, "
                    + "Moedas(C=%d, Cl=%d, G=%d, Total=%d)\n",
                    sim.getIteracoes(), criaturasAtivas, totalClusters,
                    totalMoedasCriaturas, totalMoedasClusters, moedasGuardiao, totalMoedas));

            if (!continuar) {
                finalizarSimulacao();
            }

        } catch (Exception e) {
            txtLogSimulacao.append("Erro na iteração: " + e.getMessage() + "\n");
            finalizarSimulacao();
        }
    }

    private void finalizarSimulacao() {
        timerSimulacao.stop();
        simulacaoRodando = false;

        Simulacao sim = simuladorService.getSimulacaoAtual();

        // Atualizar interface
        btnIniciarSimulacao.setEnabled(true);
        btnPararSimulacao.setEnabled(false);
        spinnerCriaturas.setEnabled(true);
        comboVelocidade.setEnabled(true);

        // Mostrar resultado
        if (sim.isBemSucedida()) {
            lblStatus.setText("✅ Simulação bem-sucedida!");
            txtLogSimulacao.append("\n🎉 SIMULAÇÃO BEM-SUCEDIDA! 🎉\n");

            // Atualizar pontuação do usuário
            usuarioService.atualizarPontuacao(usuario.getLogin(), 1);
        } else {
            lblStatus.setText("❌ Simulação não foi bem-sucedida");
            txtLogSimulacao.append("\n❌ Simulação não foi bem-sucedida.\n");

            // Incrementar apenas total de simulações
            usuarioService.atualizarPontuacao(usuario.getLogin(), 0);
        }

        txtLogSimulacao.append("Status final: " + sim.getStatus() + "\n");
        txtLogSimulacao.append("Moedas finais do guardião: " + sim.getGuardiao().getMoedas() + "\n");

        // Scroll para o final do log
        txtLogSimulacao.setCaretPosition(txtLogSimulacao.getDocument().getLength());

        // Repaint final
        painelVisualizacao.repaint();
    }

    private void pararSimulacao() {
        simulacaoRodando = false;
        timerSimulacao.stop();

        btnIniciarSimulacao.setEnabled(true);
        btnPararSimulacao.setEnabled(false);
        spinnerCriaturas.setEnabled(true);
        comboVelocidade.setEnabled(true);

        lblStatus.setText("Simulação interrompida pelo usuário");
        txtLogSimulacao.append("\n⏹️ Simulação interrompida pelo usuário.\n");

        // Atualizar apenas total de simulações (não bem-sucedida)
        if (simuladorService.getSimulacaoAtual() != null) {
            usuarioService.atualizarPontuacao(usuario.getLogin(), 0);
        }
    }
}
