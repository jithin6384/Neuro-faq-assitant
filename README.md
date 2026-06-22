# Neuro FAQ Assistant
## 12. Docker Images

The application has been containerized into three independent Docker images.

### 12.1 Spring Boot Backend

```
https://hub.docker.com/r/6483jithin/neuro-faq-backend
```

### 12.2 React Frontend

```
https://hub.docker.com/r/6483jithin/neuro-faq-frontend
```

### 12.3 Neuro-SAN Service

```
https://hub.docker.com/r/6483jithin/neuro-san-service
```

## 1. Overview

Neuro FAQ Assistant is a full-stack FAQ chatbot built using React, Spring Boot, MySQL and Neuro-SAN.

The application allows users to ask questions related to ICICI Prudential Life Insurance financial transactions. Responses are generated strictly from the official FAQ knowledge base configured within Neuro-SAN.

The project consists of three independent services:

1. React Frontend

   * Provides the chatbot user interface.

2. Spring Boot Backend

   * Manages chat sessions.
   * Stores conversation history.
   * Communicates with the Neuro-SAN service.

3. Neuro-SAN Service

   * Processes user queries.
   * Answers questions using the ICICI Prudential FAQ knowledge base.
   * Maintains conversational context.

---

## 2. Current Implementation

The current implementation follows the workflow below.

1. The user submits a question from the React application.

2. The Spring Boot backend receives the request.

3. The backend stores the user message in MySQL.

4. The backend forwards the question and conversation context to the Neuro-SAN service.

5. Neuro-SAN searches the configured ICICI Prudential FAQ knowledge base.

6. Neuro-SAN returns the answer together with the updated conversation context.

7. The backend stores the chatbot response and updated conversation context.

8. The chatbot response is returned to the frontend.

---

## 3. Planned Enhancement

The next enhancement planned for this project is semantic caching using Redis Vector Search.

Instead of forwarding every request to Neuro-SAN, the application will first search a semantic cache.

The proposed workflow is:

1. Generate an embedding for the incoming user question.

2. Search Redis Vector Search for semantically similar questions.

3. Compare the similarity score with a predefined threshold.

4. If a similar question exists, return the cached answer directly.

5. Otherwise, forward the request to Neuro-SAN.

6. Store the newly generated answer together with its embedding in Redis for future requests.

Expected benefits:

1. Lower response latency.

2. Reduced load on the Neuro-SAN service.

3. Faster responses for repeated or semantically similar questions.

4. Improved scalability.

---

## 4. System Architecture

```text
React Frontend (Port 3000)
        |
        | REST API
        |
Spring Boot Backend (Port 8080)
        |
        | HTTP
        |
Neuro-SAN Service (Port 4173)
        |
        |
ICICI Prudential FAQ Knowledge Base
```

---

## 5. Technology Stack

### 5.1 Frontend

* React
* Axios
* CSS

### 5.2 Backend

* Java 21
* Spring Boot
* Spring Data JPA
* Hibernate
* MySQL
* Lombok

### 5.3 AI Service

* Neuro-SAN
* Static FAQ Knowledge Base

---

## 6. Project Structure

```text
Neuro-faq-assistant

├── neurofaq-userInterface
├── neuro-faq-backend
├── neuro-san-service
└── README.md
```

---

## 7. Module Description

### 7.1 React Frontend

Responsibilities:

1. Create chat sessions.

2. Send user questions.

3. Display chatbot responses.

4. Load previous conversations.

Runs on:

```text
http://localhost:3000
```

### 7.2 Spring Boot Backend

Responsibilities:

1. Create chat sessions.

2. Store user messages.

3. Store chatbot responses.

4. Maintain conversation history.

5. Maintain Neuro-SAN chat context.

6. Communicate with Neuro-SAN.

Runs on:

```text
http://localhost:8080
```

### 7.3 Neuro-SAN Service

Responsibilities:

1. Answer questions using the ICICI Prudential FAQ knowledge base.

2. Maintain conversation context.

3. Reject prompt injection attempts.

4. Return structured responses to the backend.

Runs on:

```text
http://localhost:4173
```

---

## 8. Database Schema

### 8.1 ChatSession

* id
* title
* questionCount
* maxQuestions
* createdAt
* updatedAt
* lastChatContext

### 8.2 ChatMessage

* id
* sessionId
* role
* content
* createdAt

---

## 9. Request Flow

1. User submits a question.

2. React sends a POST request to Spring Boot.

3. Spring Boot stores the user message.

4. Spring Boot forwards the request to Neuro-SAN.

5. Neuro-SAN searches the FAQ knowledge base.

6. Neuro-SAN returns the answer and updated conversation context.

7. Spring Boot stores the chatbot response.

8. Spring Boot returns the response to the React application.

---

## 10. Running the Project

### 10.1 Start Neuro-SAN

```bash
cd neuro-san-service
```

Start the Neuro-SAN service.

Runs on:

```text
http://localhost:4173
```

### 10.2 Start Spring Boot Backend

```bash
cd neuro-faq-backend
./mvnw spring-boot:run
```

Runs on:

```text
http://localhost:8080
```

### 10.3 Start React Frontend

```bash
cd neurofaq-userInterface
npm install
npm start
```

Runs on:

```text
http://localhost:5173
```

---

## 11. Features

1. Chat session management.

2. Persistent conversation history.

3. Automatic chat title generation.

4. Context-aware conversations.

5. FAQ-based responses.

6. Prompt injection protection.

7. MySQL data persistence.

8. Neuro-SAN integration.

---

## 12. Future Enhancements

1. Session-based rate limiting.

2. User authentication.

3. Streaming chatbot responses.

4. Semantic caching using Redis Vector Search.

5. Docker deployment.

6. AWS deployment.
