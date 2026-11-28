# Complete Feature Inventory

Source of truth: `tokenarc/open-jarvis` at commit `0487399` ("M8-docs: Final README pass"),
read from the local clone `/storage/emulated/0/Open Jarvis`, plus `README.md`,
`docs/*.md` and the source tree. Our repo: `farsanahmed/open-jarvis-build`.

Status words: **PRESENT** (class + wiring exist and compile), **PARTIAL** (exists but a
documented behaviour is missing), **MISSING** (not in our tree), **UNVERIFIED** (needs a
device). Nothing here is claimed to work on hardware.

## 1. Accessibility and automation — `accessibility/`

| # | Feature | Upstream location | Present | In APK | Status |
|---|---|---|---|---|---|
| 1.1 | AccessibilityService registration + config | `accessibility/JarvisAccessibilityService.kt`, `res/xml/accessibility_service_config.xml` | yes | yes | PRESENT |
| 1.2 | Launch app by package / by label | `JarvisAccessibilityService.openAppByPackage/openAppByLabel` | yes | yes | PRESENT |
| 1.3 | Tap by text, tap by coords | `tapByText`, `tapByCoords` | yes | yes | PRESENT |
| 1.4 | Long press | `Action.LONG_PRESS` in `ActionExecutor` | yes | yes | PRESENT |
| 1.5 | Type / clear field | `typeText`, `clearText` | yes | yes | PRESENT |
| 1.6 | Scroll / swipe | `Action.SCROLL`, `Action.SWIPE` | yes | yes | PRESENT |
| 1.7 | Back / home / recents | `pressBack/pressHome/pressRecents` | yes | yes | PRESENT |
| 1.8 | Wait for element | `Action.WAIT_FOR` | yes | yes | PRESENT |
| 1.9 | Read screen text | `ScreenReader.extractAllText` | yes | yes | PRESENT |
| 1.10 | Action result + failure propagation | `ActionExecutor.ExecutionResult` | yes | yes | PRESENT |

## 2. Agent — `agent/`

| # | Feature | Upstream location | Present | In APK | Status |
|---|---|---|---|---|---|
| 2.1 | Intent analysis from natural language | `AgentCore.executeTask` + `PromptEngine` | yes | yes | PRESENT |
| 2.2 | Structured action plan (JSON) | `ActionPlan.kt`, `ActionJsonParser` | yes | yes | PRESENT |
| 2.3 | Multi-step execution + per-step state | `AgentCore.executeActions` | yes | yes | PRESENT |
| 2.4 | Memory injection into the prompt | `AgentCore` `{GRAPHIFY_CONTEXT}` | yes | yes | PRESENT |
| 2.5 | Stop / cancel | `AgentCore.stopTask()` (added here) | yes | yes | PRESENT |
| 2.6 | Error messages by HTTP status | `AgentCore` error mapping | yes | yes | PRESENT |
| 2.7 | Prompt sanitising | `PromptSanitizer.kt` | yes | yes | PRESENT |
| 2.8 | LLM response validation | `LLMResponseValidator.kt` | yes | yes | PRESENT |
| 2.9 | Provider fallback chain | `ProviderFallbackChain.kt` | yes | yes | PARTIAL — cooldown + failure logging exist, no success-based ordering |
| 2.10 | Self-healing retry | `SelfHealingExecutor.kt` | yes | yes | PARTIAL — 3 attempts + repair prompt, no screen re-read between attempts |
| 2.11 | Risky-action confirmation | `RiskyActionConfirmation.kt` | yes | yes | PARTIAL — classifies risk, confirmation UI not wired to the overlay |
| 2.12 | Multi-phase prompt engine | `PromptEngine.Phase`, upstream README M6.8 | yes | yes | PARTIAL — phases are data only, no persisted execution state |
| 2.13 | Task working memory | `TaskWorkingMemory.kt` | yes | yes | PRESENT |

## 3. Graphify memory — `graphify/`

| # | Feature | Present | In APK | Status |
|---|---|---|---|---|
| 3.1 | AppNode / TaskNode / ContactNode / PatternNode / ProviderNode / EdgeEntity | yes | yes | PRESENT |
| 3.2 | Room DB v3 + migration 2→3 | yes | yes | PRESENT |
| 3.3 | Task/app/contact history | yes | yes | PRESENT |
| 3.4 | Pattern detection + confidence decay | yes | yes | PRESENT |
| 3.5 | Fuzzy contact lookup (edit distance 2) | yes (added here) | yes | PRESENT |
| 3.6 | Edge storage between nodes | yes | yes | PARTIAL — DAO exists, nothing writes edges yet |
| 3.7 | Memory deletion UI | no | no | MISSING |

## 4. Providers — `llm/`

| # | Feature | Present | In APK | Status |
|---|---|---|---|---|
| 4.1 | Groq | yes | yes | PRESENT |
| 4.2 | Google Gemini | yes | yes | PRESENT |
| 4.3 | Anthropic Claude | yes | yes | PRESENT |
| 4.4 | OpenAI | yes | yes | PRESENT |
| 4.5 | OpenRouter | yes | yes | PRESENT |
| 4.6 | Ollama | yes | yes | PRESENT |
| 4.7 | LM Studio / custom OpenAI-compatible | yes (`CustomProvider`) | yes | PRESENT |
| 4.8 | Encrypted key storage + fallback | yes (added here) | yes | PRESENT |
| 4.9 | Connection test | yes | yes | PRESENT |
| 4.10 | Streaming | no | no | MISSING — upstream README claims it, no SSE code path |
| 4.11 | Model selection UI | `SettingsScreen` | yes | PRESENT |

