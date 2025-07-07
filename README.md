# 🎵 JavaFX Music Player

Music player built with JavaFX.
Supports MP3, WAV, and M4A formats. Built for local playback with a clean UI and smooth controls.

![Music-Player](https://github.com/user-attachments/assets/a7ebccab-8506-47bb-9d6a-f635bab90c79)
---
## 🚀 Features

- 🎧 Load and play **single audio files**
- 📁 Load **entire folders** as playlists
- ⏯️ Playback controls: **Play, Pause, Stop**
- ⏮️⏭️ **Previous / Next** track navigation
- 🔁 Toggle **Repeat** and **Shuffle**
- 🎚 **Seek bar** and **volume slider**
- 🧠 Remembers last opened file/folder and volume
- 🖤 Dark-themed, modern **JavaFX UI**

---


## 🛠 Setup Instructions

### 1. Download JavaFX SDK

- Go to: [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/)
- Download the **JavaFX SDK for your OS**
- Extract it to a known location, e.g., `C:\javafx-sdk-24`

### 2. Add JavaFX to Project

In IntelliJ:
- Go to **File > Project Structure > Libraries** → Add `C:\javafx-sdk-21\lib`
- Then go to **Run > Edit Configurations** → Add to VM options:

```
--module-path "C:\javafx-sdk-24\lib" --add-modules javafx.controls,javafx.media -Djava.library.path="C:\javafx-sdk-24\bin" --enable-native-access=ALL-UNNAMED
```

*(Replace the path with your JavaFX SDK location)*

## 📃 License

This project is open-source and free to use under the MIT License.

---
