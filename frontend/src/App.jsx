import { useEffect } from "react";

function App() {
  useEffect(() => {
    fetch("http://localhost:8080")
      .then(res => res.text())
      .then(data => console.log(data));
  }, []);

  return <h1>Frontend React funcionando 🚀</h1>;
}

export default App;