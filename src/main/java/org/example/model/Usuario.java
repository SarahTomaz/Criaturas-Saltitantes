package org.example.model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String login;
    private String senhaHash;
    private String avatar;
    private int pontuacao; // Quantidade de simulações bem-sucedidas
    private int totalSimulacoes;

    public Usuario(String login, String senha, String avatar) {
        this.login = login;
        this.senhaHash = hashSenha(senha);
        this.avatar = avatar;
        this.pontuacao = 0;
        this.totalSimulacoes = 0;
    }

    private String hashSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criar hash da senha", e);
        }
    }

    public boolean autenticar(String senha) {
        return this.senhaHash.equals(hashSenha(senha));
    }

    public void incrementarSimulacaoBemSucedida() {
        this.pontuacao++;
        this.totalSimulacoes++;
    }

    public void incrementarTotalSimulacoes() {
        this.totalSimulacoes++;
    }

    public double getTaxaSucesso() {
        if (totalSimulacoes == 0) {
            return 0.0;
        }
        return (double) pontuacao / totalSimulacoes;
    }

    // Getters e Setters
    public String getLogin() {
        return login;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public int getTotalSimulacoes() {
        return totalSimulacoes;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public void setTotalSimulacoes(int totalSimulacoes) {
        this.totalSimulacoes = totalSimulacoes;
    }

    @Override
    public String toString() {
        return String.format("Usuario[login=%s, avatar=%s, pontuacao=%d, total=%d]",
                login, avatar, pontuacao, totalSimulacoes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Usuario usuario = (Usuario) obj;
        return login.equals(usuario.login);
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }
}
