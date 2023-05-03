# Prosa Streaming STT Example

This is an example Java repository using Prosa Streaming STT.

## Requirements

1. Java 8 or above
2. Gradle (Gradle wrapper is included)
3. An api-key obtainable via [Prosa Console](https://console2.prosa.ai/)

## Running

To start, update the `API_KEY` in the main class to your api-key.

To run the example, provide an audio file (e.g. `audio.wav`) and run the follow command.
```bash
./gradlew run --args="audio.wav"
```
