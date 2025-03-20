import { useState } from 'react'
import './App.css'
import AnalizadorLexico from "./components/AnalizadorLexico";
function App() {
const [count, setCount] = useState(0)

  return (
    <>
     <div className="App">
      <AnalizadorLexico />
    </div>
     
    </>
  )
}

export default App
