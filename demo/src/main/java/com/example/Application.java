package com.example;

import com.example.repository.impl.UsuarioRepositoryImpl;
import com.example.repository.impl.SimulacaoRepositoryImpl;
import com.example.service.*;
import com.example.ui.main.LoginFrame;
import javax.swing.SwingUtilities;

public class Application {

    public static void main(String[] args) {
        // Inicializar repositórios
        UsuarioRepositoryImpl usuarioRepo = new UsuarioRepositoryImpl();
        SimulacaoRepositoryImpl simulacaoRepo = new SimulacaoRepositoryImpl();

        // Inicializar serviços
        UsuarioService usuarioService = new UsuarioService(usuarioRepo);
        AutenticacaoService authService = new AutenticacaoService(usuarioRepo);
        SimulacaoService simulacaoService = new SimulacaoService(simulacaoRepo,
                new EstatisticasService(usuarioRepo));

        // Criar e exibir tela de login
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);
        });
    }
}
