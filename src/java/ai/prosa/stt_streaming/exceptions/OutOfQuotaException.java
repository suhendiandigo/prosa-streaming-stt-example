package ai.prosa.stt_streaming.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Raise when the usage has exceeded the quota in the middle of an operation.
 */
@AllArgsConstructor
public class OutOfQuotaException extends InsufficientQuotaException {

    @Getter
    private double timestamp;
    @Getter
    private Integer quotaUsed;

    @Override
    public String getMessage() {
        return String.format("Ran out of quota in the middle of operation at timestamp %f using %d quota", timestamp, quotaUsed);
    }
}
