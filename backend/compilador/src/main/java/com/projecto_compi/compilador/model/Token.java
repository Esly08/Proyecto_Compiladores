package com.projecto_compi.compilador.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {
    @JsonProperty("token")
    String lexema;

    @JsonProperty("type")
    TokenType tipo;

    @JsonProperty("line")
    int linea;

    @JsonProperty("column")
    int columna;

    // Constructor
    public Token(String lexema, TokenType tipo, int linea, int columna) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }

    // Getters (necesarios para que Jackson pueda serializar la clase)
    public String getLexema() {
        return lexema;
    }

    public TokenType getTipo() {
        return tipo;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }
}