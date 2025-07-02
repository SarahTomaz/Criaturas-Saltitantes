package com.example.model.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um usuário do sistema de simulação Contém informações de
 * autenticação, perfil e estatísticas
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String login;
    private String senhaHash;
    private String avatar;
    private int pontuacao;
    private int simulacoesExecutadas;
    private int simulacoesVencidas;
    private LocalDateTime dataUltimoAcesso;
    private LocalDateTime dataCriacao;
    private boolean ativo;
    private List<String> conquistas;
    private String nomeCompleto;
    private String email;
    private NivelUsuario nivel;

    // Enum para níveis de usuário
    public enum NivelUsuario {
        INICIANTE(0, "Iniciante"),
        EXPERIENTE(50, "Experiente"),
        MESTRE(150, "Mestre"),
        LENDA(300, "Lenda");

        private final int pontuacaoMinima;
        private final String nome;

        NivelUsuario(int pontuacaoMinima, String nome) {
            this.pontuacaoMinima = pontuacaoMinima;
            this.nome = nome;
        }

        public int getPontuacaoMinima() {
            return pontuacaoMinima;
        }

        public String getNome() {
            return nome;
        }
    }

    public Usuario() {
        this.conquistas = new ArrayList<>();
        this.dataCriacao = LocalDateTime.now();
        this.dataUltimoAcesso = LocalDateTime.now();
        this.ativo = true;
        this.nivel = NivelUsuario.INICIANTE;
        this.pontuacao = 0;
        this.simulacoesExecutadas = 0;
        this.simulacoesVencidas = 0;
    }

    public Usuario(String login, String senhaHash) {
        this();
        this.login = login;
        this.senhaHash = senhaHash;
        this.avatar = "default_avatar.png";
    }

    public Usuario(String login, String senhaHash, String nomeCompleto, String email) {
        this(login, senhaHash);
        this.nomeCompleto = nomeCompleto;
        this.email = email;
    }

    /**
     * Autentica o usuário com a senha fornecida
     *
     * @param senha senha em texto plano
     * @param senhaHasheada senha já hasheada para comparação
     * @return true se autenticação foi bem-sucedida
     */
    public boolean autenticar(String senha, String senhaHasheada) {
        if (!ativo) {
            return false;
        }

        boolean autenticado = Objects.equals(this.senhaHash, senhaHasheada);

        if (autenticado) {
            this.dataUltimoAcesso = LocalDateTime.now();
        }

        return autenticado;
    }

    /**
     * Incrementa a pontuação do usuário baseada no resultado da simulação
     *
     * @param vitoria se o usuário venceu a simulação
     * @param bonusEspecial bonus adicional por performance especial
     */
    public void incrementarPontuacao(boolean vitoria, int bonusEspecial) {
        simulacoesExecutadas++;

        if (vitoria) {
            simulacoesVencidas++;

            // Pontuação base por vitória
            int pontuacaoBase = 10;

            // Bonus por sequência de vitórias
            double taxaVitoria = (double) simulacoesVencidas / simulacoesExecutadas;
            int bonusSequencia = (int) (pontuacaoBase * taxaVitoria);

            // Bonus por nível atual (quanto maior o nível, mais pontos)
            int bonusNivel = nivel.ordinal() * 2;

            int pontuacaoTotal = pontuacaoBase + bonusSequencia + bonusNivel + bonusEspecial;
            this.pontuacao += pontuacaoTotal;
        } else {
            // Pequena pontuação por participação
            this.pontuacao += 2;
        }

        // Atualiza nível e verifica conquistas
        atualizarNivel();
        verificarConquistas();
    }

    /**
     * Atualiza o nível do usuário baseado na pontuação atual
     */
    private void atualizarNivel() {
        NivelUsuario novoNivel = NivelUsuario.INICIANTE;

        for (NivelUsuario nivel : NivelUsuario.values()) {
            if (this.pontuacao >= nivel.getPontuacaoMinima()) {
                novoNivel = nivel;
            }
        }

        // Se mudou de nível, adiciona conquista
        if (novoNivel != this.nivel && novoNivel.ordinal() > this.nivel.ordinal()) {
            adicionarConquista("Alcançou nível " + novoNivel.getNome());
        }

        this.nivel = novoNivel;
    }

    /**
     * Verifica e adiciona conquistas baseadas nas estatísticas
     */
    private void verificarConquistas() {
        // Conquista por número de simulações
        if (simulacoesExecutadas >= 10 && !possuiConquista("10 Simulações")) {
            adicionarConquista("10 Simulações - Executou 10 simulações");
        }

        if (simulacoesExecutadas >= 50 && !possuiConquista("50 Simulações")) {
            adicionarConquista("50 Simulações - Executou 50 simulações");
        }

        if (simulacoesExecutadas >= 100 && !possuiConquista("Centurião")) {
            adicionarConquista("Centurião - Executou 100 simulações");
        }

        // Conquista por vitórias
        if (simulacoesVencidas >= 5 && !possuiConquista("Primeira Sequência")) {
            adicionarConquista("Primeira Sequência - 5 vitórias");
        }

        if (simulacoesVencidas >= 25 && !possuiConquista("Vitorioso")) {
            adicionarConquista("Vitorioso - 25 vitórias");
        }

        // Conquista por taxa de vitória
        if (simulacoesExecutadas >= 10) {
            double taxaVitoria = (double) simulacoesVencidas / simulacoesExecutadas;

            if (taxaVitoria >= 0.8 && !possuiConquista("Eficiência Máxima")) {
                adicionarConquista("Eficiência Máxima - 80% de vitórias");
            }

            if (taxaVitoria >= 0.9 && !possuiConquista("Quase Perfeito")) {
                adicionarConquista("Quase Perfeito - 90% de vitórias");
            }
        }

        // Conquista por pontuação
        if (pontuacao >= 100 && !possuiConquista("Primeira Centena")) {
            adicionarConquista("Primeira Centena - 100 pontos");
        }

        if (pontuacao >= 500 && !possuiConquista("Especialista")) {
            adicionarConquista("Especialista - 500 pontos");
        }
    }

    /**
     * Adiciona uma conquista ao usuário
     *
     * @param conquista descrição da conquista
     */
    private void adicionarConquista(String conquista) {
        if (!conquistas.contains(conquista)) {
            conquistas.add(conquista);
        }
    }

    /**
     * Verifica se o usuário possui uma conquista específica
     *
     * @param nomeConquista nome da conquista
     * @return true se possui a conquista
     */
    private boolean possuiConquista(String nomeConquista) {
        return conquistas.stream().anyMatch(c -> c.contains(nomeConquista));
    }

    /**
     * Calcula a taxa de vitória do usuário
     *
     * @return taxa de vitória (0.0 a 1.0)
     */
    public double getTaxaVitoria() {
        if (simulacoesExecutadas == 0) {
            return 0.0;
        }
        return (double) simulacoesVencidas / simulacoesExecutadas;
    }

    /**
     * Retorna informações de ranking do usuário
     */
    public String getInfoRanking() {
        return String.format("%s (%s) - %d pts",
                nomeCompleto != null ? nomeCompleto : login,
                nivel.getNome(),
                pontuacao);
    }

    /**
     * Retorna estatísticas detalhadas do usuário
     */
    public String getEstatisticasDetalhadas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTATÍSTICAS DO USUÁRIO ===\n");
        sb.append(String.format("Login: %s\n", login));

        if (nomeCompleto != null) {
            sb.append(String.format("Nome: %s\n", nomeCompleto));
        }

        sb.append(String.format("Nível: %s\n", nivel.getNome()));
        sb.append(String.format("Pontuação: %d\n", pontuacao));
        sb.append(String.format("Simulações Executadas: %d\n", simulacoesExecutadas));
        sb.append(String.format("Simulações Vencidas: %d\n", simulacoesVencidas));
        sb.append(String.format("Taxa de Vitória: %.1f%%\n", getTaxaVitoria() * 100));
        sb.append(String.format("Último Acesso: %s\n",
                dataUltimoAcesso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        sb.append(String.format("Membro desde: %s\n",
                dataCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        if (!conquistas.isEmpty()) {
            sb.append("\nConquistas:\n");
            for (String conquista : conquistas) {
                sb.append("• ").append(conquista).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Retorna próximo nível e pontuação necessária
     */
    public String getProgressoProximoNivel() {
        NivelUsuario proximoNivel = null;

        for (NivelUsuario n : NivelUsuario.values()) {
            if (n.ordinal() > nivel.ordinal()) {
                proximoNivel = n;
                break;
            }
        }

        if (proximoNivel == null) {
            return "Nível máximo alcançado!";
        }

        int pontosNecessarios = proximoNivel.getPontuacaoMinima() - pontuacao;
        return String.format("Próximo: %s (%d pontos necessários)",
                proximoNivel.getNome(), pontosNecessarios);
    }

    /**
     * Desativa o usuário
     */
    public void desativar() {
        this.ativo = false;
    }

    /**
     * Ativa o usuário
     */
    public void ativar() {
        this.ativo = true;
        this.dataUltimoAcesso = LocalDateTime.now();
    }

    /**
     * Atualiza a senha do usuário
     *
     * @param novaSenhaHash nova senha hasheada
     */
    public void atualizarSenha(String novaSenhaHash) {
        this.senhaHash = novaSenhaHash;
    }

    // Getters e Setters
    public String getLogin() {
        return login;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = Math.max(0, pontuacao);
        atualizarNivel();
    }

    public int getSimulacoesExecutadas() {
        return simulacoesExecutadas;
    }

    public void setSimulacoesExecutadas(int simulacoesExecutadas) {
        this.simulacoesExecutadas = Math.max(0, simulacoesExecutadas);
    }

    public int getSimulacoesVencidas() {
        return simulacoesVencidas;
    }

    public void setSimulacoesVencidas(int simulacoesVencidas) {
        this.simulacoesVencidas = Math.max(0, Math.min(simulacoesVencidas, simulacoesExecutadas));
    }

    public LocalDateTime getDataUltimoAcesso() {
        return dataUltimoAcesso;
    }

    public void setDataUltimoAcesso(LocalDateTime dataUltimoAcesso) {
        this.dataUltimoAcesso = dataUltimoAcesso;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<String> getConquistas() {
        return new ArrayList<>(conquistas);
    }

    public void setConquistas(List<String> conquistas) {
        this.conquistas = new ArrayList<>(conquistas);
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public NivelUsuario getNivel() {
        return nivel;
    }

    @Override
    public String toString() {
        return String.format("Usuario[login=%s, nivel=%s, pontos=%d, sims=%d/%d]",
                login, nivel.getNome(), pontuacao, simulacoesVencidas, simulacoesExecutadas);
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
        return Objects.equals(login, usuario.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
