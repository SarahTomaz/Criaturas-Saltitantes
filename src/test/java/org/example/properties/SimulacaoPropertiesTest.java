package org.example.properties;

import static org.assertj.core.api.Assertions.assertThat;
import org.example.model.Simulacao;
import org.example.model.Usuario;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

/**
 * Testes baseados em propriedades para a classe Simulacao.
 *
 * <p>
 * Verifica invariantes e propriedades matemáticas da simulação.</p>
 */
class SimulacaoPropertiesTest {

    /**
     * Propriedade: O número total de moedas deve ser constante durante a
     * simulação.
     */
    @Property
    void totalMoedasDeveSerConstante(
            @ForAll @IntRange(min = 1, max = 100) int numCriaturas,
            @ForAll @IntRange(min = 1, max = 1000) int iteracoes) {

        Usuario usuario = new Usuario("teste", "senha", "avatar.png");
        Simulacao simulacao = new Simulacao(usuario, numCriaturas, iteracoes + 1);

        long totalMoedasInicial = calcularTotalMoedas(simulacao);

        for (int i = 0; i < iteracoes; i++) {
            simulacao.executarIteracao();
            long totalMoedasAtual = calcularTotalMoedas(simulacao);

            assertThat(totalMoedasAtual).isEqualTo(totalMoedasInicial);
        }
    }

    private long calcularTotalMoedas(Simulacao simulacao) {
        return simulacao.getCriaturas().stream()
                .filter(c -> c.isAtiva())
                .mapToLong(c -> c.getMoedas())
                .sum()
                + simulacao.getClusters().stream()
                        .mapToLong(c -> c.getTotalMoedas())
                        .sum()
                + simulacao.getGuardiao().getMoedas();
    }
}
