package ui;

import java.awt.*;
import javax.swing.*;
import model.Usuario;
import service.UsuarioService;

public class LoginDialog extends JDialog {

    private final UsuarioService usuarioService;
    private Usuario usuarioLogado;

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnLogin;
    private JButton btnCadastrar;
    private JButton btnCancelar;

    public LoginDialog(Frame parent, UsuarioService usuarioService) {
        super(parent, "Login", true);
        this.usuarioService = usuarioService;
        configurarInterface();
    }

    private void configurarInterface() {
        setSize(350, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel principal
        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel titulo = new JLabel("Faça seu login", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painelPrincipal.add(titulo, gbc);

        // Campos de login
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        painelPrincipal.add(new JLabel("Login:"), gbc);

        gbc.gridx = 1;
        txtLogin = new JTextField(15);
        painelPrincipal.add(txtLogin, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        painelPrincipal.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        txtSenha = new JPasswordField(15);
        painelPrincipal.add(txtSenha, gbc);

        add(painelPrincipal, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(event -> realizarLogin());

        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.addActionListener(event -> abrirCadastro());

        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(event -> dispose());

        painelBotoes.add(btnLogin);
        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnCancelar);

        add(painelBotoes, BorderLayout.SOUTH);

        // Configurar tecla Enter para login
        getRootPane().setDefaultButton(btnLogin);

        // Focar no campo de login
        SwingUtilities.invokeLater(() -> txtLogin.requestFocus());
    }

    private void realizarLogin() {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, preencha todos os campos.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario usuario = usuarioService.autenticar(login, senha);
        if (usuario != null) {
            usuarioLogado = usuario;
            JOptionPane.showMessageDialog(this,
                    "Login realizado com sucesso!\nBem-vindo, " + usuario.getLogin() + "!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Login ou senha incorretos.",
                    "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
            txtSenha.requestFocus();
        }
    }

    private void abrirCadastro() {
        CadastroDialog cadastroDialog = new CadastroDialog(this, usuarioService);
        cadastroDialog.setVisible(true);

        if (cadastroDialog.isCadastroRealizado()) {
            JOptionPane.showMessageDialog(this,
                    "Cadastro realizado com sucesso!\nAgora você pode fazer login.");
        }
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}

class CadastroDialog extends JDialog {

    private final UsuarioService usuarioService;
    private boolean cadastroRealizado = false;

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;
    private JTextField txtAvatar;

    public CadastroDialog(Dialog parent, UsuarioService usuarioService) {
        super(parent, "Cadastro de Usuário", true);
        this.usuarioService = usuarioService;
        configurarInterface();
    }

    private void configurarInterface() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel principal
        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel titulo = new JLabel("Cadastro de Novo Usuário", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painelPrincipal.add(titulo, gbc);

        // Campos
        gbc.gridwidth = 1;

        gbc.gridy = 1;
        gbc.gridx = 0;
        painelPrincipal.add(new JLabel("Login:"), gbc);
        gbc.gridx = 1;
        txtLogin = new JTextField(15);
        painelPrincipal.add(txtLogin, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        painelPrincipal.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        txtSenha = new JPasswordField(15);
        painelPrincipal.add(txtSenha, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        painelPrincipal.add(new JLabel("Confirmar Senha:"), gbc);
        gbc.gridx = 1;
        txtConfirmarSenha = new JPasswordField(15);
        painelPrincipal.add(txtConfirmarSenha, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        painelPrincipal.add(new JLabel("Avatar:"), gbc);
        gbc.gridx = 1;
        txtAvatar = new JTextField(15);
        txtAvatar.setText("default.png");
        painelPrincipal.add(txtAvatar, gbc);

        add(painelPrincipal, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());

        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.addActionListener(event -> realizarCadastro());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(event -> dispose());

        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnCancelar);

        add(painelBotoes, BorderLayout.SOUTH);

        // Focar no campo de login
        SwingUtilities.invokeLater(() -> txtLogin.requestFocus());
    }

    private void realizarCadastro() {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());
        String confirmarSenha = new String(txtConfirmarSenha.getPassword());
        String avatar = txtAvatar.getText().trim();

        if (login.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, preencha todos os campos obrigatórios.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            JOptionPane.showMessageDialog(this,
                    "As senhas não coincidem.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
            txtConfirmarSenha.setText("");
            txtSenha.requestFocus();
            return;
        }

        try {
            if (usuarioService.cadastrarUsuario(login, senha, avatar)) {
                cadastroRealizado = true;
                JOptionPane.showMessageDialog(this,
                        "Usuário cadastrado com sucesso!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro: Login já existe!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCadastroRealizado() {
        return cadastroRealizado;
    }
}
