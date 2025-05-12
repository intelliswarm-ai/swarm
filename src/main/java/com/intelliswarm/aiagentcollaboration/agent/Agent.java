package com.intelliswarm.aiagentcollaboration.agent;

import java.util.Map;

public interface Agent {
    String getId();
    String getName();
    void receiveMessage(String fromAgentId, String message);
    void sendMessage(String toAgentId, String message);
    void processMessage(String message);
    Map<String, Object> getState();
    void updateState(Map<String, Object> newState);
} 