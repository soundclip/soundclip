package soundclip.core.cues;

/**
 * A cue that supports pan adjustment.
 *
 * Ensure that {@link #getSupportedOperations()} has {@link soundclip.core.CueSupportFlags#AUDIO_PAN} set
 */
public interface IPannableCue extends ICue
{
    /** Gets the pan for this cue. Clamped between {@code -1.0} (full left) and {@code 1.0} (full right) */
    double getPan();

    /**
     * Sets the pan for this cue
     *
     * @param pan A value between {@code -1.0} (full left) and {@code 1.0} (full right). If the value is not in this
     *            range, the implementation should clamp this value between {@code -1.0} and {@code 1.0}
     */
    void setPan(double pan);
}
