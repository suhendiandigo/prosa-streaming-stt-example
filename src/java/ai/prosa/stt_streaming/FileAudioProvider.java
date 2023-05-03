package ai.prosa.stt_streaming;

import lombok.Getter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileAudioProvider implements AudioProvider {

    @Getter
    private int chunkSize = 8000;
    @Getter
    private String filename = null;

    public FileAudioProvider(String filename) {
        this.filename = filename;
    }

    public FileAudioProvider(String filename, int chunkSize) {
        this(filename);
        this.chunkSize = chunkSize;
    }

    @Override
    public void provideAudio(InputStream stream) throws IOException {
        File file = new File(filename);
        final byte[] bytes = new byte[chunkSize];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            while (dataInputStream.read(bytes) > 0) {
                stream.sendAudio(bytes);
            }
            dataInputStream.close();
            fileInputStream.close();
        } finally {
            stream.close();
        }
    }
}
