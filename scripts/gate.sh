#!/usr/bin/env bash
# Manifest-vs-dex gate.
#
# Fails when the manifest declares a component whose class is not present in the
# packaged dex files. This is the check that catches a wrong android:name (for
# example ".MainActivity" when the class really is "com.openjarvis.ui.MainActivity").
set -euo pipefail

APK="${1:?usage: gate.sh <apk>}"
AAPT2="${AAPT2:-${ANDROID_HOME:-}/build-tools/34.0.0/aapt2}"

if [ ! -x "$AAPT2" ]; then
  AAPT2="$(command -v aapt2 || true)"
fi
if [ -z "${AAPT2:-}" ] || [ ! -x "$AAPT2" ]; then
  echo "GATE FAIL: aapt2 not found" >&2
  exit 1
fi

echo "== aapt2 = $AAPT2"
"$AAPT2" version || true

echo "== badging"
"$AAPT2" dump badging "$APK" | grep -E "^(package|launchable-activity|uses-permission|application:|native-code)" || true

WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

"$AAPT2" dump xmltree --file AndroidManifest.xml "$APK" > "$WORK/manifest.txt"

mkdir -p "$WORK/dex"
unzip -o -q "$APK" 'classes*.dex' -d "$WORK/dex"
if ! ls "$WORK"/dex/classes*.dex >/dev/null 2>&1; then
  echo "GATE FAIL: no classes.dex in APK" >&2
  exit 1
fi
echo "== dex files: $(ls "$WORK"/dex | tr '\n' ' ')"

cat "$WORK"/dex/classes*.dex | strings > "$WORK/dex-strings.txt"

python3 - "$WORK/manifest.txt" "$WORK/dex-strings.txt" <<'PY'
import re
import sys

manifest, dex = sys.argv[1], sys.argv[2]
package = None
components = []
stack = []

name_re = re.compile(r'^\s*E: (activity|activity-alias|service|receiver|provider)\b')
attr_re = re.compile(r'^\s*A: android:name\([^)]*\)="([^"]+)"')

with open(manifest, encoding="utf-8", errors="replace") as fh:
    for line in fh:
        m = name_re.match(line)
        if m:
            stack.append(m.group(1))
            continue
        a = attr_re.match(line)
        if a and stack:
            components.append((stack[-1], a.group(1)))
            stack.pop()
            continue
        if line.strip().startswith("E: application"):
            package = None

with open(dex, encoding="utf-8", errors="replace") as fh:
    dex_blob = fh.read()

# Resolve relative names against the manifest package, taken from badging output.
pkg = None
with open(manifest, encoding="utf-8", errors="replace") as fh:
    text = fh.read()
mp = re.search(r'package="([^"]+)"', text)
if mp:
    pkg = mp.group(1)

missing = []
for kind, raw in components:
    cls = raw
    if cls.startswith("."):
        cls = (pkg or "") + cls
    if cls not in dex_blob:
        missing.append((kind, raw, cls))

print("== declared components: %d" % len(components))
for kind, raw, cls in missing:
    print("MISSING  %-8s %s -> %s" % (kind, raw, cls))

if missing:
    print("GATE FAIL: %d manifest component(s) missing from dex" % len(missing))
    sys.exit(1)

print("GATE PASS: every manifest component exists in dex")
PY
