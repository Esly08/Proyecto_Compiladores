package com.projecto_compi.compilador.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorLexico {
    @JsonProperty("message")
    private String mensaje;

    @JsonProperty("line")
    private int linea;

    @JsonProperty("column")
    private int columna;

    @JsonProperty("character")
    private String caracter;

    public ErrorLexico(String mensaje, int linea, int columna, String caracter) {
        this.mensaje = mensaje;
        this.linea = linea;
        this.columna = columna;
        this.caracter = caracter;
    }

    // Getters para permitir la serialización JSON
    public String getMensaje() {
        return mensaje;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    public String getCaracter() {
        return caracter;
    }
}