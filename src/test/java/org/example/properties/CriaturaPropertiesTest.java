package org.example.properties;

import static org.assertj.core.api.Assertions.assertThat;
import org.example.model.Criatura;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;

class CriaturaPropertiesTest {

    @Property
    void posicaoDeveFicarEntre0e100(
            @ForAll int id,
            @ForAll @DoubleRange(min = 0.0, max = 100.0) double posicaoInicial) {

        Criatura criatura = new Criatura(id, posicaoInicial);
        criatura.mover();
        assertThat(criatura.getPosicao())
                .isBetween(0.0, 100.0);
    }

    @Property
    void moedasNuncaDevemSerNegativas(
            @ForAll int id,
            @ForAll @DoubleRange(min = 0.0, max = 100.0) double posicao,
            @ForAll int moedasParaPerder) {
        Criatura criatura = new Criatura(id, posicao);
        int moedasIniciais = criatura.getMoedas();

        int novaQuantidade = Math.max(0, moedasIniciais - moedasParaPerder);
        criatura.setMoedas(novaQuantidade);

        assertThat(criatura.getMoedas())
                .isBetween(0, moedasIniciais + 1);
    }

}
