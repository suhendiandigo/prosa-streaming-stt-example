package ai.prosa.stt_streaming;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobConfiguration {

    String label;
    String model;
    Boolean includePartial;
    AudioConfiguration audio;

}
