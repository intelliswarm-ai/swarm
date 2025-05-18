# AI Agent Collaboration Demo

This is a demo application showcasing collaborating AI agents using Spring Framework. The demo implements a simple task-solving system where agents can communicate and help each other complete tasks.

## Features

- WebSocket-based agent communication
- REST API for agent management
- Real-time task collaboration between agents
- State management for each agent

## Prerequisites

- Java 17 or higher
- Maven

## Running the Application

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

### Create a new agent
```http
POST /api/agents?name=AgentName
```

### Get all agents
```http
GET /api/agents
```

### Send a message between agents
```http
POST /api/agents/{agentId}/message?toAgentId={targetAgentId}&message={message}
```

### Get agent state
```http
GET /api/agents/{agentId}/state
```

## Example Usage

1. Create two agents:
   ```bash
   curl -X POST "http://localhost:8080/api/agents?name=Agent1"
   curl -X POST "http://localhost:8080/api/agents?name=Agent2"
   ```

2. Send a help request from Agent1 to Agent2:
   ```bash
   curl -X POST "http://localhost:8080/api/agents/agent1/message?toAgentId=agent2&message=REQUEST_HELP: Solve this task"
   ```

3. Check Agent2's state:
   ```bash
   curl "http://localhost:8080/api/agents/agent2/state"
   ```

## WebSocket Communication

The application uses WebSocket for real-time communication between agents. Agents can send and receive messages through the following channels:

- `/topic/agent/{agentId}` - Channel for receiving messages
- `/app/message` - Endpoint for sending messages

## Spring AI MCP Communication

In addition to WebSocket messaging, this demo leverages Spring AI's MCP (Message Communication Protocol) to enable tool-based communication between agents. A global `McpServer` and `McpClient` are configured in `McpConfig`.

Agents register tools (for example, `help`) on the server and invoke other agents' tools via the client, receiving reactive responses as `Mono<Map<String, Object>>`.

```java
@Configuration
public class McpConfig {

    @Bean
    public McpServer mcpServer() {
        return McpServer.create();
    }

    @Bean
    public McpClient mcpClient(McpServer mcpServer) {
        return McpClient.create(mcpServer);
    }
}
```

For example, a `TaskSolvingAgent` sets up a `help` tool on startup. To request help:

```java
mcpClient.callTool(
        "agent-2",      // target agent ID
        "help",         // tool name
        Map.of("task", "Solve this task", "priority", 1)
)
        .subscribe(response -> /* handle result */);
```

## Architecture

The demo implements a simple agent system with the following components:

- `Agent` interface - Defines the core functionality of an agent
- `AbstractAgent` - Base implementation with common functionality
- `TaskSolvingAgent` - Concrete implementation for task solving
- `WebSocketConfig` - Configuration for WebSocket communication
- `AgentController` - REST API for agent management "# swarm" 