## 5. Local models — `local/`

| # | Feature | Present | In APK | Status |
|---|---|---|---|---|
| 5.1 | Model tiers + download | `ModelManager`, `DownloadService` | yes | PRESENT |
| 5.2 | RAM guard before load | `ModelManager.canLoadModel` | yes | PRESENT |
| 5.3 | Local inference | `LlamaProvider` over `localhost:8080` | yes | PARTIAL — needs a separate llama.cpp server, none is bundled |
| 5.4 | DownloadService declared | manifest `dataSync` | yes | PRESENT |

## 6. Voice — `voice/`

| # | Feature | Present | In APK | Status |
|---|---|---|---|---|
| 6.1 | Android STT | `AndroidSTTEngine` | yes | PRESENT |
| 6.2 | Android TTS | `AndroidTTSEngine` | yes | PRESENT |
| 6.3 | Push to talk / VAD | `VoiceManager.startListeningWithVAD` | yes | PARTIAL — VAD is a silence timeout, not a real VAD |
| 6.4 | Spoken responses | `VoiceManager.speak` | yes | PRESENT |
| 6.5 | Conversation continuity | `ConversationContext` in `PromptEngine` | yes | PARTIAL — single-turn reference resolution only |

## 7. Vision — `vision/`

| # | Feature | Present | In APK | Status |
|---|---|---|---|---|
| 7.1 | MediaProjection consent + capture | `ScreenshotCapture` | yes | PRESENT |
| 7.2 | ML Kit OCR | `VisionModule` | yes | PRESENT |
| 7.3 | Screen understanding for the agent | `{SCREEN_OCR}` injection | yes | PRESENT |
| 7.4 | Screen Watch | `watch/ScreenWatcher` | yes | PARTIAL — rules + OCR, no start/pause/stop UI |

## 8. UI — `ui/`

| # | Feature | Present | In APK | Status |
|---|---|---|---|---|
| 8.1 | VOID dashboard | `ui/dashboard/DashboardScreen` | yes | PRESENT |
| 8.2 | Command box + Run + Stop | `MainActivity` (added here) | yes | PRESENT |
| 8.3 | Settings / providers | `SettingsActivity`, `SettingsScreen` | yes | PRESENT |
| 8.4 | Onboarding | `ui/onboarding/OnboardingActivity` | yes | PRESENT |
| 8.5 | Floating overlay | `ui/OverlayService`, `ui/overlay/FloatingOverlayWidget` | yes | PRESENT |
| 8.6 | Crash report screen | `ui/CrashReportActivity` (new here) | yes | PRESENT |
| 8.7 | Tutorial mode | `ui/tutorial/TutorialMode` | yes | PARTIAL — highlight view exists, no entry point in the UI |
| 8.8 | Prompt library | `ui/prompts/PromptLibrary` | yes | PARTIAL — data only, no screen |
| 8.9 | Memory management screen | no | no | MISSING |
| 8.10 | MCP management screen | no | no | MISSING |
| 8.11 | Skills management screen | no | no | MISSING |
| 8.12 | Automation management screen | no | no | MISSING |
| 8.13 | App Builder screen | upstream `builder/` | no | MISSING here |

## 9. Integrations

| # | Feature | Upstream location | Present | In APK | Status |
|---|---|---|---|---|---|
| 9.1 | MCP client + manager | `mcp/` | yes | yes | PARTIAL — JSON-RPC over HTTP, tools only, no resources/prompts |
| 9.2 | Skills engine + 7 JSON skills | `skills/`, `assets/skills/` | yes | yes | PARTIAL — loads and lists, execution path not wired to AgentCore |
| 9.3 | Automation scheduler | `automation/` | yes | yes | PARTIAL — WorkManager scheduling works, no UI |
| 9.4 | Termux bridge | `bridge/SocketServer.kt`, `scripts/jarvis.sh` | yes | yes | PARTIAL — server + CLI script, token auth not enforced |
| 9.5 | Notification intelligence | `intelligence/JarvisNotificationListener` | yes | yes | PARTIAL — captures + privacy filter, no AgentCore hook |
| 9.6 | AI app interactor / chaining | `intelligence/AIAppInteractor` | yes | yes | PARTIAL — open/type/read, no response verification |
| 9.7 | Clipboard intelligence | `intelligence/ClipboardIntelligence` | yes | yes | PARTIAL — classification only, no actions or settings |
| 9.8 | App intelligence | `intelligence/AppAnalyzer`, `AppIntelligence*` | yes | yes | PRESENT |
| 9.9 | App Builder Mode | upstream `builder/` | no | no | MISSING here |

## 10. Platform / project

| # | Feature | Present | In APK | Status |
|---|---|---|---|---|
| 10.1 | Package `com.openjarvis`, name "Open Jarvis" | yes | yes | PRESENT |
| 10.2 | minSdk 26, target 34 | yes | yes | PRESENT |
| 10.3 | Stable signing key | yes (CI secrets) | yes | PRESENT |
| 10.4 | Local build on the phone | no | — | MISSING — Gradle native libs do not load under bionic |
