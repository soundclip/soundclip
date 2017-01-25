package soundclip.core.cues;

import javafx.util.Duration;

/**
 * A cue that can fade in or out (or both).
 *
 * Ensure that {@link #getSupportedOperations()} has one (or both) of the following flags set:
 * <ul>
 *     <li>{@link soundclip.core.CueSupportFlags#FADE_IN}</li>
 *     <li>{@link soundclip.core.CueSupportFlags#FADE_OUT}</li>
 * </ul>
 *
 * Note you can also specify {@link soundclip.core.CueSupportFlags#FADE} if the implementation
 * supports both fading in and fading out.
 */
public interface IFadeableCue extends ICue
{
    /** Triggers the cue from the start with a gradual fade-in over the specified duration */
    void fadeIn(Duration duration);

    /** Fade the cue to the specified value over the specified duration. This normally alters the volume */
    void fadeTo(double value, Duration over);

    /** If the cue is currently playing, fade it out over the specified duration */
    void fadeOut(Duration duration);
}
