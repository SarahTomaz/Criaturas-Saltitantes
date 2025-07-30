package org.example.model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Representa um usuário do sistema de simulação de criaturas saltitantes.
 *
 * <p>
 * Esta classe gerencia informações de usuário incluindo autenticação segura,
 * pontuação baseada em simulações bem-sucedidas e persistência de dados.</p>
 *
 * <p>
 * <b>Invariantes da classe:</b></p>
 * <ul>
 * <li>login não pode ser nulo ou vazio</li>
 * <li>senhaHash é sempre uma string SHA-256 válida</li>
 * <li>pontuacao >= 0 (número de simulações bem-sucedidas)</li>
 * <li>totalSimulacoes >= pontuacao</li>
 * <li>taxaSucesso está entre 0.0 e 1.0</li>
 * </ul>
 *
 * @author Sistema de Simulação
 * @version 2.0
 * @since 1.0
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Nome de usuário único no sistema
     */
    private String login;

    /**
     * Hash SHA-256 da senha do usuário
     */
    private String senhaHash;

    /**
     * Nome do arquivo de avatar do usuário
     */
    private String avatar;

    /**
     * Quantidade de simulações bem-sucedidas
     */
    private int pontuacao;

    /**
     * Total de simulações realizadas
     */
    private int totalSimulacoes;

    /**
     * Cria um novo usuário com as credenciais especificadas.
     *
     * <p>
     * A senha é automaticamente hasheada usando SHA-256 para segurança. A
     * pontuação e total de simulações são inicializados como zero.</p>
     *
     * @param login Nome de usuário único (não pode ser nulo ou vazio)
     * @param senha Senha em texto plano (será hasheada internamente)
     * @param avatar Nome do arquivo de avatar (pode ser nulo, padrão:
     * "default.png")
     * @throws IllegalArgumentException se login for nulo/vazio ou senha
     * inválida
     */
    public Usuario(String login, String senha, String avatar) {
        // Validação do login
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login não pode ser nulo ou vazio");
        }

        // Validação da senha
        if (senha == null || senha.length() < 4) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 4 caracteres");
        }

        this.login = login;
        this.senhaHash = hashSenha(senha);
        this.avatar = avatar;
        this.pontuacao = 0;
        this.totalSimulacoes = 0;
    }

    /**
     * Gera hash SHA-256 da senha fornecida.
     *
     * @param senha Senha em texto plano
     * @return Hash SHA-256 da senha como string hexadecimal
     * @throws RuntimeException se algoritmo SHA-256 não estiver disponível
     */
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

    /**
     * Autentica o usuário com a senha fornecida.
     *
     * <p>
     * Compara o hash da senha fornecida com o hash armazenado.</p>
     *
     * @param senha Senha em texto plano para verificação
     * @return true se a senha for válida, false caso contrário
     */
    public boolean autenticar(String senha) {
        return this.senhaHash.equals(hashSenha(senha));
    }

    /**
     * Incrementa a pontuação e total de simulações para uma simulação
     * bem-sucedida.
     *
     * <p>
     * Este método deve ser chamado quando uma simulação é completada com
     * sucesso. Incrementa tanto a pontuação quanto o total de simulações.</p>
     */
    public void incrementarSimulacaoBemSucedida() {
        this.pontuacao++;
        this.totalSimulacoes++;
    }

    /**
     * Incrementa apenas o total de simulações (para simulações não
     * bem-sucedidas).
     *
     * <p>
     * Este método deve ser chamado quando uma simulação falha ou é
     * interrompida.</p>
     */
    public void incrementarTotalSimulacoes() {
        this.totalSimulacoes++;
    }

    /**
     * Incrementa a pontuação para uma simulação bem-sucedida.
     *
     * <p>
     * Este método incrementa tanto a pontuação quanto o total de simulações,
     * representando uma simulação que foi completada com sucesso.</p>
     */
    public void incrementarPontuacao() {
        incrementarSimulacaoBemSucedida();
    }

    /**
     * Calcula a taxa de sucesso do usuário.
     *
     * <p>
     * A taxa é calculada como: pontuacao / totalSimulacoes</p>
     *
     * @return Taxa de sucesso entre 0.0 e 1.0, ou 0.0 se nenhuma simulação foi
     * realizada
     */
    public double getTaxaSucesso() {
        if (totalSimulacoes == 0) {
            return 0.0;
        }
        return (double) pontuacao / totalSimulacoes;
    }

    // Getters e Setters
    /**
     * Obtém o nome de usuário.
     *
     * @return Nome de usuário
     */
    public String getLogin() {
        return login;
    }

    /**
     * Obtém o nome do arquivo de avatar.
     *
     * @return Nome do arquivo de avatar
     */
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

    /**
     * Valida as credenciais do usuário.
     *
     * @param senha Senha em texto plano para validação
     * @return true se a senha for válida, false caso contrário
     */
    public boolean validarCredenciais(String senha) {
        if (senha == null) {
            return false;
        }
        return autenticar(senha);
    }
}
