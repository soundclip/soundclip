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
    /** @return the duration to fade in over when {@link #fadeIn()} is called */
    Duration getFadeInDuration();

    /**
     * Set the duration to fade in over when {@link #fadeIn()} is called
     *
     * @param duration the duration to fade in over
     */
    void setFadeInDuration(Duration duration);

    /** @return the duration to fade out over when {@link #fadeOut()} is called */
    Duration getFadeOutDuration();

    /**
     * Set the duration to fade out over when {@link #fadeOut()} is called
     *
     * @param duration the duration to fade out over
     */
    void setFadeOutDuration(Duration duration);

    /** Triggers the cue from the start with a gradual fade-in over {@link #getFadeInDuration()} */
    void fadeIn();

    /** If the cue is currently playing, fade it out over {@link #getFadeOutDuration()} */
    void fadeOut();
}
