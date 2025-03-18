public class Token { // Clase Token: almacena el lexema, el tipo, la línea y la columna

    String lexema;
    TokenType tipo;
    int linea;
    int columna;

    public Token(String lexema, TokenType tipo, int linea, int columna) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }

    public String toString() {
        return "Token(" + lexema + ", " + tipo + ", línea: " + linea + ", columna: " + columna + ")";
    }

}
