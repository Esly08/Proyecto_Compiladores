export interface AnalyzeResponse {
  tokens: { token: string; type: string; line: number; column: number }[];
  symbolTable: { token: string; type: string; line: number; column: number }[];
  errors: { message: string; line: number; column: number; character: string }[];
}

export const analyzeCode = async (code: string): Promise<AnalyzeResponse> => {
  try {
    const response = await fetch("http://localhost:8080/api/lexer/analyze", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ code }),
    });

    if (!response.ok) {
      throw new Error("Error en la respuesta del servidor");
    }

    return await response.json();
  } catch (error) {
    console.error("Error al analizar el código:", error);
    return { tokens: [], symbolTable: [], errors: [] };
  }
};
