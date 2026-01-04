### MCP Server


Spring AI를 활용해 구현한 MCP(Model Context Protocol) Server로
LLM(Client/IDE/Agent)이 Git 등 외부 기능을 표준 MCP 규격으로 호출할 수 있도록 제공합니다.
---

### MCP Server Endpoint
- SSE Endpoint: http://localhost:8080/sse
- Message Endpoint: http://localhost:8080/mcp/message
---
### MCP 통신 방식
- 요청: HTTP POST (/mcp/message)
- 응답: SSE 스트리밍 (/sse)
- keep-alive 전송으로 장시간 연결 유지
---
### MCP Client Setting
- mcp_config.json
```json
{
  "mcpServers": {
    "my-mcp": {
      "command": "npx", 
      "args": [
        "-y", 
        "mcp-remote", 
        "http://localhost:8080/sse"
      ]
    }
  }
}
```