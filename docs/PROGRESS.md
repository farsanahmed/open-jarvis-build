# Progress

## Last green commit

`1541dbf` — run <https://github.com/farsanahmed/open-jarvis-build/actions/runs/46113859452>: **Compile, gate and unit test ✓ 3m22s**. Emulator jobs still running at the time of writing.

Baseline for this rebuild: `c7a846c` (run <https://github.com/farsanahmed/open-jarvis-build/actions/runs/46005276334>, success).

## Phases done

| Phase | Commit | CI | Notes |
|---|---|---|---|
| base | `1541dbf` | compile/gate/unit ✓, emulator NOT VERIFIED | all modules restored, command box + Stop, crash reporter |

## Module status

| Module | Status |
|---|---|
| `accessibility` (service, ActionExecutor, ScreenReader) | restored as original |
| `agent` (AgentCore + Stop, PromptEngine, ProviderFallbackChain, SelfHealingExecutor, RiskyActionConfirmation, LLMResponseValidator, PromptSanitizer) | restored with fixes |
| `graphify` (Room DB, nodes, DAOs, AnalysisEngine, fuzzy contact lookup) | restored with fixes |
| `intelligence` (AppAnalyzer, TaskRouter, AIAppInteractor, JarvisNotificationListener, ClipboardIntelligence, AppIntelligence*) | restored with fixes |
| `llm` (UniversalAdapter + 7 providers, keystore fallback) | restored with fixes |
| `local` (ModelManager, DownloadService) | restored as original, DownloadService declared |
| `mcp` (MCPClient, MCPManager) | restored with fixes |
| `skills` (SkillEngine + 7 JSON assets) | restored with fixes |
| `automation` (AutomationDB, AutomationManager, AutomationWorker) | restored with fixes, worker implemented |
| `bridge` (SocketServer, Termux CLI) | restored with fixes |
| `watch` (ScreenWatcher) | restored with fixes |
| `vision` (ScreenshotCapture, VisionModule) | restored with fixes |
| `voice` (VoiceManager, AndroidSTTEngine, AndroidTTSEngine) | restored with fixes |
| `ui` (MainActivity with command box + Stop, Settings, CrashReport, Onboarding, Dashboard, OverlayService, FloatingOverlayWidget, Tutorial, PromptLibrary, theme) | restored with fixes |
| `crash` (CrashReporter, CrashReportActivity) | new in this fork |

Nothing is stubbed and no dead buttons ship.

## Manifest

Declares `.ui.MainActivity` (launcher), `.ui.SettingsActivity`, `.ui.CrashReportActivity`, `.ui.onboarding.OnboardingActivity`, `.accessibility.JarvisAccessibilityService`, `.ui.OverlayService` (`specialUse`), `.local.DownloadService` (`dataSync`), `.intelligence.JarvisNotificationListener` (bind notification listener), `.intelligence.AppIntelligenceReceiver` (boot + package added). Permissions: INTERNET, ACCESS_NETWORK_STATE, RECORD_AUDIO, WAKE_LOCK, SYSTEM_ALERT_WINDOW, FOREGROUND_SERVICE, FOREGROUND_SERVICE_SPECIAL_USE, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED.

## Commits on top of the baseline

`eaf864a` docs → `2aef334` keystore fallback → `960da3c` manifest launcher → `795aeee`/`e233f58` crash reporter + resilient startup → `b6574f0` CI gates → `eae045f` drop stale test → `b1d0ed4` Robolectric test → `c172211` runTest dispatcher → `22b2508` fuzzy contacts → `0edda75` restore batch + command box → `ab20017` prune non-compiling → `05926fb`/`9acf135`/`d683265` PromptEngine/AutomationManager → `601b671` emulator options → `05cb510` docs → `6f3839a` restore UI/vision/voice/watch/skills → `57bd604`/`4c6fee4` fixes → `0e479b4`/`1541dbf` AnimatedContent/ContentTransform.

## Known problems

- Emulator images: `target: default` never booted; now `google_apis` with `-no-snapshot -gpu swiftshader_indirect -memory 2048` and a 900 s boot timeout. NOT VERIFIED.
- `origin/main` history was rewritten once (`74c5434` → `eaf864a`, force push) to drop a workflow that skipped Kotlin compilation. Old commits stay reachable as `wip-restore-075aac3` and tag `v1.0.18`.
- Local Android build is impossible here (AndroidHarness Linux shell, bionic libc, Gradle native libraries do not load). All builds run on GitHub Actions.
- `cc04cf1` exists only in the separate upstream clone `/storage/emulated/0/Open Jarvis`; never pushed. `d6c39b4` does not exist in either repo.
- `app/build.gradle` was byte-identical between `c7a846c` and the red `74c5434`; the unresolved `Column`/`padding`/`dp`/`Text` came from `MainActivity.kt` dropping its Compose imports. The BOM is now 2024.09.00 because the restored UI needs Compose 1.7 APIs.
