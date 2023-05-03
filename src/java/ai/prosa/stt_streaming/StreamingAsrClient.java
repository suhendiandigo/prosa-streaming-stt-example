package ai.prosa.stt_streaming;

import ai.prosa.stt_streaming.exceptions.*;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.websocket.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


@AllArgsConstructor
class SessionInputStream implements AudioProvider.InputStream {

    private Session session;

    @Override
    public void sendAudio(ByteBuffer bytes) throws IOException {
        session.getBasicRemote().sendBinary(bytes);
    }

    @Override
    public void close() throws IOException {
        session.getBasicRemote().sendBinary(ByteBuffer.allocate(0));
    }
}

public class StreamingAsrClient extends Endpoint implements MessageHandler.Whole<String> {
    public static final String SERVER_URI = "wss://s-api.prosa.ai/v2/speech/stt";

    private Session userSession = null;

    private AudioProvider audio = null;
    private Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private JobConfiguration jobConfig = null;
    private TranscriptionCallback callback = null;

    private String apiKey = null;

    @Getter
    private ClientState state = null;

    private CompletableFuture<Void> future;

    public StreamingAsrClient(String apiKey, AudioProvider audio, JobConfiguration jobConfig, TranscriptionCallback callback) {
        this.apiKey = apiKey;
        this.audio = audio;
        this.jobConfig = jobConfig;
        this.callback = callback;

        future = new CompletableFuture<>();
    }

    private void setException(Throwable exc) {
        future.completeExceptionally(exc);
    }

    public Future<Void> start() {
        try {
            URI uri = URI.create(SERVER_URI);
            ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, cec, uri);
        } catch (DeploymentException | IOException exc) {
            setException(exc);
        }

        return future;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        try {
            userSession = session;

            Map<String, Object> token = new HashMap<>();
            token.put("token", apiKey);
            session.getAsyncRemote().sendText(gson.toJson(token));

            session.getBasicRemote().sendText(gson.toJson(jobConfig));
            session.addMessageHandler(this);
            audio.provideAudio(new SessionInputStream(session));
        } catch (IOException exc) {
            setException(exc);
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            Map data = gson.fromJson(message, Map.class);
            String type = (String) data.get("type");
            String text = null;
            switch (type) {
                case "created":
                    callback.onCreated((String) data.get("id"));
                    break;
                case "status":
                    ClientState clientState = ClientState.valueOf(((String) data.get("status")).toUpperCase());
                    state = clientState;
                    callback.onState(clientState);
                    if (state == ClientState.COMPLETE) {
                        future.complete(null);
                    }
                    break;
                case "partial":
                    text = (String) data.get("transcript");
                    if (text.isEmpty()) {
                        break;
                    }
                    callback.onPartialTranscript(new PartialTranscript(
                            text
                    ));
                    break;
                case "result":
                    text = (String) data.get("transcript");
                    if (text.isEmpty()) {
                        break;
                    }
                    callback.onTranscript(new Transcript(
                            text,
                            (double) data.get("time_start"),
                            (double) data.get("time_end")
                    ));
                    break;
            }
        } catch (Throwable exc) {
            setException(exc);
        }
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;

        Throwable error = null;

        switch (reason.getCloseCode().getCode()) {
            case 1000:
                break;
            case 4000:
                error = new InvalidAuthenticationException();
                break;
            case 4001:
                error = new InvalidConfigurationException();
                break;
            case 4002:
                error = new InvalidModelException();
                break;
            case 4005:
                error = new InsufficientQuotaException();
                break;
            case 4029:
                error = new RateLimitedException();
                break;
            default:
                error = new RuntimeException("Internal Server Error");
                break;
        }

        if (error != null) {
            setException(error);
        }

    }

}
