import java.util.*;

public class AnalizadorLexico {
    private String input;
    private int pos;
    private int line;
    private int column;
    private List<Token> tokens;
    private List<Token> tablaSimbolos;
    private List<ErrorLexico> errores;

    // Palabras reservadas (se comparan en minúsculas para ignorar
    // mayúsculas/minúsculas)
    private static final Set<String> PALABRAS_RESERVADAS = new HashSet<>(Arrays.asList(
            "entero", "real", "booleano", "caracter", "cadena",
            "if", "else", "for", "do", "while",
            "escribirlinea", "escribir", "longitud", "acadena"));

    public AnalizadorLexico(String input) {
        this.input = input;
        this.pos = 0;
        this.line = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
        this.tablaSimbolos = new ArrayList<>();
        this.errores = new ArrayList<>();
    }

    public void analizar() {
        while (pos < input.length()) {
            char actual = input.charAt(pos);

            // Ignoramos espacios, saltos de línea y tabuladores
            if (Character.isWhitespace(actual)) {
                avanzar();
                continue;
            }

            // Manejo de comentarios
            if (actual == '/') {
                if (peek(1) == '/') {
                    // Comentario de una sola línea: se ignora hasta el final de línea
                    while (pos < input.length() && input.charAt(pos) != '\n') {
                        avanzar();
                    }
                    continue;
                } else if (peek(1) == '*') {
                    // Comentario multilínea
                    avanzar(); // salta '/'
                    avanzar(); // salta '*'
                    boolean comentarioCerrado = false;
                    while (pos < input.length()) {
                        if (input.charAt(pos) == '*' && peek(1) == '/') {
                            avanzar(); // salta '*'
                            avanzar(); // salta '/'
                            comentarioCerrado = true;
                            break;
                        } else {
                            // Actualizamos línea y columna si se encuentra salto de línea
                            if (input.charAt(pos) == '\n') {
                                line++;
                                column = 1;
                            } else {
                                column++;
                            }
                            pos++;
                        }
                    }
                    if (!comentarioCerrado) {
                        errores.add(new ErrorLexico("Comentario multilínea no cerrado", line, column, "*"));
                    }
                    continue;
                }
            }

            // Identificadores o palabras reservadas (se aceptan letras y dígitos y '_')
            if (Character.isLetter(actual)) {
                int inicioCol = column;
                StringBuilder lexema = new StringBuilder();
                while (pos < input.length()
                        && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_')) {
                    lexema.append(input.charAt(pos));
                    avanzar();
                }
                String lex = lexema.toString();
                // Comparación en minúsculas para hacer el lenguaje case-insensitive
                String lexLower = lex.toLowerCase();
                Token token;
                if (PALABRAS_RESERVADAS.contains(lexLower)) {
                    token = new Token(lex, TokenType.PALABRA_RESERVADA, line, inicioCol);
                } else {
                    token = new Token(lex, TokenType.IDENTIFICADOR, line, inicioCol);
                    // Agregar a la tabla de símbolos si no existe
                    if (!tablaSimbolos.stream().anyMatch(t -> t.lexema.equals(lex))) {
                        tablaSimbolos.add(token);
                    }
                }
                tokens.add(token);
                continue;
            }

            // Números: pueden ser enteros o reales
            if (Character.isDigit(actual)) {
                int inicioCol = column;
                StringBuilder lexema = new StringBuilder();
                boolean esReal = false;
                while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                    lexema.append(input.charAt(pos));
                    avanzar();
                }
                // Si se encuentra un punto se asume número real
                if (pos < input.length() && input.charAt(pos) == '.') {
                    esReal = true;
                    lexema.append('.');
                    avanzar();
                    while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                        lexema.append(input.charAt(pos));
                        avanzar();
                    }
                }
                Token token;
                if (esReal) {
                    token = new Token(lexema.toString(), TokenType.REAL, line, inicioCol);
                } else {
                    token = new Token(lexema.toString(), TokenType.ENTERO, line, inicioCol);
                }
                tokens.add(token);
                continue;
            }

