package com.intelliswarm.aiagentcollaboration;

import com.intelliswarm.aiagentcollaboration.agent.TaskSolvingAgent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.experimental.mcp.McpClient;
import org.springframework.experimental.mcp.McpServer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test demonstrating two agents registering a 'help' tool and calling each other via MCP.
 */
@SpringBootTest
class McpAgentToAgentTest {

    @Autowired
    private McpServer mcpServer;

    @Autowired
    private McpClient mcpClient;

    @Test
    void testMutualHelpToolCommunication() {
        new TaskSolvingAgent("agentA", "AgentA", null, mcpClient, mcpServer);
        new TaskSolvingAgent("agentB", "AgentB", null, mcpClient, mcpServer);

        Mono<Map<String, Object>> responseFromB = mcpClient.callTool(
                "agentB", "help", Map.of("task", "TaskB", "priority", 2)
        );

        Mono<Map<String, Object>> responseFromA = mcpClient.callTool(
                "agentA", "help", Map.of("task", "TaskA", "priority", 3)
        );

        StepVerifier.create(responseFromB)
                .assertNext(result -> {
                    assertEquals("completed", result.get("status"));
                    assertEquals("TaskB", result.get("task"));
                    assertEquals(2, result.get("priority"));
                })
                .verifyComplete();

        StepVerifier.create(responseFromA)
                .assertNext(result -> {
                    assertEquals("completed", result.get("status"));
                    assertEquals("TaskA", result.get("task"));
                    assertEquals(3, result.get("priority"));
                })
                .verifyComplete();
    }
}