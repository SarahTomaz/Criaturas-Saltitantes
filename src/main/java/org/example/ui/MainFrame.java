package org.example.ui;

import java.awt.*;
import javax.swing.*;
import org.example.model.Usuario;
import org.example.service.*;

public class MainFrame extends JFrame {

    private UsuarioService usuarioService;
    private SimuladorService simuladorService;
    private EstatisticasService estatisticasService;
    private Usuario usuarioLogado;

    private JLabel lblUsuarioLogado;
    private JButton btnNovaSimulacao;
    private JButton btnEstatisticas;
    private JButton btnGerenciarUsuarios;
    private JButton btnLogout;

    public MainFrame() {
        inicializarServicos();
        configurarInterface();
        mostrarLoginInicial();
    }

    private void inicializarServicos() {
        usuarioService = new UsuarioService();
        simuladorService = new SimuladorService();
        estatisticasService = new EstatisticasService(usuarioService, simuladorService);
    }

    private void configurarInterface() {
        setTitle("Simulação de Criaturas Saltitantes v2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Painel superior - informações do usuário
        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelSuperior.setBorder(BorderFactory.createEtchedBorder());
        lblUsuarioLogado = new JLabel("Usuário: Não logado");
        btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        painelSuperior.add(lblUsuarioLogado);
        painelSuperior.add(Box.createHorizontalGlue());
        painelSuperior.add(btnLogout);
        add(painelSuperior, BorderLayout.NORTH);

        // Painel central - menu principal
        JPanel painelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titulo = new JLabel("Simulação de Criaturas Saltitantes v2", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painelCentral.add(titulo, gbc);

        // Botões do menu
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        btnNovaSimulacao = criarBotaoMenu("Nova Simulação", "Executar uma nova simulação");
        btnNovaSimulacao.addActionListener(e -> abrirSimulacao());
        gbc.gridx = 0;
        painelCentral.add(btnNovaSimulacao, gbc);

        btnEstatisticas = criarBotaoMenu("Estatísticas", "Ver estatísticas dos usuários");
        btnEstatisticas.addActionListener(e -> abrirEstatisticas());
        gbc.gridx = 1;
        painelCentral.add(btnEstatisticas, gbc);

        gbc.gridy = 2;
        btnGerenciarUsuarios = criarBotaoMenu("Gerenciar Usuários", "Cadastrar/remover usuários");
        btnGerenciarUsuarios.addActionListener(e -> abrirGerenciarUsuarios());
        gbc.gridx = 0;
        painelCentral.add(btnGerenciarUsuarios, gbc);

        JButton btnSobre = criarBotaoMenu("Sobre", "Informações sobre o sistema");
        btnSobre.addActionListener(e -> mostrarSobre());
        gbc.gridx = 1;
        painelCentral.add(btnSobre, gbc);

        add(painelCentral, BorderLayout.CENTER);

        // Painel inferior - status
        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelInferior.setBorder(BorderFactory.createEtchedBorder());
        JLabel lblStatus = new JLabel(estatisticasService.getResumoEstatisticas());
        painelInferior.add(lblStatus);
        add(painelInferior, BorderLayout.SOUTH);

        // Inicialmente desabilitar botões
        habilitarBotoes(false);
    }

    private JButton criarBotaoMenu(String texto, String tooltip) {
        JButton botao = new JButton(texto);
        botao.setPreferredSize(new Dimension(200, 50));
        botao.setToolTipText(tooltip);
        botao.setFont(new Font("Arial", Font.PLAIN, 14));
        return botao;
    }

    private void mostrarLoginInicial() {
        LoginDialog loginDialog = new LoginDialog(this, usuarioService);
        loginDialog.setVisible(true);

        if (loginDialog.getUsuarioLogado() != null) {
            usuarioLogado = loginDialog.getUsuarioLogado();
            atualizarInterfaceUsuarioLogado();
        } else {
            System.exit(0); // Fechar aplicação se não fizer login
        }
    }

    private void atualizarInterfaceUsuarioLogado() {
        lblUsuarioLogado.setText("Usuário: " + usuarioLogado.getLogin() + " (" + usuarioLogado.getAvatar() + ")");
        habilitarBotoes(true);
    }

    private void habilitarBotoes(boolean habilitar) {
        btnNovaSimulacao.setEnabled(habilitar);
        btnEstatisticas.setEnabled(habilitar);
        btnGerenciarUsuarios.setEnabled(habilitar);
        btnLogout.setEnabled(habilitar);
    }

    private void abrirSimulacao() {
        SimulacaoPanel simulacaoPanel = new SimulacaoPanel(this, usuarioLogado, simuladorService, usuarioService);
        simulacaoPanel.setVisible(true);
    }

    private void abrirEstatisticas() {
        EstatisticasPanel estatisticasPanel = new EstatisticasPanel(this, estatisticasService);
        estatisticasPanel.setVisible(true);
    }

    private void abrirGerenciarUsuarios() {
        // Implementação simples inline
        String[] opcoes = {"Cadastrar Usuario", "Listar Usuarios", "Remover Usuario", "Cancelar"};
        int escolha = JOptionPane.showOptionDialog(this,
                "Escolha uma opção:", "Gerenciar Usuários",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[3]);

        switch (escolha) {
            case 0 ->
                cadastrarNovoUsuario(); // Cadastrar
            case 1 ->
                listarUsuarios(); // Listar
            case 2 ->
                removerUsuario(); // Remover
        }
    }

    private void cadastrarNovoUsuario() {
        JTextField loginField = new JTextField(15);
        JPasswordField senhaField = new JPasswordField(15);
        JTextField avatarField = new JTextField(15);
        avatarField.setText("default.png");

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Login:"));
        panel.add(loginField);
        panel.add(new JLabel("Senha:"));
        panel.add(senhaField);
        panel.add(new JLabel("Avatar:"));
        panel.add(avatarField);

        int resultado = JOptionPane.showConfirmDialog(this, panel, "Cadastrar Usuário", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String login = loginField.getText();
                String senha = new String(senhaField.getPassword());
                String avatar = avatarField.getText();

                if (usuarioService.cadastrarUsuario(login, senha, avatar)) {
                    JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro: Login já existe!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarUsuarios() {
        StringBuilder sb = new StringBuilder("Usuários cadastrados:\n\n");
        usuarioService.listarUsuarios().forEach(u
                -> sb.append(String.format("Login: %s | Avatar: %s | Pontuação: %d\n",
                        u.getLogin(), u.getAvatar(), u.getPontuacao())));

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Lista de Usuários", JOptionPane.INFORMATION_MESSAGE);
    }

    private void removerUsuario() {
        String login = JOptionPane.showInputDialog(this, "Digite o login do usuário a ser removido:");
        if (login != null && !login.trim().isEmpty()) {
            if (usuarioService.removerUsuario(login)) {
                JOptionPane.showMessageDialog(this, "Usuário removido com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao remover usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarSobre() {
        String sobre = "Simulação de Criaturas Saltitantes v2\n\n"
                + "Sistema desenvolvido em Java com as seguintes funcionalidades:\n"
                + "• Simulação com formação de clusters\n"
                + "• Guardião do horizonte\n"
                + "• Sistema de usuários com autenticação\n"
                + "• Estatísticas detalhadas\n"
                + "• Interface gráfica intuitiva\n\n"
                + "Versão: 2.0\n"
                + "Desenvolvido para fins educacionais";

        JOptionPane.showMessageDialog(this, sobre, "Sobre", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente fazer logout?", "Confirmar Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            usuarioService.logout();
            usuarioLogado = null;
            habilitarBotoes(false);
            mostrarLoginInicial();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Usar o look and feel padrão do sistema
            new MainFrame().setVisible(true);
        });
    }
}
