package com.example.ui.components;

import com.example.model.entities.*;
import javax.swing.*;
import java.awt.*;

public class PainelGraficoSimulacao extends JPanel {

    private List<Criatura> criaturas;
    private List<Cluster> clusters;
    private GuardiaoHorizonte guardiao;

    public PainelGraficoSimulacao() {
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.WHITE);
    }

    public void atualizarEntidades(List<Criatura> criaturas, List<Cluster> clusters, GuardiaoHorizonte guardiao) {
        this.criaturas = criaturas;
        this.clusters = clusters;
        this.guardiao = guardiao;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Desenhar todas as entidades
        if (criaturas != null) {
            for (Criatura c : criaturas) {
                if (c.isAtiva()) {
                    g.setColor(c.getCor());
                    g.fillOval((int) c.getPosicao() * 7, 200, 20, 20);
                }
            }
        }
        // Desenhar guardião se existir
        if (guardiao != null && guardiao.isAtivo()) {
            g.setColor(Color.BLACK);
            g.fillRect((int) guardiao.getPosicao() * 7, 180, 25, 40);
        }
    }
}