            // Literal de carácter: se delimita por comilla simple
            if (actual == '\'') {
                int inicioCol = column;
                StringBuilder lexema = new StringBuilder();
                lexema.append(actual);
                avanzar(); // salta la comilla de apertura
                if (pos < input.length()) {
                    char ch = input.charAt(pos);
                    lexema.append(ch);
                    avanzar();
                    if (pos < input.length() && input.charAt(pos) == '\'') {
                        lexema.append('\'');
                        avanzar();
                        Token token = new Token(lexema.toString(), TokenType.CARACTER, line, inicioCol);
                        tokens.add(token);
                    } else {
                        errores.add(new ErrorLexico("Carácter mal formado", line, column,
                                (pos < input.length() ? String.valueOf(input.charAt(pos)) : "EOF")));
                    }
                } else {
                    errores.add(new ErrorLexico("Carácter no cerrado", line, column, "'"));
                }
                continue;
            }

            // Literal de cadena: se delimita por comillas dobles
            if (actual == '"') {
                int inicioCol = column;
                StringBuilder lexema = new StringBuilder();
                lexema.append(actual);
                avanzar(); // salta la comilla de apertura
                while (pos < input.length() && input.charAt(pos) != '"') {
                    // Si se encuentra un salto de línea dentro de la cadena se puede reportar error
                    if (input.charAt(pos) == '\n') {
                        errores.add(
                                new ErrorLexico("Cadena literal no permitida en múltiples líneas", line, column, "\""));
                        break;
                    }
                    lexema.append(input.charAt(pos));
                    avanzar();
                }
                if (pos < input.length() && input.charAt(pos) == '"') {
                    lexema.append('"');
                    avanzar();
                    Token token = new Token(lexema.toString(), TokenType.CADENA, line, inicioCol);
                    tokens.add(token);
                } else {
                    errores.add(new ErrorLexico("Cadena no cerrada", line, column, "\""));
                }
                continue;
            }

            // Operadores y símbolos
            // Se verifica primero la posibilidad de operadores compuestos (dos caracteres)
            int inicioCol = column;
            String op = String.valueOf(actual);
            char siguiente = peek(1);
            if ((actual == '+' && siguiente == '+') ||
                    (actual == '-' && siguiente == '-') ||
                    (actual == '=' && siguiente == '=') ||
                    (actual == '>' && siguiente == '=') ||
                    (actual == '<' && siguiente == '=') ||
                    (actual == '!' && siguiente == '=') ||
                    (actual == '|' && siguiente == '|') ||
                    (actual == '&' && siguiente == '&')) {
                op += siguiente;
                avanzar();
                avanzar();
                Token token = new Token(op, TokenType.OPERADOR, line, inicioCol);
                tokens.add(token);
                continue;
            }
            // Operadores y símbolos de un solo carácter
            if ("+-*/^#!<>(){};,=".indexOf(actual) != -1) {
                avanzar();
                Token token = new Token(op, TokenType.OPERADOR, line, inicioCol);
                tokens.add(token);
                continue;
            }

            // Si el carácter no se reconoce se registra un error
            errores.add(new ErrorLexico("Símbolo no reconocido", line, column, String.valueOf(actual)));
            avanzar();
        }
    }

    // Método para avanzar un carácter actualizando línea y columna
    private void avanzar() {
        if (pos < input.length()) {
            if (input.charAt(pos) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            pos++;
        }
    }

    // Método para obtener el carácter que se encuentra k posiciones adelante
    // (lookahead)
    private char peek(int k) {
        if (pos + k < input.length()) {
            return input.charAt(pos + k);
        }
        return '\0';
    }

    // Getters para tokens, tabla de símbolos y errores
    public List<Token> getTokens() {
        return tokens;
    }

    public List<Token> getTablaSimbolos() {
        return tablaSimbolos;
    }

    public List<ErrorLexico> getErrores() {
        return errores;
    }

    // Método main para pruebas
    public static void main(String[] args) {
        String codigo = "Entero i = 10;\n" +
                "Real r = 3.14;\n" +
                "// Esto es un comentario\n" +
                "+  \n" +
                "if(i >= 10){\n" +
                "   EscribirLinea(\"Valor: \" + i);\n" +
                "}\n";
        AnalizadorLexico analizador = new AnalizadorLexico(codigo);
        analizador.analizar();

        System.out.println("Tokens:");
        for (Token t : analizador.getTokens()) {
            System.out.println(t);
        }

        System.out.println("\nTabla de Símbolos:");
        for (Token t : analizador.getTablaSimbolos()) {
            System.out.println(t);
        }

        System.out.println("\nErrores:");
        for (ErrorLexico err : analizador.getErrores()) {
            System.out.println(err);
        }
    }
}
