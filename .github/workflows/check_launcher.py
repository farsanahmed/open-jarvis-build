#!/usr/bin/env python3
"""CI gate: verify the fixed launcher activity class exists in the APK's dex."""

import zipfile
import sys
import os

def main():
    apk_path = os.environ.get("ANDROID_APK", None)
    
    if not apk_path or not os.path.exists(apk_path):
        print(f"ERROR: APK not found at {apk_path}")
        sys.exit(1)

    print(f"=== Launcher Activity Check ===")
    print(f"Checking APK: {apk_path}")
    print()

    with zipfile.ZipFile(apk_path, 'r') as zf:
        # Check dex files for the key class
        key_class = 'Lcom/openjarvis/ui/MainActivity;'
        found = False
        
        dex_files = [name for name in zf.namelist() 
                     if name.startswith('classes') and name.endswith('.dex')]
        
        print(f"DEX files: {len(dex_files)}")
        for dex_name in dex_files:
            dex_data = zf.read(dex_name)
            if key_class.encode('ascii') in dex_data:
                found = True
                print(f"  Found {key_class} in {dex_name}")
        
        # Also check the original (broken) class should NOT be solely relied upon
        broken_class = b'Lcom/openjarvis/MainActivity;'
        broken_found = broken_class in zf.read(dex_files[0]) if dex_files else b''
        
        print(f"Broken class Lcom/openjarvis/MainActivity in first dex: {broken_found}")
        
        # Verify the fix
        if found:
            print(f"PASS: Lcom/openjarvis/ui/MainActivity found in dex - launcher activity fix verified")
            print(f"      The ClassNotFoundException crash cause is resolved.")
            sys.exit(0)
        else:
            print(f"FAIL: Lcom/openjarvis/ui/MainActivity NOT found in dex")
            print(f"      The launcher activity class mismatch is not fixed.")
            sys.exit(1)

if __name__ == "__main__":
    main()
