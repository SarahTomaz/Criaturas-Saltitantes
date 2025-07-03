package test;

import model.*;

public class TesteGuardiao {

    public static void main(String[] args) {
        System.out.println("=== TESTE: Moedas do Guardião Durante a Simulação ===\n");

        // Criar usuário de teste
        Usuario usuario = new Usuario("teste", "123", "avatar1");

        // Criar simulação com poucas criaturas para facilitar colisões
        Simulacao sim = new Simulacao(usuario, 3, 100);

        System.out.println("Estado inicial:");
        System.out.println("Guardião: " + sim.getGuardiao());
        System.out.println("Criaturas:");
        for (Criatura c : sim.getCriaturas()) {
            if (c.isAtiva()) {
                System.out.println("  " + c);
            }
        }
        System.out.println();

        // Forçar posições próximas para garantir colisão
        GuardiaoHorizonte guardiao = sim.getGuardiao();
        guardiao.setPosicao(50.0);

        Criatura criatura1 = sim.getCriaturas().get(0);
        criatura1.setPosicao(50.001); // Muito próximo do guardião

        System.out.println("Forçando posições próximas:");
        System.out.println("Guardião na posição: " + guardiao.getPosicao() + " com " + guardiao.getMoedas() + " moedas");
        System.out.println("Criatura " + criatura1.getId() + " na posição: " + criatura1.getPosicao() + " com " + criatura1.getMoedas() + " moedas");
        System.out.println("Distância: " + Math.abs(guardiao.getPosicao() - criatura1.getPosicao()));
        System.out.println();

        // Executar uma iteração
        System.out.println("Executando iteração...");
        boolean continuou = sim.executarIteracao();

        System.out.println("\nApós iteração:");
        System.out.println("Guardião: " + guardiao);
        System.out.println("Criatura " + criatura1.getId() + ": " + criatura1);
        System.out.println("Criatura ativa? " + criatura1.isAtiva());

        // Verificar se as moedas do guardião aumentaram
        if (guardiao.getMoedas() > 0) {
            System.out.println("\n✅ SUCESSO: Guardião ganhou moedas durante a simulação!");
        } else {
            System.out.println("\n❌ PROBLEMA: Guardião não ganhou moedas (pode não ter havido colisão)");
        }

        System.out.println("\n=== FIM DO TESTE ===");
    }
}
