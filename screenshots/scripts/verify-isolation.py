#!/usr/bin/env python3
"""Check packaged Android artifacts, not only source-level flags."""
import subprocess
import sys
import zipfile
import os
import shutil
from pathlib import Path

sdk = Path(os.environ.get('ANDROID_HOME') or os.environ.get('ANDROID_SDK_ROOT') or Path.home() / 'Library/Android/sdk')
analyzer = shutil.which('apkanalyzer') or str(sdk / 'cmdline-tools/latest/bin/apkanalyzer')
apk = Path('screenshots/android/build/outputs/apk/debug/android-debug.apk')
manifest = subprocess.check_output([str(analyzer), 'manifest', 'print', str(apk)], text=True)
for forbidden in ['android.permission.INTERNET', 'android.permission.CAMERA', 'FirebaseInitProvider', 'com.google.firebase.messaging', 'CrewApplication']:
    assert forbidden not in manifest, f'Unexpected production capability: {forbidden}'
assert 'android:name="android.app.Application"' in manifest
assert 'crew-screenshot' in manifest
assert 'com.kaanf.crew.screenshots' in manifest
print('Screenshot APK: isolated identifier, no Internet permission or production startup providers.')
if len(sys.argv) > 1:
    production = Path(sys.argv[1])
    manifest = subprocess.check_output([str(analyzer), 'manifest', 'print', str(production)], text=True)
    assert 'crew-screenshot' not in manifest
    with zipfile.ZipFile(production) as archive:
        for name in archive.namelist():
            assert 'crew.screenshots.generated' not in name, name
            if name.endswith('.dex'):
                data = archive.read(name)
                for forbidden in [b'crew-screenshot://', b'Lcom/kaanf/crew/screenshots/', b'demo-event-2026-10-24']:
                    assert forbidden not in data, f'Screenshot content in {name}'
    print('Production APK: no screenshot route, classes, fixture IDs or bundled screenshot resources.')
