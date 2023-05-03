package ai.prosa.stt_streaming;

import lombok.Data;

@Data
public class AudioConfiguration {
    /**
     * Automatically resolve the audio configuration.
     */
    public static final AudioConfiguration AUTO = null;
    String format = AudioEncoding.UNSPECIFIED.getFormat();
    Integer channels = null;
    Integer sampleRate = null;
}
