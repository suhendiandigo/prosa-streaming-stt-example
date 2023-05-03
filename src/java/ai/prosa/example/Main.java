package ai.prosa.example;

import ai.prosa.stt_streaming.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    // Replace with your API_KEY
    private static final String API_KEY = null;
    private static final String STT_MODEL = "stt-general-online";
    private static final int READ_CHUNK_SIZE = 8000;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No file to transcribe");
            System.exit(1);
        }
        String filename = args[0];

        String label = "Example Job";
        boolean includePartial = false;

        TranscriptionCallback callback = new TranscriptionCallback() {

            @Override
            public void onCreated(String jobId) {
                System.out.println("New job: " + jobId);
            }

            @Override
            public void onState(ClientState state) {
                System.out.println("Current state: " + state);
            }

            @Override
            public void onTranscript(Transcript transcript) {
                System.out.println("Text: " + transcript);
            }

            @Override
            public void onPartialTranscript(PartialTranscript transcript) {
                // Returns the last sentence that is being spoken that is not spoken completely yet.
                System.out.println("Partial: " + transcript);
            }

        };

        StreamingAsrClient client = new StreamingAsrClient(
                API_KEY,
                new FileAudioProvider(filename, READ_CHUNK_SIZE),
                new JobConfiguration(
                        label,
                        STT_MODEL,
                        includePartial,
                        AudioConfiguration.AUTO
                ),
                callback
        );

        Future<Void> future = client.start();
        try {
            // Wait for the async process to finish.
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
