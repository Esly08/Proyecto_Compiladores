package com.projecto_compi.compilador;

import com.projecto_compi.compilador.model.AnalizadorLexico;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/lexer")
@CrossOrigin(origins = "*") // Permitir peticiones desde el frontend

public class AnalizadorController {

    @PostMapping("/analyze")
    public Map<String, Object> analyzeCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        AnalizadorLexico analizador = new AnalizadorLexico(code);
        analizador.analizar();

        Map<String, Object> response = new HashMap<>();
        response.put("tokens", analizador.getTokens());
        response.put("symbolTable", analizador.getTablaSimbolos());
        response.put("errors", analizador.getErrores());

        return response;
    }
}
