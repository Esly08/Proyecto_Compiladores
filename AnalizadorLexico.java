// AnalizadorLexico.java
public class AnalizadorLexico {
    private String codigoFuente;
    private int posicion;
    private int linea;
    
    public AnalizadorLexico(String codigoFuente) {
        this.codigoFuente = codigoFuente;
        this.posicion = 0;
        this.linea = 1;
    }
    
    public Token getSiguienteToken() {
        ignorarEspaciosYSaltos();
        
        if (finDeArchivo()) {
            return new Token(TokenType.TOKEN_EOF, "", linea);
        }
        
        char c = peekChar();
        
        // Manejo de comentarios
        if (c == '/') {
            char c2 = peekChar(1);
            if (c2 == '/') {
                consumirComentarioUnaLinea();
                return getSiguienteToken();
            } else if (c2 == '*') {
                consumirComentarioMultilinea();
                return getSiguienteToken();
            } else {
                avanzar(1);
                return new Token(TokenType.TOKEN_OP_DIV, "/", linea);
            }
        }
        
        // Operadores de dos caracteres (ej. ++, --, ==, !=, <=, >=)
        if (c == '+') {
            if (peekChar(1) == '+') {
                avanzar(2);
                return new Token(TokenType.TOKEN_OP_INCREMENTO, "++", linea);
            } else {
                avanzar(1);
                return new Token(TokenType.TOKEN_OP_SUM, "+", linea);
            }
        }
        
        if (c == '-') {
            if (peekChar(1) == '-') {
                avanzar(2);
                return new Token(TokenType.TOKEN_OP_DECREMENTO, "--", linea);
            } else {
                avanzar(1);
                return new Token(TokenType.TOKEN_OP_RESTA, "-", linea);
            }
        }
        
        if (c == '=') {
            if (peekChar(1) == '=') {
                avanzar(2);
                return new Token(TokenType.TOKEN_OP_IGUAL, "==", linea);
            } else {
                avanzar(1);
                return new Token(TokenType.TOKEN_ASIGNACION, "=", linea);
            }
        }
        
        if (c == '!') {
            if (peekChar(1) == '=') {
                avanzar(2);
                return new Token(TokenType.TOKEN_OP_DIFERENTE, "!=", linea);
            } else {
                avanzar(1);
                return new Token(TokenType.TOKEN_OP_NOT, "!", linea);
            }
        }
        
        if (c == '<') {
            if (peekChar(1) == '=') {
                avanzar(2);
                return new Token(TokenType.TOKEN_OP_MENORIGUAL, "<=", linea);
            } else {
                avanzar(1);
                return new Token(TokenType.TOKEN_OP_MENOR, "<", linea);
            }
        }
        
        if (c == '>') {
            if (peekChar(1) == '=') {
                avanzar(2);
                return new Token(TokenType.TOKEN_OP_MAYORIGUAL, ">=", linea);
            } else {
                avanzar(1);
                return new Token(TokenType.TOKEN_OP_MAYOR, ">", linea);
            }
        }
        
        // Operadores lógicos && y ||
        if (c == '&' && peekChar(1) == '&') {
            avanzar(2);
            return new Token(TokenType.TOKEN_OP_AND, "&&", linea);
        }
        if (c == '|' && peekChar(1) == '|') {
            avanzar(2);
            return new Token(TokenType.TOKEN_OP_OR, "||", linea);
        }
        
        // Cadenas de texto
        if (c == '"') {
            return leerCadena();
        }
        
        // Literales de carácter
        if (c == '\'') {
            return leerCaracter();
        }
        
        // Números (enteros y reales)
        if (Character.isDigit(c) || (c == '-' && Character.isDigit(peekChar(1)))) {
            return leerNumero();
        }
        
        // Identificadores o palabras reservadas (case insensitive)
        if (Character.isLetter(c) || c == '_') {
            return leerIdentificadorOKeyword();
        }
        
        // Símbolos simples: ; { } ( ) ^ #
        switch(c) {
            case ';':
                avanzar(1);
                return new Token(TokenType.TOKEN_PUNTOYCOMA, ";", linea);
            case '{':
                avanzar(1);
                return new Token(TokenType.TOKEN_LLAVE_IZQ, "{", linea);
            case '}':
                avanzar(1);
                return new Token(TokenType.TOKEN_LLAVE_DER, "}", linea);
            case '(':
                avanzar(1);
                return new Token(TokenType.TOKEN_PAR_IZQ, "(", linea);
            case ')':
                avanzar(1);
                return new Token(TokenType.TOKEN_PAR_DER, ")", linea);
            case '^':
                avanzar(1);
                return new Token(TokenType.TOKEN_OP_POT, "^", linea);
            case '#':
                avanzar(1);
                return new Token(TokenType.TOKEN_OP_MOD, "#", linea);
            default:
                // Caracter no reconocido
                avanzar(1);
                return new Token(TokenType.TOKEN_ERROR, String.valueOf(c), linea);
        }
    }
    
    // Métodos auxiliares
    
    private void ignorarEspaciosYSaltos() {
        while (!finDeArchivo()) {
            char c = peekChar();
            if (c == ' ' || c == '\t' || c == '\r') {
                avanzar(1);
            } else if (c == '\n') {
                avanzar(1);
                linea++;
            } else {
                break;
            }
        }
    }
    
