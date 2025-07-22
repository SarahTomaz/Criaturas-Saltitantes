package org.example.domain;

import org.example.model.Cluster;
import org.example.model.Criatura;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClusterDomainTest {

    private Cluster cluster;
    private Criatura criatura1;
    private Criatura criatura2;

    @BeforeEach
    void setUp() {
        cluster = new Cluster(1);
        criatura1 = new Criatura(1, 10.0);
        criatura2 = new Criatura(2, 10.5);
    }

    @Test
    @DisplayName("Deve criar cluster vazio com posição zero")
    void criarCluster_Vazio() {
        assertAll(
                () -> assertEquals(1, cluster.getId()),
                () -> assertEquals(0.0, cluster.getPosicao(), 0.001),
                () -> assertEquals(0, cluster.getTamanho()),
                () -> assertEquals(0, cluster.getTotalMoedas())
        );
    }

    @Test
    @DisplayName("Deve adicionar criatura ao cluster")
    void adicionarCriatura_Valida() {
        cluster.adicionarCriatura(criatura1);

        assertAll(
                () -> assertEquals(1, cluster.getTamanho()),
                () -> assertEquals(10.0, cluster.getPosicao(), 0.001),
                () -> assertEquals(1_000_000, cluster.getTotalMoedas())
        );
    }

    @Test
    @DisplayName("Deve calcular posição média corretamente")
    void calcularPosicaoMedia() {
        cluster.adicionarCriatura(criatura1);
        cluster.adicionarCriatura(criatura2);

        assertEquals(10.25, cluster.getPosicao(), 0.001);
    }

    @Test
    @DisplayName("Deve remover criaturas e recalcular posição")
    void removerCriaturas() {
        cluster.adicionarCriatura(criatura1);
        cluster.adicionarCriatura(criatura2);
        cluster.removerCriatura(criatura1);

        assertEquals(1, cluster.getTamanho());
        assertEquals(10.5, cluster.getPosicao(), 0.001);
    }
}
