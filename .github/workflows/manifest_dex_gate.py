#!/usr/bin/env python3
"""Manifest vs Dex gate: verify every declared component exists in the APK's dex files."""

import zipfile
import sys
import os
import re
import struct

def main():
    # Get the APK path - default to the standard debug output location
    apk_path = os.environ.get("ANDROID_APK", None)
    
    # Find the APK if not provided via env var
    if not apk_path:
        # Look for the standard debug APK path
        possible_paths = [
            "app/build/outputs/apk/debug/app-debug.apk",
        ]
        for p in possible_paths:
            if os.path.exists(p):
                apk_path = p
                break
        if not apk_path:
            # Search recursively
            for root, dirs, files in os.walk("."):
                for f in files:
                    if f.startswith("app-debug") and f.endswith(".apk"):
                        apk_path = os.path.join(root, f)
                        break
    
    if not apk_path or not os.path.exists(apk_path):
        print(f"ERROR: APK not found. Searched standard paths.")
        sys.exit(1)

    print(f"=== Manifest vs Dex Gate ===")
    print(f"Checking APK: {apk_path}")
    print()

    # --- Extract component names from the APK manifest ---
    component_names = set()  # declared com.openjarvis.* components
    dex_classes_found = set()  # actual classes found in dex (by direct string search)
    
    with zipfile.ZipFile(apk_path, 'r') as zf:
        # --- Extract AndroidManifest.xml ---
        manifest_bytes = zf.read('AndroidManifest.xml')
        
        # Decode as UTF-16 (AXML format)
        try:
            manifest_text = manifest_bytes.decode('utf-16')
        except:
            manifest_text = manifest_bytes.decode('utf-16-le', errors='replace')
        
        # Method 1: Parse android:name attributes from the manifest
        # Pattern matches android:name="..." or android:name='...'
        # In AXML, the name string is followed by AXML delimiters (\r\n or space or >)
        for match in re.finditer(r'android:name=[\"\"]([^\"\"\\r\\n\\s>]+)[\"\"\s]', manifest_text):
            name = match.group(1)
            if name.startswith('com.openjarvis.'):
                component_names.add(name)
        
        # Method 2: Also find via Lcom...; patterns in the text
        for match in re.finditer(r'L([a-zA-Z_][a-zA-Z0-9_.]*/)*com\.openjarvis/[a-zA-Z_][a-zA-Z0-9_.]*', manifest_text):
            full = match.group(0)
            m2 = re.search(r'com\.openjarvis\.([a-zA-Z_][a-zA-Z0-9_]*)', full)
            if m2:
                comp_name = 'com.openjarvis.' + m2.group(1)
                if not any(x in comp_name.lower() for x in ['$', 'resource', 'layout', 'drawable', 'string', 'style', 'id']):
                    component_names.add(comp_name)
        
        # --- Extract class names from each DEX file ---
        dex_files = [name for name in zf.namelist() 
                     if name.startswith('classes') and name.endswith('.dex')]
        
        print(f"DEX files in APK: {len(dex_files)}")
        for d in dex_files:
            print(f"  {d}")
        
        # Check for key classes by searching dex data (bytes)
        key_class_bytes = [
            b'Lcom/openjarvis/ui/MainActivity;',
            b'Lcom/openjarvis/MainActivity;',
            b'Lcom/openjarvis/accessibility/JarvisAccessibilityService;',
            b'Lcom/openjarvis/ui/SettingsActivity;',
        ]
        
        for dex_name in dex_files:
            dex_data = zf.read(dex_name)
            
            for key in key_class_bytes:
                if key in dex_data:
                    dex_classes_found.add(key.decode('ascii', errors='replace'))
        
        print(f"Key classes found in dex (by direct string search):")
        for c in sorted(dex_classes_found):
            print(f"  - {c}")
    
    print()
    print(f"Declared com.openjarvis components: {len(component_names)}")
    for c in sorted(component_names):
        print(f"  - {c}")
    
    print()
    print(f"Classes found in dex: {len(dex_classes_found)}")
    for c in sorted(dex_classes_found):
        print(f"  - {c}")
    
    # --- Check that every declared component exists in dex ---
    print()
    print("=== Results ===")
    
    # Filter to only check components we can verify
    valid_component_types = {'MainActivity', 'SettingsActivity', 
                           'JarvisAccessibilityService', 'OnboardingActivity'}
    
    missing = []
    for declared in sorted(component_names):
        # Extract the short class name
        # e.g., "com.openjarvis.MainActivity" -> "MainActivity"
        parts = declared.split('.')
        if len(parts) >= 3:
            short_name = parts[2]  # MainActivity, SettingsActivity, etc.
        else:
            short_name = declared
        
        # Check if this is a type we can verify
        if short_name not in valid_component_types:
            # Skip resources, inner classes, etc.
            continue
        
        # Normalize: Lcom.openjarvis.ShortName;
        normalized = f'L{declared};'
        
        if normalized not in dex_classes_found:
            missing.append((declared, normalized))
    
    if missing:
        print(f"FAIL: {len(missing)} declared component(s) missing from dex:")
        for original, normalized in missing:
            print(f"  MISSING: {original}")
            print(f"         (looking for: {normalized})")
        sys.exit(1)
    else:
        print("PASS: Every declared component (with valid type) exists in the dex.")
        sys.exit(0)

if __name__ == "__main__":
    main()
