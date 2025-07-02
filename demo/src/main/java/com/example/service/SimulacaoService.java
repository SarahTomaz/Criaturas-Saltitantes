package com.example.service;

import com.example.model.entities.*;
import com.example.model.simulation.*;
import com.example.repository.SimulacaoRepository;

public class SimulacaoService {

    private final SimulacaoRepository simulacaoRepository;
    private final EstatisticasService estatisticasService;

    public SimulacaoService(SimulacaoRepository simulacaoRepository,
            EstatisticasService estatisticasService) {
        this.simulacaoRepository = simulacaoRepository;
        this.estatisticasService = estatisticasService;
    }

    public ResultadoSimulacao executarSimulacao(Usuario usuario, ParametrosSimulacao parametros) {
        SimuladorEngine simulador = new SimuladorEngine();
        simulador.iniciarSimulacao(parametros);

        StatusSimulacao status = simulador.executarSimulacaoCompleta();
        ResultadoSimulacao resultado = criarResultado(usuario, simulador, status);

        estatisticasService.registrarSimulacao(usuario, resultado);
        simulacaoRepository.salvarResultado(resultado);

        return resultado;
    }

    private ResultadoSimulacao criarResultado(Usuario usuario, SimuladorEngine simulador,
            StatusSimulacao status) {
        ResultadoSimulacao resultado = new ResultadoSimulacao(usuario.getLogin());
        resultado.inicializarEstatisticas(
                simulador.getCriaturas().size(),
                simulador.getCriaturas().stream().mapToInt(Criatura::getMoedas).sum(),
                simulador.getGuardiao() != null ? simulador.getGuardiao().getPosicao() : 0
        );

        // Configura outros dados do resultado
        return resultado;
    }
}
