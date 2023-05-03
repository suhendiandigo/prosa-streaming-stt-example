package ai.prosa.stt_streaming;

public enum AudioEncoding {
    UNSPECIFIED(null),
    LINEAR16("s16le"),
    FLAC("flac"),
    MULAW("pcm_mulaw"),
    ALAW("pcm_alaw"),
    OGG_OPUS("opus"),
    WEBM_OPUS("webm");

    private final String format;

    AudioEncoding(final String name) {
        this.format = name;
    }

    public String getFormat() {
        return format;
    }

    public String toString() {
        return format;
    }
}