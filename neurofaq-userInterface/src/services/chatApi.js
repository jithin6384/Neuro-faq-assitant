import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
});

export async function sendMessage(payload) {
    return await api.post("/api/chat", payload);
}

// export async function getMessages(currentSessionId) {
   
//     return await api.get(`/api/chat/current/${currentSessionId}`); 
// }

export async function loadFullHistory(currentSessionId){
     const response = await api.get(`/api/chat/history?sessionId=${currentSessionId || ''}`);
     return response;
}