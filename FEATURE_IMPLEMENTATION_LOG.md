# Feature Implementation Log

| Commit | Feature | Change |
|---|---|---|
| `2aef334` | Provider key storage | `UniversalAdapter` falls back to plain `SharedPreferences` when `MasterKey`/AES256_GCM fails, so startup survives keystore errors |
| `22b2508` | Graphify contacts | `findContactByName` falls back to a Levenshtein search (distance ≤ 2) over the 200 most recent contacts |
| `795aeee` | Crash reporting | `CrashReporter` writes the uncaught trace to Downloads (fallback: files dir) and stores it; `CrashReportActivity` shows it with Share/Clear |
| `e233f58` | Startup resilience | `MainActivity` builds `GraphifyRepository`/`AgentCore` inside try-catch and renders an inline error card instead of crashing |
| `0edda75` | Command input | Home screen gets a command field with Run and Stop; `AgentCore.stopTask()` cancels the running job |
| `6f3839a` | UI/vision/voice/watch/skills | Dashboard, overlay, onboarding, tutorial, `ScreenshotCapture`, `VisionModule`, `VoiceManager`, `AndroidSTTEngine`, `ScreenWatcher`, `SkillEngine` restored and fixed against Compose 1.7 / ML Kit 16 |
| `ab20017`→`6f3839a` | Automation | `AutomationEntity` mappers added; `AutomationWorker` implemented (loads the row, runs `AgentCore`, records `lastRun`/`lastResult`/`runCount`) |
| `cec634c` | Signing | Stable keystore wired into `debug` and `release` |
| `c4df642` | Skills execution | `SkillEngine.ensureLoaded()`; `AgentCore` runs a matched skill's actions, records usage, logs the task; `SkillExecutionTest` added |
