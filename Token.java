// Token.java
public class Token {
    private TokenType type;
    private String lexema;
    private int linea;
    
    public Token(TokenType type, String lexema, int linea) {
        this.type = type;
        this.lexema = lexema;
        this.linea = linea;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getLexema() {
        return lexema;
    }
    
    public int getLinea() {
        return linea;
    }
    
    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexema='" + lexema + '\'' +
                ", linea=" + linea +
                '}';
    }
}
