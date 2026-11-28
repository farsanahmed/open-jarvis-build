# Missing Features

Everything below is absent or incomplete in our tree versus
`tokenarc/open-jarvis@0487399`. Ordered by user-visible impact.

## Missing entirely

1. **App Builder Mode** — upstream `builder/` package. No project generation, no file
   operations, no build invocation, no UI entry.
2. **Memory management screen** — no way to inspect or delete Graphify nodes.
3. **MCP management screen** — no server list, no tool list, no invocation UI.
4. **Skills management screen** — no list, no enable/disable, no run.
5. **Automation management screen** — no list, no create, no run-now.
6. **Streaming responses** — README claims SSE; no streaming code path exists in any
   provider.
7. **Edge writing** — `EdgeDao` exists but nothing inserts edges, so the graph has no
   relationships.

## Incomplete

8. **ProviderFallbackChain** — no success-based ordering, no health memory beyond a
   fixed 60 s cooldown.
9. **SelfHealingExecutor** — does not re-read the screen between attempts.
10. **RiskyActionConfirmation** — classification exists, but the confirmation dialog is
    not wired into the overlay or the agent loop.
11. **Multi-phase prompt engine** — phases are data classes; no persisted state, no
    dependency resolution, no approval checkpoints.
12. **Screen Watch** — no start/pause/stop controls, no suggestions surface.
13. **Skills execution** — JSON loads and validates; `AgentCore` never dispatches to a
    skill.
14. **MCP** — tools only; no resources, prompts, notifications or session handling.
15. **Termux bridge** — no shared-token authentication, so any local process can post
    commands.
16. **Notification intelligence** — captures and filters, but never reaches `AgentCore`.
17. **AI app chaining** — open/type/read only; no response verification.
18. **Clipboard intelligence** — classifies content; no actions, no settings, no
    AgentCore hook.
19. **Tutorial mode** — no entry point in the UI.
20. **Prompt library** — data only, no screen.
21. **Local inference** — needs an external llama.cpp/Ollama server; nothing bundled.
22. **Voice conversation continuity** — single-turn reference resolution only; VAD is a
    silence timeout.

## Environment

23. **Local build on the phone** — Gradle's `libnative-platform.so` and `libjansi.so`
    do not load under Android bionic. All compilation happens in GitHub Actions, which
    does not satisfy the local-build requirement.
