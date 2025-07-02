package com.example.service;

import com.example.model.entities.Usuario;
import com.example.repository.UsuarioRepository;
import com.example.exception.UsuarioException;
import com.example.utils.CriptografiaUtils;
import java.util.List;

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario cadastrarUsuario(String login, String senha, String avatar) throws UsuarioException {
        if (usuarioRepository.existeUsuario(login)) {
            throw new UsuarioException("Login já está em uso");
        }

        String senhaHash = CriptografiaUtils.gerarHash(senha);
        Usuario novoUsuario = new Usuario(login, senhaHash);
        novoUsuario.setAvatar(avatar);
        
        return usuarioRepository.salvarUsuario(novoUsuario);
    }

    public Usuario autenticar(String login, String senha) throws UsuarioException {
        Usuario usuario = usuarioRepository.buscarPorLogin(login);
        if (usuario == null || !usuario.autenticar(senha, usuario.getSenhaHash())) {
            throw new UsuarioException("Login ou senha inválidos");
        }
        return usuario;
    }

    public void atualizarPontuacao(Usuario usuario, boolean simulacaoBemSucedida) {
        usuario.incrementarPontuacao(simulacaoBemSucedida, 0);
        usuarioRepository.atualizarUsuario(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.listarTodosUsuarios();
    }
}