# Feature Verification Matrix

Evidence available today. "Compiled" means `assembleDebug` succeeded and the class is in
`classes*.dex`. "JVM" means a Robolectric or plain JUnit test ran. Nothing is
device-verified: no one has installed a build and reported back.

| Feature | Implementation | In APK | Test performed | Result |
|---|---|---|---|---|
| MainActivity opens | `ui/MainActivity.kt` | yes | Robolectric `mainActivityStarts` | PARTIAL — Robolectric throws `Resources$NotFoundException` on a framework string id; logged as a known JVM gap |
| SettingsActivity opens | `ui/SettingsActivity.kt` | yes | Robolectric `settingsActivityStarts` | PASS (JVM) |
| OnboardingActivity opens | `ui/onboarding/OnboardingActivity.kt` | yes | Robolectric `onboardingActivityStarts` | PASS (JVM) |
| CrashReportActivity opens | `ui/CrashReportActivity.kt` | yes | Robolectric `crashReportActivityStarts` | PASS (JVM) |
| Accessibility service constructs | `accessibility/JarvisAccessibilityService.kt` | yes | Robolectric `accessibilityServiceCanBeCreated` | PASS (JVM) |
| Overlay service constructs | `ui/OverlayService.kt` | yes | Robolectric `overlayServiceCanBeCreated` | PARTIAL — no AndroidKeyStore under Robolectric |
| Notification listener constructs | `intelligence/JarvisNotificationListener.kt` | yes | Robolectric `notificationListenerCanBeCreated` | PASS (JVM) |
| Manifest components exist in dex | `scripts/gate.sh` | yes | CI gate on every build | PASS |
| Fuzzy contact lookup | `graphify/GraphifyRepository.kt` | yes | `AnalysisEngineTest` (Robolectric, Room) | PASS (JVM) |
| Pattern confidence decay | `graphify/AnalysisEngine.kt` | yes | `AnalysisEngineTest` | PASS (JVM) |
| Provider defaults | `llm/UniversalAdapter.kt` | yes | `UnitSanityTest` | PASS (JVM) |
| Launcher activity resolves | manifest | yes | `aapt2 dump badging` | PASS — `launchable-activity: com.openjarvis.ui.MainActivity` |
| APK signature | CI keystore | yes | `apksigner verify --print-certs` | PASS — V2 signer `e4e54b45…` |
| Tap / type / scroll on a real app | `accessibility/` | yes | none | NOT TESTED |
| Voice STT/TTS | `voice/` | yes | none | NOT TESTED |
| OCR / MediaProjection | `vision/` | yes | none | NOT TESTED |
| Floating overlay over another app | `ui/overlay/` | yes | none | NOT TESTED |
| MCP tool discovery/invocation | `mcp/` | yes | none | NOT TESTED |
| Skills execution | `skills/` | yes | none | NOT TESTED |
| Scheduler fires | `automation/` | yes | none | NOT TESTED |
| Termux bridge round-trip | `bridge/` | yes | none | NOT TESTED |
| Streaming responses | — | no | — | MISSING |
| App Builder Mode | upstream `builder/` | no | — | MISSING |
| Memory deletion UI | — | no | — | MISSING |
| MCP / Skills / Automation screens | — | no | — | MISSING |
