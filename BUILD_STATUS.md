# Build Status

## Current

- Branch `main`, HEAD `d2ead3c`.
- CI: **Compile, gate and unit test** green on `a1ade83` (run
  <https://github.com/farsanahmed/open-jarvis-build/actions/runs/46136902919>, 3m23s) and on
  `327741c` (run `46134467430`). Later commits are in flight.
- Release: every green build publishes a **pre-release** named
  `UNVERIFIED (not tested on a device)`.
- Delivered APK: `/storage/emulated/0/Download/Open-Jarvis-Final.apk`,
  63 209 163 bytes, sha256 `733d7743155f17baa07005735c5ab21fcfd54bd75aa2d0084e668659c05f3a26`,
  V2 signer certificate `e4e54b4520367ff05cc56a7e41a610e33480854da77d5d56a6e4cd203caec8f9`
  (stable key, so later builds install over it).

## Blockers

1. **Emulator jobs never boot.** Evidence: the KVM diagnostic printed
   `crw-rw---- 1 root kvm 10, 232 /dev/kvm` and `egrep -c '(vmx|svm)' /proc/cpuinfo` = `4`;
   `pre-emulator-launch-script` then produced `crw-rw-rw- 1 root kvm`, and the log shows
   `Starting emulator.` — but adb never registers the device
   (`adb: device 'emulator-5554' not found`, then `The operation was canceled.`). The
   emulator process dies before opening the adb port. NOT VERIFIED, and no further runs are
   being spent on it.
2. **No local build on the phone.** Gradle's `libnative-platform.so` and `libjansi.so`
   do not load under Android bionic, so every APK is compiled by GitHub Actions. This does
   not satisfy the "built locally on this phone" requirement.
3. **Signing:** the keystore lives outside the repo at
   `/storage/emulated/0/.open-jarvis-signing/open-jarvis-release.jks`; its base64 and
   passwords exist only as the `KEYSTORE_BASE64`, `KS_STORE_PW`, `KS_KEY_PW` and
   `KEY_ALIAS` secrets. The first install of a key-signed build needs one uninstall of any
   older debug-signed build.

## Verified by tooling

| Check | Evidence |
|---|---|
| compiles and packages | `assembleDebug` ✓ in CI |
| manifest components exist in dex | `scripts/gate.sh` ✓ in CI |
| JVM + Robolectric smoke | `testDebugUnitTest` ✓ (7 cases) |
| signature | `apksigner verify --print-certs` ✓ |
| launcher activity | `aapt2 dump badging` → `com.openjarvis.ui.MainActivity` |
| APK contents | 18 dex files, 27/27 restored classes present, 7 skill assets |

## Not verified

Everything that needs hardware: launching, tapping, typing, voice, OCR,
MediaProjection, the overlay over other apps, the accessibility service, MCP, skills,
the scheduler and the Termux bridge. See `FEATURE_VERIFICATION_MATRIX.md`.
