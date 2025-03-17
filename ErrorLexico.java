public class ErrorLexico {
    // Clase para representar errores léxicos
    String mensaje;
    int linea;
    int columna;
    String caracter;

    public ErrorLexico(String mensaje, int linea, int columna, String caracter) {
        this.mensaje = mensaje;
        this.linea = linea;
        this.columna = columna;
        this.caracter = caracter;
    }

    public String toString() {
        return "ErrorLexico: " + mensaje + " en línea " + linea + ", columna " + columna + ". Caracter: " + caracter;
    }
}