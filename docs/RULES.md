# Build Rules

- The GitHub repo is the source of truth. A change counts only if CI is green on that commit.
- Restore removed modules from git history (`git log --diff-filter=D --name-only`, `git show <sha>:<path>`) or upstream `tokenarc/open-jarvis`. Fix their compile errors. Write new code only where the original is missing. Never stub. Never leave a dead button.
- Declare every component, permission and foreground service type correctly in the manifest. Manifest class names must match real packages.
- CI must run on every push and pull request and `workflow_dispatch`, and must fail the build on:
  - compile;
  - the manifest-vs-dex gate (`aapt2 dump xmltree`);
  - JVM unit tests;
  - API 28 and API 34 emulator smoke tests (launch, wait 8s, process alive, enable accessibility, still alive, no `FATAL EXCEPTION` or `ClassNotFoundException` in logcat, upload logcat and a screenshot);
  - an instrumented test that opens every screen.
- Small commits. If CI goes red, fix that first. If one module's errors do not go down after 8 runs, record it in `docs/PROGRESS.md`, keep the smallest working version, hide its UI entry, and move on.
- Delivery after each green phase: publish a release from the CI-built APK (tag `v<phase>.<run_number>`) and run `gh release download` so the APK lands in `/sdcard/Download/open-jarvis-<phase>.apk`. Delete older `open-jarvis*.apk` files from Downloads.
- Report: commit SHA, CI run URL with each job's status, release URL, sha256 of the release asset versus the file in Downloads, `aapt2 dump badging`, a table of modules (restored as original / restored with fixes / minimal and hidden), and what only the user can test by hand. Write `NOT DONE` or `NOT VERIFIED` for anything not run. Never say "works" without a log line.
- Never write the user's token or keystore into any file, commit, or message.
- Never hand-patch APKs. Fix Kotlin errors in source.