    private boolean finDeArchivo() {
        return posicion >= codigoFuente.length();
    }
    
    private char peekChar() {
        return finDeArchivo() ? '\0' : codigoFuente.charAt(posicion);
    }
    
    private char peekChar(int offset) {
        return (posicion + offset >= codigoFuente.length()) ? '\0' : codigoFuente.charAt(posicion + offset);
    }
    
    private void avanzar(int n) {
        posicion += n;
    }
    
    private void consumirComentarioUnaLinea() {
        // Consume los dos caracteres iniciales "//"
        avanzar(2);
        while (!finDeArchivo() && peekChar() != '\n') {
            avanzar(1);
        }
    }
    
    private void consumirComentarioMultilinea() {
        // Consume "/*"
        avanzar(2);
        while (!finDeArchivo()) {
            if (peekChar() == '*' && peekChar(1) == '/') {
                avanzar(2);
                break;
            }
            if (peekChar() == '\n') {
                linea++;
            }
            avanzar(1);
        }
    }
    
    private Token leerCadena() {
        avanzar(1); // consume la comilla de apertura
        StringBuilder sb = new StringBuilder();
        while (!finDeArchivo() && peekChar() != '"') {
            char actual = peekChar();
            if (actual == '\n') {
                linea++;
            }
            sb.append(actual);
            avanzar(1);
        }
        if (!finDeArchivo()) {
            avanzar(1); // consume la comilla de cierre
        }
        return new Token(TokenType.TOKEN_LITERAL_CADENA, sb.toString(), linea);
    }
    
    private Token leerCaracter() {
        avanzar(1); // consume la comilla simple de apertura
        StringBuilder sb = new StringBuilder();
        while (!finDeArchivo() && peekChar() != '\'') {
            char actual = peekChar();
            if (actual == '\n') {
                linea++;
            }
            sb.append(actual);
            avanzar(1);
        }
        if (!finDeArchivo()) {
            avanzar(1); // consume la comilla simple de cierre
        }
        return new Token(TokenType.TOKEN_LITERAL_CARACTER, sb.toString(), linea);
    }
    
    private Token leerNumero() {
        StringBuilder sb = new StringBuilder();
        boolean esReal = false;
        // Manejo de signo negativo (si es parte del número)
        if (peekChar() == '-') {
            sb.append('-');
            avanzar(1);
        }
        while (Character.isDigit(peekChar())) {
            sb.append(peekChar());
            avanzar(1);
        }
        if (peekChar() == '.') {
            esReal = true;
            sb.append('.');
            avanzar(1);
            while (Character.isDigit(peekChar())) {
                sb.append(peekChar());
                avanzar(1);
            }
        }
        if (esReal) {
            return new Token(TokenType.TOKEN_LITERAL_REAL, sb.toString(), linea);
        } else {
            return new Token(TokenType.TOKEN_LITERAL_ENTERO, sb.toString(), linea);
        }
    }
    
    private Token leerIdentificadorOKeyword() {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(peekChar()) || peekChar() == '_') {
            sb.append(peekChar());
            avanzar(1);
        }
        // Para lenguaje case insensitive convertimos a minúsculas
        String lexema = sb.toString().toLowerCase();
        TokenType tipo = identificarKeyword(lexema);
        return new Token(tipo, lexema, linea);
    }
    
    private TokenType identificarKeyword(String lexema) {
        switch (lexema) {
            case "entero": return TokenType.TOKEN_ENTERO;
            case "real": return TokenType.TOKEN_REAL;
            case "booleano": return TokenType.TOKEN_BOOLEANO;
            case "caracter": return TokenType.TOKEN_CARACTER;
            case "cadena": return TokenType.TOKEN_CADENA;
            case "if": return TokenType.TOKEN_IF;
            case "else": return TokenType.TOKEN_ELSE;
            case "for": return TokenType.TOKEN_FOR;
            case "do": return TokenType.TOKEN_DO;
            case "while": return TokenType.TOKEN_WHILE;
            case "escribir": return TokenType.TOKEN_ESCRIBIR;
            case "escribirlinea": return TokenType.TOKEN_ESCRIBIRLINEA;
            case "longitud": return TokenType.TOKEN_LONGITUD;
            case "acadena": return TokenType.TOKEN_ACADENA;
            case "return": return TokenType.TOKEN_RETURN;
            case "true":
            case "false":
                return TokenType.TOKEN_LITERAL_BOOLEANO;
            default:
                return TokenType.TOKEN_ID;
        }
    }
    
    // Método para probar el analizador léxico (opcional)
    public static void main(String[] args) {
        String codigo = "entero contador;\n" +
                        "contador = 1;\n" +
                        "if (contador == 1) { escribirLinea(\"Hola Mundo\"); } else { escribirLinea(\"Adiós\"); }\n" +
                        "// Esto es un comentario de línea\n" +
                        "/* Comentario \n   de varias líneas */";
                        
        AnalizadorLexico analizador = new AnalizadorLexico(codigo);
        Token token;
        do {
            token = analizador.getSiguienteToken();
            System.out.println(token);
        } while (token.getType() != TokenType.TOKEN_EOF);
    }
}
