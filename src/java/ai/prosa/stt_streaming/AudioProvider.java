package ai.prosa.stt_streaming;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface AudioProvider {
    void provideAudio(InputStream stream) throws IOException;

    interface InputStream {

        default void sendAudio(byte[] bytes) throws IOException {
            this.sendAudio(ByteBuffer.wrap(bytes));
        }

        void sendAudio(ByteBuffer bytes) throws IOException;

        void close() throws IOException;
    }

}
