package com.intelliswarm.aiagentcollaboration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.experimental.mcp.*;
import org.springframework.experimental.mcp.*;

/**
 * Configuration for Spring AI MCP (Message Communication Protocol) server and client.
 */
@Configuration
public class McpConfig {

    /**
     * In-memory MCP server for tool registration and dispatch.
     */
    @Bean
    public McpServer mcpServer() {
        return McpServer.create();
    }

    /**
     * MCP client to invoke tools on registered agents via the MCP server.
     */
    @Bean
    public McpClient mcpClient(McpServer mcpServer) {
        return McpClient.create(mcpServer);
    }

}