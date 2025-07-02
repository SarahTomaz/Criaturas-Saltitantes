package com.example.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuracao {

    private static final String ARQUIVO_CONFIG = "config.properties";
    private Properties propriedades;

    public Configuracao() {
        propriedades = new Properties();
        try (InputStream input = new FileInputStream(ARQUIVO_CONFIG)) {
            propriedades.load(input);
        } catch (IOException ex) {
            // Usar valores padrão se arquivo não existir
            propriedades.setProperty("max.iteracoes", "200");
            propriedades.setProperty("tamanho.horizonte", "100");
        }
    }

    public int getMaxIteracoes() {
        return Integer.parseInt(propriedades.getProperty("max.iteracoes"));
    }

    public double getTamanhoHorizonte() {
        return Double.parseDouble(propriedades.getProperty("tamanho.horizonte"));
    }
}
