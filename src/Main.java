
import javax.swing.*;
import ui.MainFrame;

public class Main {

    public static void main(String[] args) {
        // Cria e exibe a janela principal
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
