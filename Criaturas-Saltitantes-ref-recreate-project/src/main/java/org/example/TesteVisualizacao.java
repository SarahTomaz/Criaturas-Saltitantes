package org.example;

import javax.swing.*;
import org.example.ui.MainFrame;

public class TesteVisualizacao {

    public static void main(String[] args) {
        System.out.println("Iniciando aplicação de simulação de criaturas...");
        System.out.println("- Interface gráfica com escolha do número de criaturas");
        System.out.println("- Visualização em tempo real das criaturas, clusters e guardião");
        System.out.println("- Exibição visual das moedas de cada elemento");
        System.out.println("- Estatísticas detalhadas durante a execução");

        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                System.out.println("Aplicação iniciada com sucesso!");
            } catch (Exception e) {
                System.err.println("Erro ao iniciar aplicação: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
