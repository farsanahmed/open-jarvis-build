# Progress

## Last green commit

`c7a846c` — CI run <https://github.com/farsanahmed/open-jarvis-build/actions/runs/46005276334> (branch `baseline`, success, 3m42s).

Current `main` = `c7a846c` + the commits below. Compile + manifest-vs-dex gate + JVM unit tests are green as of `d683265` (run <https://github.com/farsanahmed/open-jarvis-build/actions/runs/46110551095>, job "Compile, gate and unit test" ✓ 3m14s). The emulator job in that run failed on "Timeout waiting for emulator to boot", not on the app.

## Phases done

| Phase | Commit | CI | Notes |
|---|---|---|---|
| base | `601b671` | NOT VERIFIED | compile/gate/unit green at `d683265`; emulator boot fix in flight |

## Commits on top of the baseline

| Commit | Change |
|---|---|
| `eaf864a` | docs: build rules and progress log |
| `2aef334` | UniversalAdapter keystore init made non-fatal |
| `960da3c` | manifest launcher activity `.ui.MainActivity` |
| `795aeee` | crash reporter + CrashReportActivity |
| `e233f58` | MainActivity resilient startup |
| `b6574f0` | CI gates: compile, manifest-vs-dex, unit tests, API 28/34 smoke, all-screens, release |
| `eae045f` | drop SocketServerTest until bridge is restored |
| `b1d0ed4` | AnalysisEngineTest under Robolectric, legal backtick name |
| `c172211` | `runTest` needs a TestDispatcher |
| `22b2508` | real fuzzy contact lookup (edit distance 2) |
| `0edda75` | restore agent/overlay/voice/mcp/skills/automation/watch/bridge/onboarding/dashboard/tutorial + command box and Stop |
| `ab20017` | keep only modules that compile; defer the Compose-heavy ones |
| `05926fb`, `9acf135`, `d683265` | PromptEngine/AutomationManager compile fixes |
| `601b671` | emulator boot options |

## Known problems

- Emulator images: `target: default` + `api-level: 28` never booted on the hosted runner. Changed to `google_apis` with `-no-snapshot -gpu swiftshader_indirect -memory 2048` and a 900 s boot timeout. NOT VERIFIED yet.
- Deferred modules, removed rather than stubbed (no dead buttons ship). Exact first errors when restored against Compose BOM 2023.10.01 / ML Kit 16.0.0:
  - `ui/dashboard/DashboardScreen.kt`: `withStyle`, `scale`, `alpha` parameter, `Divider`.
  - `ui/overlay/FloatingOverlayWidget.kt`: `detectTransformableState`, `pointerInput`, `infiniteTransition`, `automirrored`, `KeyboardArrowUp`, `AnimatedContent` `ContentTransform`.
  - `ui/onboarding/OnboardingActivity.kt`: `animateFloat`, `updateTransition`, `collectAsStateWithLifecycle`, `delay` outside a coroutine.
  - `ui/tutorial/TutorialMode.kt`: `HighContrast`, `description`, `var` reassignment.
  - `vision/ScreenshotCapture.kt`: `ImageReader` `width`/`height`/`allocation`.
  - `vision/VisionModule.kt`: text `confidence`.
  - `voice/AndroidSTTEngine.kt`: `EXTRA_SPEECH_INPUT_MINIMUM_CONFIDENCE_THRESHOLD`; `voice/VoiceManager.kt`: recognizer `cancel`.
  - `watch/ScreenWatcher.kt`: service-based `ScreenReader`, `var` reassignment.
  - `skills/SkillEngine.kt`: `File(context.assets, …)` and `listFiles()` overloads.
  Each needs a real API downgrade or new code, not a stub.
- `origin/main` history was rewritten once (`74c5434` → `eaf864a`, force push) to drop a workflow that skipped Kotlin compilation. The old commits stay reachable as `wip-restore-075aac3` and tag `v1.0.18`.
- Local Android build is impossible in this environment (AndroidHarness Linux shell, bionic libc, Gradle native libraries do not load). All builds run on GitHub Actions.
- `cc04cf1` (an earlier MainActivity repair) exists only in the separate upstream clone `/storage/emulated/0/Open Jarvis` and was never pushed. `d6c39b4` does not exist in either repo.
- `app/build.gradle` is byte-identical between `c7a846c` and the red `74c5434`; the unresolved `Column`/`padding`/`dp`/`Text` came from `MainActivity.kt` dropping its Compose imports, not from Gradle.
