package com.project.agrostore.chatbot;

public interface ChatBotListener {
    void onError(String message);

    void onResponse(String reply);
}

