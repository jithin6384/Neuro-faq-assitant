import { useState, useEffect } from "react";
import { sendMessage, loadFullHistory } from "./services/chatApi";
import "./App.css";

const initialMessages = 
  {
    role: "bot",
    text: "Hi, I am your FAQ assistant. Ask me anything about customer support or financial transactions.",
  };


function App() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [currentSessionId, setCurrentSessionId] = useState('');
  
  useEffect( () => {
    async function fetchHistory(){
        const savedSessionId = localStorage.getItem("currentSessionId") || '';
        const response =  await loadFullHistory(savedSessionId || '')
        const data =  response.data;
        setCurrentSessionId(data.sessionId);
        localStorage.setItem("currentSessionId", data.sessionId);
        const formatted = data.messages.map(m => ({
            role: m.role.toLowerCase(),
            text: m.content
          }));
   
        setMessages(formatted);

    }

    fetchHistory();
   
  },[])
  async function handleSend() {
    if (!input.trim() || !currentSessionId || loading) return;
    const payload = {
        role: "user",
        text: input,
        sessionId: `${currentSessionId}`,
        userId: 1,
        timestamp: new Date().toISOString()
    };

    setMessages((prev) => [...prev,  {
    role: "user",
    text: input
  }]);

  setInput("");
  setLoading(true);
  // await new Promise((resolve) => setTimeout(resolve, 1500));

    try {

        // Save message and fetch message 
        
        const response = await sendMessage(payload);
        console.log(response.data);
        const botMessage = {
          role: response.data.role.toLowerCase(),
          text: response.data.content,
        };

        setMessages((prev) => [...prev, botMessage]);
        // Fetch all messages


        
     
        // Just pick the last one for now
        // const lastMessage = response.data[response.data.length - 1];

        // console.log(lastMessage);
     

    } catch (error) {
        console.error(error);
    }finally {
    setLoading(false);
  }

}

  function handleKeyDown(event) {
    if (event.key === "Enter") {
      handleSend();
    }
  }

  return (
    <div className="app">
      <aside className="sidebar">
        <h2>Neuro FAQ</h2>
        <button className="new-chat">+ New Chat</button>

        <div className="sidebar-footer">
          <p>FAQ Assistant</p>
        </div>
      </aside>

      <main className="chat-container">
        <header className="chat-header">
          <h1>Customer Support Assistant</h1>
          <p>Ask questions from the FAQ dataset</p>
        </header>

        <section className="messages">
         {!messages.length && <div className="message-row bot-row">
            <div className="message ">
              {initialMessages.text}
            </div>
         </div>}
           
          {messages.map((msg, index) => (
              <div
                key={index}
                className={`message-row ${msg.role === "user" ? "user-row" : "bot-row"}`}
              >
                <div className={`message ${msg.role}`}>
                  {msg.text}
                </div>
              </div>
          ))}

          {loading && (
            <div className="message-row bot-row">
              <div className="message bot">Thinking...</div>
            </div>
          )}
        </section>

        <footer className="input-area">
          <input
            type="text"
            placeholder="Ask a question..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
          />

          <button onClick={handleSend} disabled={loading}>
            Send
          </button>
        </footer>
      </main>
    </div>
  );
}

export default App;