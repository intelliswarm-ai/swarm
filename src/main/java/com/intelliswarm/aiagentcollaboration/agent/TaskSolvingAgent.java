package com.intelliswarm.aiagentcollaboration.agent;

import org.springframework.experimental.mcp.McpClient;
import org.springframework.experimental.mcp.McpServer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import reactor.core.publisher.Mono;

public class TaskSolvingAgent extends AbstractAgent {
    private static final String TASK_COMPLETED = "TASK_COMPLETED";
    private static final String TASK_FAILED = "TASK_FAILED";
    private static final String REQUEST_HELP = "REQUEST_HELP";

    private final McpClient mcpClient;
    private final McpServer mcpServer;

    public TaskSolvingAgent(String id, String name, SimpMessagingTemplate messagingTemplate,
                           McpClient mcpClient, McpServer mcpServer) {
        super(id, name, messagingTemplate);
        this.mcpClient = mcpClient;
        this.mcpServer = mcpServer;
        setupMcpHandlers();
    }

    private void setupMcpHandlers() {
        mcpServer.registerTool("help", Map.of(
            "task", "string",
            "priority", "number"
        ), arguments -> {
            String task = (String) arguments.get("task");
            Integer priority = (Integer) arguments.get("priority");
            
            return Mono.fromCallable(() -> {
                System.out.println(name + " is helping with task: " + task + " (priority: " + priority + ")");
                Thread.sleep(1000); // Simulate task processing
                return Map.of(
                    "status", "completed",
                    "task", task,
                    "priority", priority
                );
            });
        });
    }

    @Override
    public void processMessage(String message) {
        if (message.startsWith(REQUEST_HELP)) {
            handleHelpRequest(message);
        } else if (message.startsWith(TASK_COMPLETED)) {
            handleTaskCompletion(message);
        } else if (message.startsWith(TASK_FAILED)) {
            handleTaskFailure(message);
        } else {
            System.out.println(name + " received unknown message type: " + message);
        }
    }

    private void handleHelpRequest(String message) {
        String task = message.substring(REQUEST_HELP.length()).trim();
        String targetAgentId = message.split(":")[1];
        
        mcpClient.callTool(targetAgentId, "help", Map.of(
            "task", task,
            "priority", 1
        )).subscribe(
            result -> {
                Map<String, Object> response = (Map<String, Object>) result;
                if ("completed".equals(response.get("status"))) {
                    sendMessage(targetAgentId, TASK_COMPLETED + ": " + task);
                } else {
                    sendMessage(targetAgentId, TASK_FAILED + ": " + task);
                }
            },
            error -> {
                System.err.println("Error processing help request: " + error.getMessage());
                sendMessage(targetAgentId, TASK_FAILED + ": " + task);
            }
        );
    }

    private void handleTaskCompletion(String message) {
        String task = message.substring(TASK_COMPLETED.length()).trim();
        System.out.println(name + " received confirmation of task completion: " + task);
        updateState(Map.of("lastCompletedTask", task));
    }

    private void handleTaskFailure(String message) {
        String task = message.substring(TASK_FAILED.length()).trim();
        System.out.println(name + " received notification of task failure: " + task);
        updateState(Map.of("lastFailedTask", task));
    }
} 