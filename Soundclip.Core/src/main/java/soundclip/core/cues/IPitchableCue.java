package soundclip.core.cues;

/**
 * A cue that supports pitch adjustment.
 *
 * Ensure that {@link #getSupportedOperations()} has {@link soundclip.core.CueSupportFlags#AUDIO_PITCH} set
 */
public interface IPitchableCue extends ICue
{
    /** Gets the pitch to play this cue back at */
    double getPitch();
    /** Sets the pitch to play this cue back at */
    void setPitch(double pitch);
}
