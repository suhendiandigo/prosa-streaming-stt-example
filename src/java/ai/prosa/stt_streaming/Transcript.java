package ai.prosa.stt_streaming;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transcript {
    String text;
    double timeStart;
    double timeEnd;
}
