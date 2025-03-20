
import { useState } from "react";
import { Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@mui/material";
import TextareaAutosize from "@mui/material/TextareaAutosize";
import { analyzeCode, AnalyzeResponse } from "./services/api";

export default function AnalizadorLexico() {
  const [code, setCode] = useState<string>("");
  const [tokens, setTokens] = useState<AnalyzeResponse["tokens"]>([]);
  const [symbolTable, setSymbolTable] = useState<AnalyzeResponse["symbolTable"]>([]);
  const [errors, setErrors] = useState<AnalyzeResponse["errors"]>([]);


  const handleAnalyze = async () => {
    const data = await analyzeCode(code);
    setTokens(data.tokens);
    setSymbolTable(data.symbolTable);
    setErrors(data.errors);
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "20px", padding: "20px", backgroundColor: "#000", color: "#00FF00", minHeight: "100vh", fontWeight: "bold" }}>
      <div style={{ display: "flex", width: "100%", gap: "20px" }}>
        <div style={{ flex: 1, border: "2px solid #00FF00", padding: "20px", borderRadius: "10px", display: "flex", flexDirection: "column", height: "100%" }}>
          <h1 style={{ fontSize: "24px", marginBottom: "10px" }}>Analizador Léxico</h1>
          <TextareaAutosize
            value={code}
            onChange={(e) => setCode(e.target.value)}
            placeholder="Escribe o carga tu código aquí..."
            style={{ minWidth:"550px", maxWidth: "900px" , minHeight: "150px", maxHeight: "600px", backgroundColor: "#000", color: "#00FF00", padding: "10px", border: "1px solid #00FF00", fontSize: "16px" }}
          />
          <div style={{ marginTop: "10px" }}>
            <Button onClick={handleAnalyze} variant="contained" style={{ backgroundColor: "#00FF00", color: "#000000" }}>Ejecutar Análisis</Button>
          </div>
        </div>
      
  
        <div style={{ flex: 1 }}>
          <h2 style={{ fontSize: "20px", borderBottom: "2px solid #00FF00", paddingBottom: "5px" }}>Tokens</h2>
          <TableContainer component={Paper} style={{ backgroundColor: "#000", border: "2px solid #00FF00" }}>
            <Table style={{ borderCollapse: "collapse" }}>
              <TableHead>
                <TableRow style={{ backgroundColor: "#00FF00" }}>
                  <TableCell style={{ color: "#fff", fontWeight: "bold" }}>Token</TableCell>
                  <TableCell style={{ color: "#000", fontWeight: "bold" }}>Tipo</TableCell>
                  <TableCell style={{ color: "#000", fontWeight: "bold" }}>Línea</TableCell>
                  <TableCell style={{ color: "#000", fontWeight: "bold" }}>Columna</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {tokens.map((t, index) => (
                  <TableRow key={index} style={{ borderBottom: "1px solid #00FF00" }}>
                    <TableCell style={{ color: "#00FF00" }}>{t.token}</TableCell>
                    <TableCell style={{ color: "#00FF00" }}>{t.type}</TableCell>
                    <TableCell style={{ color: "#00FF00" }}>{t.line}</TableCell>
                    <TableCell style={{ color: "#00FF00" }}>{t.column}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <h2 style={{ fontSize: "20px", borderBottom: "2px solid #00FF00", paddingBottom: "5px" }}>Tabla de Símbolos</h2>
          <TableContainer component={Paper} style={{ backgroundColor: "#000", border: "2px solid #00FF00", marginTop: "10px" }}>
            <Table style={{ borderCollapse: "collapse" }}>
              <TableHead>
                <TableRow style={{ backgroundColor: "#00FF00" }}>
                  <TableCell style={{  fontWeight: "bold" }}>Identificador</TableCell>
                  <TableCell style={{ color:"#000", fontWeight: "bold" }}>Tipo</TableCell>
                  <TableCell style={{ color: "#000", fontWeight: "bold" }}>Línea</TableCell>
                  <TableCell style={{ color: "#000", fontWeight: "bold" }}>Columna</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {symbolTable.map((s, index) => (
                  <TableRow key={index} style={{ borderBottom: "1px solid #00FF00" }}>
                    <TableCell style={{ color: "#00FF00" }}>{s.token}</TableCell>
                    <TableCell style={{ color: "#00FF00" }}>{s.type}</TableCell>
                    <TableCell style={{ color: "#00FF00" }}>{s.line}</TableCell>
                    <TableCell style={{ color: "#00FF00" }}>{s.column}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </div>
      </div>

      
      <div style={{ width: "100%", border: "2px solid #00FF00", padding: "10px", borderRadius: "10px" }}>
        <h2 style={{ fontSize: "20px", borderBottom: "2px solid #00FF00", paddingBottom: "5px" }}>Errores Léxicos</h2>
        {errors.length === 0 ? (
          <p style={{ color: "#00FF00" }}>Sin errores</p>
        ) : (
          <ul>
            {errors.map((err, index) => (
              <li key={index} style={{ color: "#9d4edd" }}>
             {`Mensaje: ${err.message}, Línea: ${err.line}, Columna: ${err.column}, Caracter: ${err.character}`}
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
