package com.intelliswarm.aiagentcollaboration.agent;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TaskSolvingAgent extends AbstractAgent {
    private static final String TASK_COMPLETED = "TASK_COMPLETED";
    private static final String TASK_FAILED = "TASK_FAILED";
    private static final String REQUEST_HELP = "REQUEST_HELP";

    public TaskSolvingAgent(String id, String name, SimpMessagingTemplate messagingTemplate) {
        super(id, name, messagingTemplate);
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
        
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println(name + " is helping with task: " + task);
                Thread.sleep(1000); // Simulate task processing
                sendMessage(targetAgentId, TASK_COMPLETED + ": " + task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                sendMessage(targetAgentId, TASK_FAILED + ": " + task);
            }
        });
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