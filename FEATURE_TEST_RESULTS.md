# Feature Test Results

All results below come from CI runs, not from a device.

| Test | Suite | Result |
|---|---|---|
| `builtinSkillsLoadFromAssets` | `SkillExecutionTest` (Robolectric) | NOT VERIFIED — added in `c4df642`, run in flight |
| `triggerPhraseMatchesAndVariablesAreExtracted` | `SkillExecutionTest` | NOT VERIFIED — as above |
| `unmatchedCommandReturnsNull` | `SkillExecutionTest` | NOT VERIFIED — as above |
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
| API 28 / API 34 emulator smoke | Emulator jobs | FAIL — emulator never registers on adb; NOT VERIFIED |
