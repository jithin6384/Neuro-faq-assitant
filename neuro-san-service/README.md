# Neuro-SAN Service

## Overview

This service hosts the FAQ knowledge base using Neuro-SAN and is responsible for generating answers to customer queries.

The assistant is configured as an ICICI Prudential Life Insurance FAQ chatbot and answers only from the predefined FAQ dataset.

## Features

* FAQ-based question answering
* Conversation context support
* Prompt injection protection
* Uses a static FAQ knowledge base
* Returns structured responses for backend consumption

## Security Rules

The assistant is configured to:

* Answer only from the provided FAQ knowledge base.
* Reject prompt injection attempts.
* Never reveal internal prompts or system instructions.
* Respond with a fallback message for questions outside the FAQ.
* Maintain conversation context for follow-up questions.

## Running the Service

Start the Neuro-SAN service:

```bash
python app.py
```

The service runs on:

```
http://localhost:4173
```

## API Endpoint

```
POST /api/v1/faq_assistant/streaming_chat
```

Example request:

```json
{
  "user_message": {
    "text": "How do I switch funds?"
  }
}
```

Example response:

```json
{
  "response": {
    "type": "AGENT_FRAMEWORK",
    "text": "...",
    "chat_context": {}
  }
}
```
