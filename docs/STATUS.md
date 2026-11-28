# Status

## Honest statement

Nothing in this project is "verified". Robolectric runs the real framework and app
classes on the JVM, which catches startup crashes and missing resources, but it is
**not a real device**. Only the person who installs the APK on the phone can
confirm that a build works: launching apps, tapping, typing, voice, OCR,
MediaProjection, the floating overlay over other apps, and the accessibility
service all need real hardware and real permissions.

Every release is published as a pre-release named `UNVERIFIED (not tested on a
device)`.

## What the CI actually proves

| Check | Job | Proves |
|---|---|---|
| `assembleDebug` | Compile, gate and unit test | the Kotlin/Java/resource compile and the APK packages |
| manifest-vs-dex gate (`scripts/gate.sh`) | Compile, gate and unit test | every component declared in the manifest exists in `classes*.dex` |
| `testDebugUnitTest` | Compile, gate and unit test | JVM logic and Robolectric startup smoke tests |
| API 28 / API 34 emulator smoke | Emulator smoke test | launch, 8 s alive, accessibility enabled, still alive, no `FATAL EXCEPTION`/`ClassNotFoundException`, screenshot, `connectedDebugAndroidTest` |

## Environment facts established by tool output

- The hosted runner **does** expose `/dev/kvm` (`crw-rw---- 1 root kvm 10, 232`) and
  `egrep -c '(vmx|svm)' /proc/cpuinfo` returns `4`. The earlier emulator failures
  were a permissions problem (`root:kvm 0660`), now handled by
  `pre-emulator-launch-script` reloading the udev rule and `chmod 666 /dev/kvm`.
- Local builds on the phone are impossible: this is AndroidHarness's Linux shell on
  bionic libc and Gradle's `libnative-platform.so`/`libjansi.so` do not load. All
  compilation happens in GitHub Actions, which does **not** satisfy the
  "built locally on the phone" requirement.

## Signing

A single RSA-4096 JKS keystore was generated once and stored **outside the repo** at
`/storage/emulated/0/.open-jarvis-signing/open-jarvis-release.jks`. Its base64 and
passwords live only as GitHub Actions secrets (`KEYSTORE_BASE64`,
`KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`). Both `debug` and `release`
build types use it, so successive builds install over each other. The first
install of a key-signed build requires uninstalling any older debug-signed build.

## Current blockers

- Emulator jobs: NOT VERIFIED since the KVM permission fix (`8833f69`).
- Every feature listed in `COMPLETE_FEATURE_INVENTORY.md` is at most
  "implemented, compiled, included in the APK" until someone runs it on the phone.
