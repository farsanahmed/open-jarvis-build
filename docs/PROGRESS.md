# Progress

## Last green commit

`c57f92f` — run <https://github.com/farsanahmed/open-jarvis-build/actions/runs/46121028618>: **Compile, gate and unit test ✓ 3m51s**. Emulator jobs (API 28, API 34) still running at the time of writing.

Baseline for this rebuild: `c7a846c` (run <https://github.com/farsanahmed/open-jarvis-build/actions/runs/46005276334>, success).

## Phases done

| Phase | Commit | CI | Notes |
|---|---|---|---|
| base | `c57f92f` | compile/gate/unit ✓, emulator NOT VERIFIED | all modules restored, command box + Stop, crash reporter, APK delivered |

## Delivered artifact

Release `v1.0.68` → `/storage/emulated/0/Download/open-jarvis-base.apk`
(63 209 163 bytes, sha256 `3aa03fd8c42cdd8e7e378001f24ed512d6e32916c7f56752508210e171cfed7a`, matches the release asset digest).

## Module status (all restored, none stubbed)

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

Verified in the delivered APK's dex (27/27 classes present): MainActivity, SettingsActivity, CrashReportActivity, OnboardingActivity, JarvisAccessibilityService, OverlayService, DownloadService, JarvisNotificationListener, AppIntelligenceReceiver, AgentCore, PromptEngine, ProviderFallbackChain, SelfHealingExecutor, FloatingOverlayWidget, VoiceManager, AndroidSTTEngine, AndroidTTSEngine, MCPClient, SkillEngine, AutomationManager, AutomationWorker, ScreenWatcher, SocketServer, DashboardScreen, TutorialMode, VisionModule, CrashReporter.

## Manifest

Declares `.ui.MainActivity` (launcher), `.ui.SettingsActivity`, `.ui.CrashReportActivity`, `.ui.onboarding.OnboardingActivity`, `.accessibility.JarvisAccessibilityService`, `.ui.OverlayService` (`specialUse`), `.local.DownloadService` (`dataSync`), `.intelligence.JarvisNotificationListener`, `.intelligence.AppIntelligenceReceiver` (boot + package added). Permissions: INTERNET, ACCESS_NETWORK_STATE, RECORD_AUDIO, WAKE_LOCK, SYSTEM_ALERT_WINDOW, FOREGROUND_SERVICE, FOREGROUND_SERVICE_SPECIAL_USE, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED.

## Known problems

- This runner has **no KVM** ("hardware acceleration is not available"). The emulator jobs now run with `disable-linux-hw-accel: true` and `emulator-boot-timeout: 1800`. Earlier attempts failed because the input is `emulator-boot-timeout`, not `boot-timeout`.
- The release job needs only a successful build and prints the emulator result in the notes, so the APK is always downloadable and never labelled verified without green emulator jobs.
- `origin/main` history was rewritten once (`74c5434` → `eaf864a`, force push) to drop a workflow that skipped Kotlin compilation. Old commits stay reachable as `wip-restore-075aac3` and tag `v1.0.18` (now marked pre-release: "BROKEN: crashes on launch. Do not install.").
- Local Android build is impossible here (AndroidHarness Linux shell, bionic libc, Gradle native libraries do not load). All builds run on GitHub Actions.
- `cc04cf1` exists only in the separate upstream clone `/storage/emulated/0/Open Jarvis`; never pushed. `d6c39b4` does not exist in either repo.
- Compose BOM is 2024.09.00 because the restored UI needs Compose 1.7 APIs (`withStyle`, `scale`, `Modifier.alpha`, `HorizontalDivider`, `ContentTransform`).
