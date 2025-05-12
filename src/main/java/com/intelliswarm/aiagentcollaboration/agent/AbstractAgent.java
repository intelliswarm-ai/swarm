package com.intelliswarm.aiagentcollaboration.agent;

import lombok.Getter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class AbstractAgent implements Agent {
    protected final String id;
    protected final String name;
    protected final SimpMessagingTemplate messagingTemplate;
    protected final Map<String, Object> state;

    protected AbstractAgent(String id, String name, SimpMessagingTemplate messagingTemplate) {
        this.id = id;
        this.name = name;
        this.messagingTemplate = messagingTemplate;
        this.state = new ConcurrentHashMap<>();
    }

    @Override
    public void receiveMessage(String fromAgentId, String message) {
        System.out.println(name + " received message from " + fromAgentId + ": " + message);
        processMessage(message);
    }

    @Override
    public void sendMessage(String toAgentId, String message) {
        String destination = "/topic/agent/" + toAgentId;
        messagingTemplate.convertAndSend(destination, message);
        System.out.println(name + " sent message to " + toAgentId + ": " + message);
    }

    @Override
    public Map<String, Object> getState() {
        return new ConcurrentHashMap<>(state);
    }

    @Override
    public void updateState(Map<String, Object> newState) {
        state.putAll(newState);
    }
} 