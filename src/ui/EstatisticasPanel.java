package ui;

import java.awt.*;
import javax.swing.*;
import service.EstatisticasService;

public class EstatisticasPanel extends JDialog {

    private final EstatisticasService estatisticasService;

    private JTextArea txtEstatisticas;
    private JButton btnFechar;

    public EstatisticasPanel(JFrame parent, EstatisticasService estatisticasService) {
        super(parent, "Estatísticas da Simulação", true);
        this.estatisticasService = estatisticasService;
        configurarInterface();
    }

    private void configurarInterface() {
        setSize(600, 400);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel principal
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Área de texto para estatísticas
        txtEstatisticas = new JTextArea();
        txtEstatisticas.setEditable(false);
        txtEstatisticas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        atualizarEstatisticas();

        JScrollPane scrollPane = new JScrollPane(txtEstatisticas);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));

        btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(event -> dispose());
        painelBotoes.add(btnFechar);

        add(painelPrincipal, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void atualizarEstatisticas() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== ESTATÍSTICAS GERAIS ===\n\n");
        sb.append(estatisticasService.getResumoEstatisticas()).append("\n\n");

        sb.append("=== TOP 5 USUÁRIOS ===\n");
        estatisticasService.getRankingUsuarios().stream()
                .limit(5)
                .forEach(usuario -> sb.append(String.format("- %s: %d pontos (Avatar: %s)\n",
                usuario.getLogin(), usuario.getPontuacao(), usuario.getAvatar()))
                );

        sb.append("\n=== RELATÓRIO COMPLETO ===\n");
        sb.append(estatisticasService.gerarRelatorioCompleto());

        txtEstatisticas.setText(sb.toString());
        txtEstatisticas.setCaretPosition(0); // Rolagem para o topo
    }
}
