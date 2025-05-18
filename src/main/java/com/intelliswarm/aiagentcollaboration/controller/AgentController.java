package com.intelliswarm.aiagentcollaboration.controller;

import com.intelliswarm.aiagentcollaboration.agent.Agent;
import com.intelliswarm.aiagentcollaboration.agent.TaskSolvingAgent;
import org.springframework.experimental.mcp.McpClient;
import org.springframework.experimental.mcp.McpServer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/agents")
public class AgentController {
    private final Map<String, Agent> agents = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final McpClient mcpClient;
    private final McpServer mcpServer;

    public AgentController(SimpMessagingTemplate messagingTemplate,
                          McpClient mcpClient,
                          McpServer mcpServer) {
        this.messagingTemplate = messagingTemplate;
        this.mcpClient = mcpClient;
        this.mcpServer = mcpServer;
    }

    @PostMapping
    public Agent createAgent(@RequestParam String name) {
        String id = "agent-" + System.currentTimeMillis();
        Agent agent = new TaskSolvingAgent(id, name, messagingTemplate, mcpClient, mcpServer);
        agents.put(id, agent);
        return agent;
    }

    @GetMapping
    public Map<String, Agent> getAllAgents() {
        return agents;
    }

    @PostMapping("/{agentId}/message")
    public void sendMessage(
            @PathVariable String agentId,
            @RequestParam String toAgentId,
            @RequestParam String message) {
        Agent agent = agents.get(agentId);
        if (agent != null) {
            agent.sendMessage(toAgentId, message);
        }
    }

    @GetMapping("/{agentId}/state")
    public Map<String, Object> getAgentState(@PathVariable String agentId) {
        Agent agent = agents.get(agentId);
        return agent != null ? agent.getState() : Map.of();
    }
} 