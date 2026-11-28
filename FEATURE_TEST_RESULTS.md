# Feature Test Results

All results below come from CI runs, not from a device.

| Test | Suite | Result |
|---|---|---|
| `builtinSkillsLoadFromAssets` | `SkillExecutionTest` (Robolectric) | PASS (JVM) — run 46141819479, 7 skills loaded |
| `triggerPhraseMatchesAndVariablesAreExtracted` | `SkillExecutionTest` | PASS (JVM) — run 46141819479 |
| `unmatchedCommandReturnsNull` | `SkillExecutionTest` | PASS (JVM) — run 46141819479 |
| `mainActivityStarts` | `SmokeRobolectricTest` | PARTIAL — Robolectric `Resources$NotFoundException` on a framework string id |
| `settingsActivityStarts` | `SmokeRobolectricTest` | PASS (JVM) |
| `onboardingActivityStarts` | `SmokeRobolectricTest` | PASS (JVM) |
| `crashReportActivityStarts` | `SmokeRobolectricTest` | PASS (JVM) |
| `accessibilityServiceCanBeCreated` | `SmokeRobolectricTest` | PASS (JVM) |
| `overlayServiceCanBeCreated` | `SmokeRobolectricTest` | PARTIAL — Robolectric has no AndroidKeyStore |
| `notificationListenerCanBeCreated` | `SmokeRobolectricTest` | PASS (JVM) |
| `AnalysisEngineTest` (3 cases) | Robolectric + Room | PASS (JVM) |
| `UnitSanityTest` (2 cases) | plain JUnit | PASS (JVM) |
| manifest-vs-dex gate | `scripts/gate.sh` | PASS in CI |
| API 28 / API 34 emulator smoke | Emulator jobs | FAIL — `/dev/kvm` is present and world-writable after the udev rule and the log shows `Starting emulator.`, but adb never registers the device. NOT VERIFIED |
