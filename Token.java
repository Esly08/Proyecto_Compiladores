// Clase Token: representa un token léxico identificado por el analizador léxico.
// Contiene el lexema (cadena encontrada), el tipo de token, y la posición (línea y columna) en el archivo fuente.

public class Token {
    // El lexema es la cadena de caracteres que forma el token (por ejemplo, una palabra clave, identificador, número, etc.)
    String lexema;
    TokenType tipo;
    int linea;
    int columna;

    // Constructor de la clase Token. Recibe el lexema, tipo de token, línea y columna.
    public Token(String lexema, TokenType tipo, int linea, int columna) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }

    // Método que devuelve una representación legible del token,
    // útil para depuración o impresión de resultados
    public String toString() {
        return "Token(" + lexema + ", " + tipo + ", línea: " + linea + ", columna: " + columna + ")";
    }

}
