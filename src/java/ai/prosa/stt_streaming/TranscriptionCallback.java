package ai.prosa.stt_streaming;

public interface TranscriptionCallback {

    void onCreated(String jobId);

    void onState(ClientState state);

    void onTranscript(Transcript transcript);

    void onPartialTranscript(PartialTranscript transcript);

    void handleException(Throwable exc);

}
