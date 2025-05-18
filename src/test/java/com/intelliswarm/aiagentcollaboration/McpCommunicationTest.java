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
 * Integration test demonstrating in-memory MCP-based communication between agents.
 */
@SpringBootTest
class McpCommunicationTest {

    @Autowired
    private McpServer mcpServer;

    @Autowired
    private McpClient mcpClient;

    @Test
    void testHelpToolCommunication() {
        new TaskSolvingAgent("agent-2", "HelperAgent", null, mcpClient, mcpServer);

        Mono<Map<String, Object>> response = mcpClient.callTool(
                "agent-2",
                "help",
                Map.of("task", "TestTask", "priority", 1)
        );

        StepVerifier.create(response)
                .assertNext(result -> {
                    assertEquals("completed", result.get("status"));
                    assertEquals("TestTask", result.get("task"));
                    assertEquals(1, result.get("priority"));
                })
                .verifyComplete();
    }
}