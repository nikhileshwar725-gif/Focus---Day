# Focus Day v0.1

Build: open folder in Android Studio (Ladybug or newer), let Gradle sync, Build > Build APK(s).
Install: copy app/build/outputs/apk/debug/app-debug.apk to phone, allow "install unknown apps".

First run:
1. Open Focus Day, add tasks with +.
2. Settings > Accessibility > Focus Day > turn on.
3. App info > Battery > Unrestricted (stops OEM killers from stopping the service).
4. Test: open Instagram outside 09:00-13:00 (pause screen), then inside (hard block).

Not in v0.1: unfinished roll-over, daily budget, snooze, editable windows, export,
24-hour rule, grayscale mode, browser URL blocking, Shorts/Reels-only blocking.

## No Android Studio? Build in the cloud (GitHub Actions)
1. Create a new GitHub repo (private is fine).
2. Upload all files in this folder, including the hidden `.github` folder.
3. Go to the repo's Actions tab > "Build APK" > Run workflow (or it runs on upload).
4. When green, open the run > Artifacts > download FocusDay-debug-apk.zip.
5. Unzip it, copy app-debug.apk to your phone, install.
