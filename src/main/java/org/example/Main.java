package org.example;

public class Main {

    public static void main(String[] args) {
        // Inicia a interface gráfica da simulação de criaturas saltitantes
        javax.swing.SwingUtilities.invokeLater(() -> {
            new org.example.ui.MainFrame().setVisible(true);
        });
    }
}
