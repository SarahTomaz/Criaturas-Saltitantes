package com.example.service;

import com.example.model.entities.Usuario;
import com.example.exception.AutenticacaoException;
import com.example.repository.UsuarioRepository;

public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario autenticarUsuario(String login, String senha) throws AutenticacaoException {
        try {
            Usuario usuario = usuarioRepository.buscarPorLogin(login);
            if (usuario != null && usuario.autenticar(senha, usuario.getSenhaHash())) {
                return usuario;
            }
            throw new AutenticacaoException("Credenciais inválidas");
        } catch (Exception e) {
            throw new AutenticacaoException("Erro durante autenticação: " + e.getMessage());
        }
    }
}
