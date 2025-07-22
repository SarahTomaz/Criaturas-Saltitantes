package org.example.service;

import org.example.model.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioService {

    private static final String ARQUIVO_USUARIOS = "data/usuarios.ser";
    private List<Usuario> usuarios;
    private Usuario usuarioLogado;

    public UsuarioService() {
        this.usuarios = new ArrayList<>();
        carregarUsuarios();
    }

    public boolean cadastrarUsuario(String login, String senha, String avatar) {
        // Validações
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login não pode ser vazio");
        }
        if (senha == null || senha.length() < 4) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 4 caracteres");
        }
        if (avatar == null || avatar.trim().isEmpty()) {
            avatar = "default.png";
        }

        // Verificar se login já existe
        if (buscarUsuarioPorLogin(login).isPresent()) {
            return false; // Usuário já existe
        }

        Usuario novoUsuario = new Usuario(login.trim(), senha, avatar.trim());
        usuarios.add(novoUsuario);
        salvarUsuarios();
        return true;
    }

    public Usuario autenticar(String login, String senha) {
        if (login == null || senha == null) {
            return null;
        }

        Optional<Usuario> usuarioOpt = buscarUsuarioPorLogin(login);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.autenticar(senha)) {
                usuarioLogado = usuario;
                return usuario;
            }
        }
        return null;
    }

    public void logout() {
        usuarioLogado = null;
    }

    public boolean removerUsuario(String login) {
        if (usuarioLogado != null && usuarioLogado.getLogin().equals(login)) {
            return false; // Não pode remover usuário logado
        }

        Optional<Usuario> usuarioOpt = buscarUsuarioPorLogin(login);
        if (usuarioOpt.isPresent()) {
            usuarios.remove(usuarioOpt.get());
            salvarUsuarios();
            return true;
        }
        return false;
    }

    public boolean alterarAvatar(String login, String novoAvatar) {
        Optional<Usuario> usuarioOpt = buscarUsuarioPorLogin(login);
        if (usuarioOpt.isPresent()) {
            usuarioOpt.get().setAvatar(novoAvatar);
            salvarUsuarios();
            return true;
        }
        return false;
    }

    private Optional<Usuario> buscarUsuarioPorLogin(String login) {
        return usuarios.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .findFirst();
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean temUsuarioLogado() {
        return usuarioLogado != null;
    }

    public int getTotalUsuarios() {
        return usuarios.size();
    }

    @SuppressWarnings("unchecked")
    private void carregarUsuarios() {
        File arquivo = new File(ARQUIVO_USUARIOS);
        if (!arquivo.exists()) {
            // Criar diretório se não existir
            arquivo.getParentFile().mkdirs();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            usuarios = (List<Usuario>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
            usuarios = new ArrayList<>();
        }
    }

    public void salvarUsuarios() {
        File arquivo = new File(ARQUIVO_USUARIOS);
        arquivo.getParentFile().mkdirs();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuários: " + e.getMessage());
        }
    }

    // Método para atualizar dados do usuário após simulação
    public void atualizarUsuario(Usuario usuario) {
        Optional<Usuario> usuarioExistente = buscarUsuarioPorLogin(usuario.getLogin());
        if (usuarioExistente.isPresent()) {
            // Atualizar referência na lista
            int index = usuarios.indexOf(usuarioExistente.get());
            usuarios.set(index, usuario);
            salvarUsuarios();
        }
    }

    /**
     * Atualiza a pontuação de um usuário específico
     *
     * @param login Login do usuário
     * @param pontuacao Nova pontuação a ser definida
     * @return true se a atualização foi bem-sucedida, false caso contrário
     */
    public boolean atualizarPontuacao(String login, int pontuacao) {
        Optional<Usuario> usuarioOpt = buscarUsuarioPorLogin(login);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Assumindo que a pontuação representa simulações bem-sucedidas
            // e queremos incrementar com base no resultado da simulação
            if (pontuacao > 0) {
                usuario.incrementarSimulacaoBemSucedida();
            } else {
                usuario.incrementarTotalSimulacoes();
            }
            salvarUsuarios();
            return true;
        }
        return false;
    }
}
