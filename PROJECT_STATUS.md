# Project Status

Checkpoint after the signing, CI and skills-execution work.

## Done and proven by CI

- `main` rebuilt from the green baseline `c7a846c` without force-pushing after that
  single rewrite; every change is its own commit with a CI run.
- Compile + manifest-vs-dex gate + JVM/Robolectric tests are green.
- Stable RSA-4096 keystore generated once, kept outside the repo, stored only as
  GitHub Actions secrets; both build types use it.
- Releases are pre-releases named `UNVERIFIED (not tested on a device)`.
- Skills now actually run: `SkillEngine.ensureLoaded()` loads the 7 built-in JSON
  skills, `AgentCore` matches a trigger phrase, executes the skill's action template,
  records usage and logs the task. Covered by `SkillExecutionTest`.
- Crash reporter writes the trace to `/storage/emulated/0/Download/open-jarvis-crash.txt`
  (falling back to the app files dir) and `CrashReportActivity` shows it with Share.

## In flight / next

- Emulator smoke tests: blocked, see `BUILD_STATUS.md`. No more runs will be spent on
  them; the jobs stay in the workflow but do not gate the release.
- Next feature groups, in order: memory management (list + delete), Termux bridge
  authentication, MCP resources/prompts, clipboard actions, screen-watch controls,
  skills/MCP/automation screens, App Builder Mode, streaming.

## Honest limitations

- No build is verified. Only the person who installs the APK can confirm behaviour.
- The local-build requirement is unmet: compilation happens in GitHub Actions.
- `docs/STATUS.md` records the same statement for future sessions.
